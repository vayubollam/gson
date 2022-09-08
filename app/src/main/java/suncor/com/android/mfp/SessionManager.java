package suncor.com.android.mfp;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLAccessTokenListener;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLogoutResponseListener;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;
import com.worklight.wlclient.auth.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.utilities.ConnectionUtil;
import suncor.com.android.utilities.Consumer;
import suncor.com.android.utilities.SharedPrefsHelper;
import suncor.com.android.utilities.Timber;
import suncor.com.android.utilities.UserLocalSettings;

@Singleton
public class SessionManager implements SessionChangeListener {

    public static final int LOCK_TIME_MINUTES = 30;
    public static final int LOGIN_ATTEMPTS = 6;
    public static final String RETRIEVE_PROFILE_FAILED = "com.ibm.suncor.profile.failed";
    private static final String SHARED_PREF_USER = "com.ibm.suncor.user";
    private static final String ACCOUNT_BLOCKED_DATE = "com.ibm.suncor.account.blocked.date";
    private final UserLocalSettings userLocalSettings;
    private Profile profile;
    private AccountState accountState;
    private String carWashKey;
    private UserLoginChallengeHandler challengeHandler;
    private WLAuthorizationManager authorizationManager;
    private MutableLiveData<Resource<SigninResponse>> loginObservable;
    private MutableLiveData<LoginState> loginState = new MutableLiveData<LoginState>() {
        @Override
        public void postValue(LoginState value) {
            if (value != getValue()) {
                super.postValue(value);
            }
        }

        @Override
        public void setValue(LoginState value) {
            if (value != getValue()) {
                super.setValue(value);
            }
        }
    };

    private boolean loginOngoing = false;

    private SuncorApplication application;
    private Gson gson;

    private int rewardedPoints = -1;
    private SharedPrefsHelper mSharedPrefsHelper;


    @Inject
    public SessionManager(UserLoginChallengeHandler challengeHandler, WLAuthorizationManager authorizationManager,
                          UserLocalSettings userLocationSettings, SuncorApplication application, Gson gson,
                          SharedPrefsHelper sharedPrefsHelper) {
        this.challengeHandler = challengeHandler;
        challengeHandler.setSessionChangeListener(this);
        this.authorizationManager = authorizationManager;
        this.userLocalSettings = userLocationSettings;
        this.application = application;
        this.mSharedPrefsHelper = sharedPrefsHelper;
        this.gson = gson;
    }

