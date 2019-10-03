package suncor.com.android.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.account.Province;
import suncor.com.android.ui.SplashActivity;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.AndroidBug5497Workaround;
import suncor.com.android.ui.common.KeepStateNavigator;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.common.SessionAwareActivity;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.utilities.AnalyticsUtils;

public class MainActivity extends SessionAwareActivity implements OnBackPressedListener {
    public static final String LOGGED_OUT_DUE_CONFLICTING_LOGIN = "logged_out_conflict";
    public static final String LOGGED_OUT_DUE_PASSWORD_CHANGE = "password_change_requires_re_login";
    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    SuncorApplication application;
    private BottomNavigationView bottomNavigation;
    private Fragment navHostFragment;
    private NavController navController;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Province> provinces = new ArrayList<>();
    private MainViewModel mainViewModel;
    private boolean autoLoginFailed = false;

    public ArrayList<Province> getProvinces() {
        return provinces;
    }

    private boolean isProfileTabSelected = false;
    private ProfileSharedViewModel profileSharedViewModel;
    private BroadcastReceiver loginConflictReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LOGGED_OUT_DUE_CONFLICTING_LOGIN.equals(intent.getAction())) {
                AnalyticsUtils.logEvent(application.getApplicationContext(), "error_log", new Pair<>("errorMessage", LOGGED_OUT_DUE_CONFLICTING_LOGIN));
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setPositiveButton(R.string.login_conflict_alert_positive_button, (dialog, which) -> {
                    Intent homeActivityIntent = new Intent(application, MainActivity.class);
                    homeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.startActivity(homeActivityIntent);
                });
                adb.setTitle(getResources().getString(R.string.password_change_re_login_alert_title));
                adb.setMessage(getResources().getString(R.string.pawword_change_re_login_alert_body));
                adb.show();
            }
        }
    };

    private BroadcastReceiver loginChangedPasswordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LOGGED_OUT_DUE_PASSWORD_CHANGE.equals(intent.getAction()) && !autoLoginFailed) {
                navController.navigate(R.id.action_global_home_tab);
                AnalyticsUtils.logEvent(application.getApplicationContext(), "error_log", new Pair<>("errorMessage", LOGGED_OUT_DUE_PASSWORD_CHANGE));
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setPositiveButton(R.string.login_conflict_alert_positive_button, (dialog, which) -> {
                    Intent loginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(loginActivityIntent);
                });
                adb.setTitle(getResources().getString(R.string.password_change_re_login_alert_title));
                adb.setMessage(getResources().getString(R.string.pawword_change_re_login_alert_body));
                adb.show();
            }
            autoLoginFailed = false;
        }
    };

    public NavController getNavController() {
        return navController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the view to full screen, then each fragment will handle its UI and apply it's insets
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        setContentView(R.layout.activity_main);
        AndroidBug5497Workaround.assistActivity(this);
        profileSharedViewModel = ViewModelProviders.of(this).get(ProfileSharedViewModel.class);

        floatingActionButton = findViewById(R.id.action_float_button);
       // floatingActionButton.setVisibility(isLoggedIn()? View.VISIBLE : View.GONE);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.inflateMenu( R.menu.bottom_navigation_menu_signedin ); //testing only DELETE
        //bottomNavigation.inflateMenu(isLoggedIn() ? R.menu.bottom_navigation_menu_signedin : R.menu.bottom_navigation_menu_guest);
        View mainDivider = findViewById(R.id.mainDivider);
        if (!application.isSplashShown()) {
            Animation animslideUp = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
            animslideUp.setDuration(500);
            bottomNavigation.startAnimation(animslideUp);
            mainDivider.startAnimation(animslideUp);
            application.setSplashShown(true);
        }

        navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.getNavigatorProvider().addNavigator(new KeepStateNavigator(this, navHostFragment.getChildFragmentManager(), R.id.nav_host_fragment));
        navController.setGraph(R.navigation.main_nav_graph);

        NavigationUI.setupWithNavController(bottomNavigation, navController);

        mainViewModel.userLoggedOut.observe(this, event -> {
            profileSharedViewModel.setEcryptedSecurityAnswer(null);
            isProfileTabSelected = false;
        });
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            AnalyticsUtils.logEvent(MainActivity.this, "navigation", new Pair<>("actionBarTap", item.getTitle().toString()));
            if (item.getItemId() == R.id.profile_tab) {
                isProfileTabSelected = true;
            }
            if (item.getItemId() != R.id.profile_tab && isProfileTabSelected && profileSharedViewModel.getEcryptedSecurityAnswer() != null) {
                profileSharedViewModel.setEcryptedSecurityAnswer(null);
                isProfileTabSelected = false;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        if (getIntent().hasExtra(SplashActivity.LOGINFAILED) && getIntent().getExtras().getBoolean(SplashActivity.LOGINFAILED, false)) {
            autoLoginFailed = true;
            Alerts.prepareGeneralErrorDialog(this).show();
        }
        String[] provincesArray = getResources().getStringArray(R.array.province_names);

        for (String provinceCodeName : provincesArray) {
            String[] nameCode = provinceCodeName.split(";");
            provinces.add(new Province(nameCode[1], nameCode[0], nameCode[2]));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(loginConflictReceiver, new IntentFilter(LOGGED_OUT_DUE_CONFLICTING_LOGIN));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginChangedPasswordReceiver, new IntentFilter(LOGGED_OUT_DUE_PASSWORD_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginConflictReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginChangedPasswordReceiver);
    }

    @Override
    protected void onLogout() {
        super.onLogout();
        for (Fragment fragment : navHostFragment.getChildFragmentManager().getFragments()) {
            if (fragment instanceof MainActivityFragment) {
                ((MainActivityFragment) fragment).onLoginStatusChanged();
            }
        }

        bottomNavigation.getMenu().clear();
        bottomNavigation.inflateMenu(R.menu.bottom_navigation_menu_guest);
    }

    @Override
    protected void onLoginSuccess() {
        super.onLoginSuccess();
        for (Fragment fragment : navHostFragment.getChildFragmentManager().getFragments()) {
            if (fragment instanceof MainActivityFragment) {
                ((MainActivityFragment) fragment).onLoginStatusChanged();
            }
        }

        bottomNavigation.getMenu().clear();
        bottomNavigation.inflateMenu(R.menu.bottom_navigation_menu_signedin);
    }

    @Override
    public void onBackPressed() {
        boolean backPressedGandled = false;
        List<Fragment> currentFragments = navHostFragment.getChildFragmentManager().getFragments();
        for (Fragment f : currentFragments) {
            if (f instanceof OnBackPressedListener) {
                ((OnBackPressedListener) f).onBackPressed();
                backPressedGandled = true;
            }
        }
        if (!backPressedGandled) {
            super.onBackPressed();
        }


    }
}
