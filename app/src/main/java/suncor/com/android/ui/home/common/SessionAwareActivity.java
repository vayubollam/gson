package suncor.com.android.ui.home.common;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.worklight.wlclient.api.WLAccessTokenListener;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.auth.AccessToken;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;

@SuppressLint("Registered")
public class SessionAwareActivity extends FragmentActivity {

    private SessionManager sessionManager;
    private boolean currentLoginStatus;

    private BroadcastReceiver loginRequiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentLoginStatus) {
                currentLoginStatus = false;
                onLogout();
            }
            requestLogin();
        }
    };

    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!currentLoginStatus) {
                currentLoginStatus = true;
                onLoginSuccess();
            }
        }
    };

    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (currentLoginStatus) {
                currentLoginStatus = false;
                onLogout();
            }
        }
    };

    private BroadcastReceiver errorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onLoginFailed();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = SessionManager.getInstance();
        currentLoginStatus = sessionManager.isUserLoggedIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sessionManager.getUserName() != null) {
            WLAuthorizationManager.getInstance().obtainAccessToken(UserLoginChallengeHandler.SCOPE, new WLAccessTokenListener() {
                @Override
                public void onSuccess(AccessToken accessToken) {
                    Log.d(this.getClass().getSimpleName(), "User is logged in");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d(this.getClass().getSimpleName(), "User is not logged in");
                }
            });
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(loginRequiredReceiver, new IntentFilter(SessionManager.ACTION_LOGIN_REQUIRED));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, new IntentFilter(SessionManager.ACTION_LOGIN_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, new IntentFilter(SessionManager.ACTION_LOGOUT_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(errorReceiver, new IntentFilter(SessionManager.ACTION_LOGIN_FAILURE));
        if (currentLoginStatus != sessionManager.isUserLoggedIn()) {
            currentLoginStatus = sessionManager.isUserLoggedIn();
            if (currentLoginStatus) {
                onLoginSuccess();
            } else {
                onLogout();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginRequiredReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(errorReceiver);
    }

    protected boolean isLoggedIn() {
        return currentLoginStatus;
    }

    protected void requestLogin() {
    }

    protected void onLoginSuccess() {
    }

    protected void onLogout() {
    }

    protected void onLoginFailed() {
    }
}
