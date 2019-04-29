package suncor.com.android.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.ui.common.KeepStateNavigator;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.common.SessionAwareActivity;

public class HomeActivity extends SessionAwareActivity {
    public static final String LOGGED_OUT_DUE_CONFLICTING_LOGIN = "logged_out_conflict";
    @Inject
    SuncorApplication application;
    private BottomNavigationView bottom_navigation;
    private Fragment navHostFragment;
    private NavController navController;

    private BroadcastReceiver loginConflictReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LOGGED_OUT_DUE_CONFLICTING_LOGIN.equals(intent.getAction())) {
                AlertDialog.Builder adb = new AlertDialog.Builder(HomeActivity.this);
                adb.setPositiveButton("OK", (dialog, which) -> {
                    Intent homeActivityIntent = new Intent(application, HomeActivity.class);
                    homeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.startActivity(homeActivityIntent);
                });
                adb.setTitle(R.string.alert_signed_out_title);
                adb.setMessage(getString(R.string.alert_signed_out_conflicting_login));
                adb.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        View mainDivider = findViewById(R.id.mainDivider);
        if (!application.isSplashShown()) {
            Animation animslideUp = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
            animslideUp.setDuration(500);
            bottom_navigation.startAnimation(animslideUp);
            mainDivider.startAnimation(animslideUp);
            application.setSplashShown(true);
        }

        navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.getNavigatorProvider().addNavigator(new KeepStateNavigator(this, navHostFragment.getFragmentManager(), R.id.nav_host_fragment));
        navController.setGraph(R.navigation.home_nav_graph);

        NavigationUI.setupWithNavController(bottom_navigation, navController);

        if (!isLoggedIn()) {
            bottom_navigation.getMenu().findItem(R.id.profile_tab).setVisible(false);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(loginConflictReceiver, new IntentFilter(LOGGED_OUT_DUE_CONFLICTING_LOGIN));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginConflictReceiver);
    }

    @Override
    protected void onLogout() {
        super.onLogout();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onLoginStatusChanged();
            }
        }

        bottom_navigation.getMenu().findItem(R.id.profile_tab).setVisible(false);
    }

    @Override
    protected void onLoginSuccess() {
        super.onLoginSuccess();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onLoginStatusChanged();
            }
        }

        bottom_navigation.getMenu().findItem(R.id.profile_tab).setVisible(true);
    }

    public NavController getNavController() {
        return navController;
    }
}
