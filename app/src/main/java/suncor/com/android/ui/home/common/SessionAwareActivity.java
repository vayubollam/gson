package suncor.com.android.ui.home.common;

import android.annotation.SuppressLint;
import android.os.Bundle;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.mfp.SessionManager;

@SuppressLint("Registered")
public class SessionAwareActivity extends DaggerAppCompatActivity {

    @Inject
    SessionManager sessionManager;
    private boolean currentLoginStatus;


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
        sessionManager.checkLoginState();
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
