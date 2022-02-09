package suncor.com.android.ui.main.common;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.Alerts;

@SuppressLint("Registered")
public class SessionAwareActivity extends DaggerAppCompatActivity {

    @Inject
    SessionManager sessionManager;
    private boolean currentLoginStatus;

    private BroadcastReceiver retrieveProfileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Alerts.prepareGeneralErrorDialog(SessionAwareActivity.this, "Session").show();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLoginStatus = sessionManager.isUserLoggedIn();
        sessionManager.getLoginState().observe(this, (state) -> {
            if (state == SessionManager.LoginState.LOGGED_IN) {
                if (!currentLoginStatus) {
                    currentLoginStatus = true;
                    onLoginSuccess();
                }
            } else {
                if (currentLoginStatus) {
                    currentLoginStatus = false;
                    onLogout();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(retrieveProfileReceiver, new IntentFilter(SessionManager.RETRIEVE_PROFILE_FAILED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(retrieveProfileReceiver);
    }

    protected boolean isLoggedIn() {
        return currentLoginStatus;
    }

    protected void requestLogin() {
        //do nothing
    }

    protected void onLoginSuccess() {
        //do nothing
    }

    protected void onLogout() {
        //do nothing
    }

    protected void onLoginFailed() {
        //do nothing
    }
}
