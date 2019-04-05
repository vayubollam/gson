package suncor.com.android.mfp.challengeHandlers;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLoginResponseListener;
import com.worklight.wlclient.api.challengehandler.SecurityCheckChallengeHandler;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.mfp.SessionChangeListener;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.account.Profile;
import suncor.com.android.utilities.Timber;

/**
 * Created by bahramhaddadi on 2018-11-28.
 */

@Singleton
public class UserLoginChallengeHandler extends SecurityCheckChallengeHandler {
    public static final String SECURITY_CHECK_NAME_LOGIN = "UserLogin";
    public static final String SCOPE = "LoggedIn";
    private static final String REMAINING_ATTEMPTS = "attemptsRemaining";
    private static final String FAILURE = "failure";
    private static final String ERROR_CODE = "errorCode";
    private static final String PUBWEB = "pubWeb";
    private static final String RETRY_TIMEOUT = "retryTimeout";

    private boolean isChallenged = false;
    private SessionChangeListener listener;

    @Inject
    SuncorApplication application;

    @Inject
    public UserLoginChallengeHandler() {
        super(SECURITY_CHECK_NAME_LOGIN);
    }

    @Override
    public void handleChallenge(JSONObject jsonObject) {
        Timber.d("Challenge Received");
        Timber.v(jsonObject.toString());
        try {
            isChallenged = true;
            if (jsonObject.has("useCase")) {
                String errorCode = jsonObject.getString(ERROR_CODE);
                JSONObject pubWebResponse = jsonObject.getJSONObject(PUBWEB);

                switch (errorCode) {
                    case ErrorCodes.ERR_ACCOUNT_BAD_PASSWORD:
                        if (pubWebResponse.has(REMAINING_ATTEMPTS)) {
                            int remainingAttempts = pubWebResponse.getInt(REMAINING_ATTEMPTS);
                            listener.onLoginFailed(SigninResponse.wrongCredentials(remainingAttempts));
                        } else {
                            listener.onLoginFailed(SigninResponse.wrongCredentials());
                        }
                        break;
                    case ErrorCodes.ERR_ACCOUNT_SOFT_LOCK:
                        int timeout = pubWebResponse.getInt(RETRY_TIMEOUT);
                        listener.onLoginFailed(SigninResponse.softLocked(timeout));
                        break;
                    case ErrorCodes.ERR_ACCOUNT_HARD_LOCK:
                        listener.onLoginFailed(SigninResponse.hardLocked());
                        break;
                    case ErrorCodes.ERR_PASSWORD_CHANGE_REQUIRED:
                        listener.onLoginFailed(SigninResponse.passwordReset());
                        break;
                    default:
                        listener.onLoginFailed(SigninResponse.generalFailure());
                }
            } else {
                //Which means the token is either invalid or has expired
                Timber.d("Challenge without a useCase, user either is not logged in, or token expired");
                listener.onTokenInvalid();
            }
        } catch (JSONException e) {
            Timber.e(e, "parsing challenge response failed");
            listener.onLoginFailed(SigninResponse.generalFailure());
        }
    }

    @Override
    public void handleFailure(JSONObject error) {
        super.handleFailure(error);
        Timber.d("Handle failure");
        Timber.v(error.toString());

        isChallenged = false;
        listener.onLoginFailed(SigninResponse.generalFailure());
    }

    @Override
    public void handleSuccess(JSONObject identity) {
        Timber.d("handle success");
        super.handleSuccess(identity);
        isChallenged = false;
        try {
            //Save the current user
            String profile = identity.getJSONObject("user").getString("attributes");
            listener.onLoginSuccess(new Gson().fromJson(profile, Profile.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void login(JSONObject credentials) {
        if (isChallenged) {
            submitChallengeAnswer(credentials);
        } else {
            WLAuthorizationManager.getInstance().login(SECURITY_CHECK_NAME_LOGIN, credentials, new WLLoginResponseListener() {
                @Override
                public void onSuccess() {
                    Timber.d("Login Preemptive Success");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    //TODO handle failures related to connection issues
                    Timber.d("Login Preemptive Failure");
                }
            });
        }
    }

    @Override
    public void cancel() {
        isChallenged = false;
        super.cancel();
    }

    public void setSessionChangeListener(SessionChangeListener sessionManager) {
        listener = sessionManager;
    }
}
