package suncor.com.android.mfp.challengeHandlers;

import android.content.Context;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLoginResponseListener;
import com.worklight.wlclient.api.challengehandler.SecurityCheckChallengeHandler;

import org.json.JSONException;
import org.json.JSONObject;

import suncor.com.android.mfp.SessionChangeListener;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Profile;
import suncor.com.android.utilities.Timber;

/**
 * Created by bahramhaddadi on 2018-11-28.
 */

public class UserLoginChallengeHandler extends SecurityCheckChallengeHandler {
    public static final String SECURITY_CHECK_NAME_LOGIN = "UserLogin";
    public static final String SCOPE = "LoggedIn";
    private static final String REMAINING_ATTEMPTS = "remainingAttempts";
    private static final String FAILURE = "failure";
    private String errorMsg = "";
    private boolean isChallenged = false;
    private SessionManager sessionManager;
    private SessionChangeListener listener;


    public UserLoginChallengeHandler(String securityCheckName) {
        super(securityCheckName);
        Context context = WLClient.getInstance().getContext();
        sessionManager = SessionManager.getInstance();
        sessionManager.setChallengeHandler(this);
    }

    public static UserLoginChallengeHandler createAndRegister() {
        UserLoginChallengeHandler challengeHandler = new UserLoginChallengeHandler(SECURITY_CHECK_NAME_LOGIN);
        WLClient.getInstance().registerChallengeHandler(challengeHandler);
        return challengeHandler;
    }


    @Override
    public void handleChallenge(JSONObject jsonObject) {
        Timber.d( "Challenge Received");
        isChallenged = true;
        try {
            if (!sessionManager.isAccountBlocked()) {
                int remainingAttempts = jsonObject.getInt(REMAINING_ATTEMPTS);
                listener.onLoginRequired(remainingAttempts);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Timber.e( "handle challenge failed, " + jsonObject);
            listener.onLoginFailed(e.getMessage());
        }
    }

    @Override
    public void handleFailure(JSONObject error) {
        Timber.d( "Handle failure");
        super.handleFailure(error);
        isChallenged = false;
        try {
            errorMsg = error.getString(FAILURE);
            if (errorMsg.equals("Account blocked")) {
                if (!sessionManager.isAccountBlocked()) {
                    sessionManager.markAccountAsBlocked();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listener.onLoginFailed(errorMsg);
    }

    @Override
    public void handleSuccess(JSONObject identity) {
        Timber.d( "handle success");
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
                    Timber.d( "Login Preemptive Success");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    //TODO handle failures related to connection issues
                    Timber.d( "Login Preemptive Failure");
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
