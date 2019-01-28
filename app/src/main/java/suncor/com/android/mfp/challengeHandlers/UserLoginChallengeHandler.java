package suncor.com.android.mfp.challengeHandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLLoginResponseListener;
import com.worklight.wlclient.api.WLLogoutResponseListener;
import com.worklight.wlclient.api.challengehandler.SecurityCheckChallengeHandler;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import suncor.com.android.GeneralConstants;
import suncor.com.android.mfp.SessionManager;

/**
 * Created by bahramhaddadi on 2018-11-28.
 */

public class UserLoginChallengeHandler extends SecurityCheckChallengeHandler {
    public static final String ERROR_MSG = "errorMsg";
    public static final String REMAINING_ATTEMPTS = "remainingAttempts";
    public static final String FAILURE = "failure";
    private String errorMsg = "";
    private Context context;
    private boolean isChallenged = false;
    private SessionManager sessionManager;

    private LocalBroadcastManager broadcastManager;

    public UserLoginChallengeHandler(String securityCheckName) {
        super(securityCheckName);
        context = WLClient.getInstance().getContext();
        broadcastManager = LocalBroadcastManager.getInstance(context);
        sessionManager = SessionManager.getInstance();

        //Receive logout requests
        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                logout();
            }
        }, new IntentFilter(GeneralConstants.ACTION_LOGOUT));
    }

    public static UserLoginChallengeHandler createAndRegister() {
        UserLoginChallengeHandler challengeHandler = new UserLoginChallengeHandler(GeneralConstants.SECURITY_CHECK_NAME_LOGIN);
        WLClient.getInstance().registerChallengeHandler(challengeHandler);
        return challengeHandler;
    }


    @Override
    public void handleChallenge(JSONObject jsonObject) {
        Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "Challenge Received");
        sessionManager.setUserName(null);
        {
            isChallenged = true;
            try {
                if (sessionManager.isAccountBlocked()) {
                    Intent intent = new Intent();
                    intent.setAction(GeneralConstants.ACTION_USER_ACCOUNT_BLOCKED);
                    broadcastManager.sendBroadcast(intent);
                } else {
                    if (jsonObject.isNull(ERROR_MSG)) {
                        errorMsg = "";
                    } else {
                        errorMsg = jsonObject.getString(ERROR_MSG);
                    }

                    //private static String securityCheckName = "UserLogin";
                    int remainingAttempts = jsonObject.getInt(REMAINING_ATTEMPTS);
                    Intent intent = new Intent();
                    intent.setAction(GeneralConstants.ACTION_LOGIN_REQUIRED);
                    intent.putExtra(ERROR_MSG, errorMsg);
                    intent.putExtra(REMAINING_ATTEMPTS, remainingAttempts);
                    broadcastManager.sendBroadcast(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleFailure(JSONObject error) {
        Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "Handle failure");
        super.handleFailure(error);
        isChallenged = false;
        Intent intent = new Intent();
        intent.setAction(GeneralConstants.ACTION_LOGIN_FAILURE);
        if (error.isNull(FAILURE)) {
            errorMsg = "Failed to login. Please try again later.";
        } else {
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
        }

        intent.putExtra(ERROR_MSG, errorMsg);
        broadcastManager.sendBroadcast(intent);
        Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "handleFailure");
    }

    @Override
    public void handleSuccess(JSONObject identity) {
        super.handleSuccess(identity);
        isChallenged = false;
        try {
            //Save the current user
            sessionManager.setUserName(identity.getJSONObject("user").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.setAction(GeneralConstants.ACTION_LOGIN_SUCCESS);
        broadcastManager.sendBroadcast(intent);
        Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "handleSuccess");
    }

    public void login(JSONObject credentials) {
        if (isChallenged) {
            submitChallengeAnswer(credentials);
        } else {
            WLAuthorizationManager.getInstance().login(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, credentials, new WLLoginResponseListener() {
                @Override
                public void onSuccess() {
                    Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "Login Preemptive Success");

                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "Login Preemptive Failure");
                }
            });
        }
    }


    public void logout() {
        WLAuthorizationManager.getInstance().logout(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, new WLLogoutResponseListener() {
            @Override
            public void onSuccess() {
                sessionManager.setUserName(null);
                Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "Logout Success");
                Intent intent = new Intent();
                intent.setAction(GeneralConstants.ACTION_LOGOUT_SUCCESS);
                broadcastManager.sendBroadcast(intent);
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "Logout Failure");
                Intent intent = new Intent();
                intent.setAction(GeneralConstants.ACTION_LOGOUT_FAILURE);
                broadcastManager.sendBroadcast(intent);
            }
        });
    }
}
