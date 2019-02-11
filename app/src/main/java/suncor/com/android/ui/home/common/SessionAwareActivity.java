package suncor.com.android.ui.home.common;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import suncor.com.android.mfp.SessionManager;

@SuppressLint("Registered")
public class SessionAwareActivity extends FragmentActivity {

    private SessionManager sessionManager;
    private boolean currentLoginStatus;

//    private BroadcastReceiver loginRequiredReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (currentLoginStatus) {
//                currentLoginStatus = false;
//                onLogout();
//            }
//            requestLogin();
//        }
//    };
//
//    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (!currentLoginStatus) {
//                currentLoginStatus = true;
//                onLoginSuccess();
//            }
//        }
//    };
//
//    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (currentLoginStatus) {
//                currentLoginStatus = false;
//                onLogout();
//            }
//        }
//    };

//    //private BroadcastReceiver errorReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            onLoginFailed();
//        }
//    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = SessionManager.getInstance();
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

    @Override
    protected void onStop() {
        super.onStop();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginRequiredReceiver);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(errorReceiver);
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