    public LiveData<Resource<Boolean>> logout() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        challengeHandler.logout(new WLLogoutResponseListener() {
            @Override
            public void onSuccess() {
                userLocalSettings.setString(UserLocalSettings.RECENTLY_SEARCHED, null);
                mSharedPrefsHelper.deleteSavedData(SharedPrefsHelper.USER_VACUUM_TOGGLE);
                mSharedPrefsHelper.deleteSavedData(SharedPrefsHelper.USER_DONATE_TOGGLE);
                mSharedPrefsHelper.deleteSavedData(SharedPrefsHelper.SETTING_VACUUM_TOGGLE);
                mSharedPrefsHelper.deleteSavedData(SharedPrefsHelper.SETTING_DONATE_TOGGLE);
                setProfile(null);
                accountState = null;
                loginState.postValue(LoginState.LOGGED_OUT);
                result.postValue(Resource.success(true));
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Timber.d("Logout Failure");
                result.postValue(Resource.error(wlFailResponse.getErrorMsg(), false));
            }
        });
        return result;
    }

    public LiveData<Resource<SigninResponse>> login(String name, String password) {
        if (challengeHandler == null) {
            throw new IllegalStateException("Security Challenge Handler not initialized, did you forget to call setChallengeHandler()");
        }
        name = name.replaceAll("\\s", "");
        loginObservable = new MutableLiveData<>();
        loginOngoing = true;
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("email", name);
            credentials.put("password", password);
            loginObservable.postValue(Resource.loading());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        challengeHandler.login(credentials);

        return loginObservable;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<AutoLoginState> checkLoginState() {
        Timber.d("Checking login status");
        MutableLiveData<AutoLoginState> autoLoginState = new MutableLiveData<>();

        //MFP doesn't notify listener about some errors, we need to add a separate timer to timeout after 35 seconds in case we don't get a response before
        TimerTask timeoutTask = new TimerTask() {

            @Override
            public void run() {
                Timber.d("obtain Access Token call timed out without notification from MFP");
                setProfile(null);
                loginState.postValue(LoginState.LOGGED_OUT);
                autoLoginState.postValue(AutoLoginState.LOGGED_OUT);
            }
        };

        Timer timer = new Timer();
        timer.schedule(timeoutTask, 30000);


        authorizationManager.obtainAccessToken(UserLoginChallengeHandler.SCOPE, new WLAccessTokenListener() {
            @Override
            public void onSuccess(AccessToken accessToken) {
                Timber.d("Got access token, retrieving profile :" + accessToken.getValue());
                timer.cancel();
                retrieveProfile(
                        (profile) -> {
                            setProfile(profile);

                            loginState.postValue(LoginState.LOGGED_IN);
                            autoLoginState.postValue(AutoLoginState.LOGGED_IN);
                        },
                        (error) -> {
                            if (ConnectionUtil.haveNetworkConnection(application)) {
                                autoLoginState.postValue(AutoLoginState.ERROR);
                            } else {
                                autoLoginState.postValue(AutoLoginState.LOGGED_OUT);
                            }
                            setProfile(null);
                            loginState.postValue(LoginState.LOGGED_OUT);
                        }
                );
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                timer.cancel();
                Timber.d("Cannot retrieve an access token\n" + wlFailResponse.toString());
                setProfile(null);
                loginState.postValue(LoginState.LOGGED_OUT);
                autoLoginState.postValue(AutoLoginState.LOGGED_OUT);
            }
        });

        return autoLoginState;
    }

    private void retrieveProfile(Consumer<Profile> onSuccess, Consumer<WLFailResponse> onError) {
        try {
            //We use 30s as the timeout for this request, as it times out a lot, and causes logout
            WLResourceRequest request = new WLResourceRequest(new URI("/adapters/suncor/v7/rfmp-secure/profiles"), WLResourceRequest.GET, SuncorApplication.PROTECTED_SCOPE);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Profile received, response: " + wlResponse.getResponseText());
                    Profile profile = gson.fromJson(wlResponse.getResponseText(), Profile.class);
                    Boolean userVacuumToggle = (profile != null && profile.toggleFeature != null) ? profile.toggleFeature.isVacuumScanBarcode() : null;
                    Boolean userDonateToggle = (profile != null && profile.toggleFeature != null) ? profile.toggleFeature.isDonatePetroPoints() : null;
                    if (userVacuumToggle != null)
                        mSharedPrefsHelper.put(SharedPrefsHelper.USER_VACUUM_TOGGLE, userVacuumToggle);

                    if(userDonateToggle != null)
                        mSharedPrefsHelper.put(SharedPrefsHelper.USER_DONATE_TOGGLE, userDonateToggle);

                    onSuccess.accept(profile);
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.w("Profile cannot be retrieved");
                    Timber.w(wlFailResponse.toString());
                    onError.accept(wlFailResponse);
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e);
        }
    }

    public void cancelLogin() {
        loginOngoing = false;
        try {
            challengeHandler.cancel();
        } catch (Exception ignored) {
        }
    }

    public void setChallengeHandler(UserLoginChallengeHandler userLoginChallengeHandler) {
        challengeHandler = userLoginChallengeHandler;
        challengeHandler.setSessionChangeListener(this);
    }

    public boolean isUserLoggedIn() {
        if (loginState.getValue() != null) {
            return loginState.getValue() == LoginState.LOGGED_IN && profile != null;
        } else {
/*
            //when the app is just being launched, we want to assume that it was logged in if we have a cached profile
            return profile != null;
*/
            //when the app is just being launched, start with guest mode, until we check login status: revisit this
            return false;
        }
    }

    public Profile getProfile() {
        return profile;
    }

    private void setProfile(Profile profile) {
        if (profile == null) {
            this.profile = null;
            accountState = null;
        } else {
            this.profile = profile;
            if (accountState == null) {
                accountState = AccountState.REGULAR_LOGIN;
            }
        }
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public void markAccountAsBlocked() {
        userLocalSettings.setLong(ACCOUNT_BLOCKED_DATE, System.currentTimeMillis());
    }

    public boolean isAccountBlocked() {
        return remainingTimeToUnblock() > 0;
    }

    /**
     * @return in milliseconds
     */
    public long remainingTimeToUnblock() {
        long lockTime = userLocalSettings.getLong(ACCOUNT_BLOCKED_DATE);
        return (lockTime + (LOCK_TIME_MINUTES + 1) * 60 * 1000) - System.currentTimeMillis();
    }

    @Override
    public void onLoginSuccess(Profile account) {
        Timber.d("login succeeded");
        Timber.d("user's email: " + account.getEmail());

        AtomicBoolean isRetrievingProfile = new AtomicBoolean(false);

        if (!loginOngoing || isRetrievingProfile.get()) {
            return;
        }

        userLocalSettings.setString(UserLocalSettings.RECENTLY_SEARCHED, null);


        isRetrievingProfile.set(true);

        retrieveProfile((profile) -> {
            isRetrievingProfile.set(false);
            if (loginOngoing) {
                loginOngoing = false;

                setProfile(profile);
                loginState.postValue(LoginState.LOGGED_IN);
                if (loginObservable != null) {
                    loginObservable.postValue(Resource.success(SigninResponse.success()));
                }
            }
        }, (error) -> {
            isRetrievingProfile.set(false);
            if (loginOngoing) {
                loginOngoing = false;

                setProfile(null);
                loginState.postValue(LoginState.LOGGED_OUT);
                if (loginObservable != null) {
                    loginObservable.postValue(Resource.success(SigninResponse.generalFailure()));
                }
            }
        });
    }

    @Override
    public void onLoginFailed(SigninResponse response) {
        Timber.d("login failed, cause: " + response.getStatus().name());
        setProfile(null);
        if (loginObservable != null) {
            loginObservable.postValue(Resource.success(response));
        }
        loginState.postValue(LoginState.LOGGED_OUT);

        //cancel login only if it's not an intermediate response, or the handler was started by MFP without login flow
        if (response.getStatus() != SigninResponse.Status.WRONG_CREDENTIALS || !loginOngoing) {
            cancelLogin();
        }
    }

    @Override
    public void onTokenInvalid() {
        if (profile != null) {
            setProfile(null);
            Timber.d("token expired, navigate to main");
            Intent intent = new Intent(application, MainActivity.class);
            //intent.putExtra(MainActivity.LOGGED_OUT_EXTRA, MainActivity.LOGGED_OUT_DUE_INACTIVITY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        }
        cancelLogin();
    }

    public Boolean getVacuumToggle() {
        Boolean settingsVacuumToggle = null;
        if (mSharedPrefsHelper.checkHasKey(SharedPrefsHelper.SETTING_VACUUM_TOGGLE)) {
            settingsVacuumToggle = mSharedPrefsHelper.get(SharedPrefsHelper.SETTING_VACUUM_TOGGLE, false);
        }
        Boolean userVacuumToggle = mSharedPrefsHelper.get(SharedPrefsHelper.USER_VACUUM_TOGGLE, false);
        if (settingsVacuumToggle != null) {
            if (settingsVacuumToggle) {
                return true;
            } else {
                return userVacuumToggle;
            }
        } else {
            return null;
        }
    }

    public Boolean getDonateToggle() {
        Boolean settingsDonateToggle = null;
        if (mSharedPrefsHelper.checkHasKey(SharedPrefsHelper.SETTING_DONATE_TOGGLE)) {
            settingsDonateToggle = mSharedPrefsHelper.get(SharedPrefsHelper.SETTING_DONATE_TOGGLE, false);
        }
        Boolean userDonateToggle = mSharedPrefsHelper.get(SharedPrefsHelper.USER_DONATE_TOGGLE, false);
        if (settingsDonateToggle != null) {
            if (settingsDonateToggle) {
                return true;
            }
                return userDonateToggle;
        }
            return null;
    }

    public void setRewardedPoints(int rewardedPoints) {
        this.rewardedPoints = rewardedPoints;
    }

    public String getCarWashKey() {
        return carWashKey;
    }

    public void setCarWashKey(String carWashKey) {
        this.carWashKey = carWashKey;
    }

    //Only when accountState is JUST_ENROLLED
    public int getRewardedPoints() {
        return rewardedPoints;
    }

    public enum LoginState {
        LOGGED_IN, LOGGED_OUT
    }

    public enum AutoLoginState {
        LOGGED_IN, LOGGED_OUT, ERROR
    }

    public enum AccountState {
        JUST_ENROLLED, REGULAR_LOGIN
    }

    public UserLocalSettings getUserLocalSettings() {
        return userLocalSettings;
    }

    public SharedPrefsHelper getSharedPrefsHelper() {
        return mSharedPrefsHelper;
    }
}
