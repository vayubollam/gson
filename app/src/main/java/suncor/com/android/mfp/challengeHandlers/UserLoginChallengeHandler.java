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
import suncor.com.android.utilities.UserLocalSettings;

/**
 * Created by bahramhaddadi on 2018-11-28.
 */

public class UserLoginChallengeHandler extends SecurityCheckChallengeHandler {
    //    private static String securityCheckName = "UserLogin";
    private int remainingAttempts = -1;
    private String errorMsg = "";
    private Context context;
    private boolean isChallenged = false;
    private boolean firstTry = true;

    private LocalBroadcastManager broadcastManager;

    public UserLoginChallengeHandler(String securityCheckName) {
        super(securityCheckName);
        context = WLClient.getInstance().getContext();
        broadcastManager = LocalBroadcastManager.getInstance(context);

        //Reset the current user
        UserLocalSettings.removeKey(GeneralConstants.SHARED_PREF_USER);

        //Receive login requests
        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    JSONObject credentials = new JSONObject(intent.getStringExtra("credentials"));
                    login(credentials);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new IntentFilter(GeneralConstants.ACTION_LOGIN_SUBMIT_ANSWER));

        //Cancel login process
        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cancel();
            }
        }, new IntentFilter(GeneralConstants.ACTION_LOGIN_CANCELLED));


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
        if(firstTry) {
            firstTry = false;
            cancel();
        } else {
            isChallenged = true;
            try {
                if(UserLocalSettings.isAccountBlicked() < GeneralConstants.ACCOUNT_BLOCKED_TIME) {
                    Intent intent = new Intent();
                    intent.setAction(GeneralConstants.ACTION_USER_ACCOUNT_BLOCKED);
                    intent.putExtra("Unblock_In", UserLocalSettings.isAccountBlicked());
                    broadcastManager.sendBroadcast(intent);
                } else {
                    if (jsonObject.isNull("errorMsg")) {
                        errorMsg = "";
                    } else {
                        errorMsg = jsonObject.getString("errorMsg");
                    }

                    remainingAttempts = jsonObject.getInt("remainingAttempts");
                    Intent intent = new Intent();
                    intent.setAction(GeneralConstants.ACTION_LOGIN_REQUIRED);
                    intent.putExtra("errorMsg", errorMsg);
                    intent.putExtra("remainingAttempts", remainingAttempts);
                    broadcastManager.sendBroadcast(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleFailure(JSONObject error) {
        super.handleFailure(error);
        isChallenged = false;
        Intent intent = new Intent();
        intent.setAction(GeneralConstants.ACTION_LOGIN_FAILURE);
        if (error.isNull("failure")) {
            errorMsg = "Failed to login. Please try again later.";
        } else {
            try {
                errorMsg = error.getString("failure");
                if(errorMsg.equals("Account blocked") && !firstTry) {
                    if(UserLocalSettings.isAccountBlicked() > GeneralConstants.ACCOUNT_BLOCKED_TIME) {
                        UserLocalSettings.markAccountAsBlocked();
                    }
                    intent.putExtra("blocked", String.valueOf(GeneralConstants.ACCOUNT_BLOCKED_TIME - UserLocalSettings.isAccountBlicked()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        intent.putExtra("errorMsg", errorMsg);
        broadcastManager.sendBroadcast(intent);
        Log.d(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, "handleFailure");
        firstTry = false;
    }

    @Override
    public void handleSuccess(JSONObject identity) {
        super.handleSuccess(identity);
        isChallenged = false;
        firstTry = false;
        try {
            //Save the current user
            UserLocalSettings.setString(GeneralConstants.SHARED_PREF_USER, identity.getJSONObject("user").toString());
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
