package suncor.com.android.mfp.challengeHandlers;

import android.content.Context;
import android.util.Log;

import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLoginResponseListener;
import com.worklight.wlclient.api.challengehandler.SecurityCheckChallengeHandler;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import suncor.com.android.mfp.SessionChangeListener;
import suncor.com.android.mfp.SessionManager;

/**
 * Created by bahramhaddadi on 2018-11-28.
 */

public class UserLoginChallengeHandler extends SecurityCheckChallengeHandler {
    private static final String REMAINING_ATTEMPTS = "remainingAttempts";
    private static final String FAILURE = "failure";

    public static final String SECURITY_CHECK_NAME_LOGIN = "UserLogin";
    public static final String SCOPE = "LoggedIn";

    private String errorMsg = "";
    private boolean isChallenged = false;
    private SessionManager sessionManager;
    private SessionChangeListener listener;

    private LocalBroadcastManager broadcastManager;

    public UserLoginChallengeHandler(String securityCheckName) {
        super(securityCheckName);
        Context context = WLClient.getInstance().getContext();
        broadcastManager = LocalBroadcastManager.getInstance(context);
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
        Log.d(UserLoginChallengeHandler.class.getSimpleName(), "Challenge Received");
        isChallenged = true;
        try {
            if (!sessionManager.isAccountBlocked()) {
                int remainingAttempts = jsonObject.getInt(REMAINING_ATTEMPTS);
                listener.onLoginRequired(remainingAttempts);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleFailure(JSONObject error) {
        Log.d(UserLoginChallengeHandler.class.getSimpleName(), "Handle failure");
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
        Log.d(UserLoginChallengeHandler.class.getSimpleName(), "handle success");
        super.handleSuccess(identity);
        isChallenged = false;
        try {
            //Save the current user
            listener.onLoginSuccess(identity.getJSONObject("user").toString());
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
                    Log.d(UserLoginChallengeHandler.class.getSimpleName(), "Login Preemptive Success");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    //TODO handle failures related to connection issues
                    Log.d(UserLoginChallengeHandler.class.getSimpleName(), "Login Preemptive Failure");
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
