package suncor.com.android.mfp;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLAccessTokenListener;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLogoutResponseListener;
import com.worklight.wlclient.auth.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.utilities.Timber;
import suncor.com.android.utilities.UserLocalSettings;

@Singleton
public class SessionManager implements SessionChangeListener {

    public static final int LOCK_TIME_MINUTES = 15;
    public static final int LOGIN_ATTEMPTS = 5;
    private static final String SHARED_PREF_USER = "com.ibm.suncor.user";
    private static final String ACCOUNT_BLOCKED_DATE = "com.ibm.suncor.account.blocked.date";
    private final UserLocalSettings userLocalSettings;
    private Profile profile;
    private AccountState accountState;
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

    @Inject
    public SessionManager(UserLoginChallengeHandler challengeHandler, WLAuthorizationManager authorizationManager, UserLocalSettings userLocationSettings) {
        this.challengeHandler = challengeHandler;
        challengeHandler.setSessionChangeListener(this);
        this.authorizationManager = authorizationManager;
        this.userLocalSettings = userLocationSettings;
        String profileString = userLocationSettings.getString(SHARED_PREF_USER);
        if (profileString != null && !profileString.isEmpty()) {
            profile = new Gson().fromJson(profileString, Profile.class);
            accountState = AccountState.REGULAR_LOGIN;
        }
    }

    public LiveData<Resource<Boolean>> logout() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        authorizationManager.logout(UserLoginChallengeHandler.SECURITY_CHECK_NAME_LOGIN, new WLLogoutResponseListener() {
            @Override
            public void onSuccess() {
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
        loginObservable = new MutableLiveData<>();
        loginOngoing = true;
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("email", name);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        challengeHandler.login(credentials);

        return loginObservable;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void checkLoginState() {
        Timber.d("Checking login status");
        authorizationManager.obtainAccessToken(UserLoginChallengeHandler.SCOPE, new WLAccessTokenListener() {
            @Override
            public void onSuccess(AccessToken accessToken) {
                Timber.d("Access token received, user is logged in");
                loginState.postValue(LoginState.LOGGED_IN);
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                //TODO handle this according to errors, an error due to connection error shouldn't clear login state
                Timber.w("Access token cannot be retrieved");
                Timber.w(wlFailResponse.toString());
                loginState.postValue(LoginState.LOGGED_OUT);
                setProfile(null);
            }
        });
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
            return loginState.getValue() == LoginState.LOGGED_IN;
        } else {
            //when the app is just being launched, we want to assume that it was logged in if we have a cached profile
            return profile != null;
        }
    }

    public Profile getProfile() {
        return profile;
    }

    private void setProfile(Profile profile) {
        if (profile == null) {
            this.profile = null;
            accountState = null;
            userLocalSettings.removeKey(SHARED_PREF_USER);
        } else {
            this.profile = profile;
            userLocalSettings.setString(SHARED_PREF_USER, new Gson().toJson(profile));
            accountState = AccountState.REGULAR_LOGIN;
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
    public void onLoginSuccess(Profile profile) {
        Timber.d("login succeeded");
        if (!profile.equals(this.profile)) {
            Timber.d("user's email: " + profile.getEmail());
            setProfile(profile);
            if (loginObservable != null) {
                loginObservable.postValue(Resource.success(SigninResponse.SUCCESS));
            }
            loginState.postValue(LoginState.LOGGED_IN);
            loginOngoing = false;
        }
    }

    @Override
    public void onLoginRequired(int remainingAttempts) {
        Timber.d("login challenged, remaining attempts: " + remainingAttempts);
        setProfile(null);
        if (loginObservable != null) {
            loginObservable.postValue(Resource.error(remainingAttempts + "", SigninResponse.CHALLENGED));
        }
        loginState.postValue(LoginState.LOGGED_OUT);

        if (!loginOngoing) {
            cancelLogin();
        }
    }

    @Override
    public void onLoginFailed(String error) {
        Timber.d("login failed, cause: " + error);
        setProfile(null);
        if (loginObservable != null) {
            loginObservable.postValue(Resource.error(error, SigninResponse.FAILED));
        }
        loginState.postValue(LoginState.LOGGED_OUT);
        loginOngoing = false;
    }

    public enum LoginState {
        LOGGED_IN, LOGGED_OUT
    }

    public enum SigninResponse {
        SUCCESS, CHALLENGED, FAILED
    }

    public enum AccountState {
        JUST_ENROLLED, REGULAR_LOGIN
    }
}
