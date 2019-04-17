package suncor.com.android.ui.home;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.common.SessionAwareActivity;
import suncor.com.android.ui.home.dashboard.DashboardFragment;
import suncor.com.android.ui.home.profile.ProfileFragment;
import suncor.com.android.ui.home.stationlocator.StationsFragment;

import static suncor.com.android.ui.home.dashboard.DashboardFragment.DASHBOARD_FRAGMENT_TAG;

public class HomeActivity extends SessionAwareActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String LOGGED_OUT_EXTRA = "logged_out_extra";
    public static final int LOGGED_OUT_DUE_CONFLICTING_LOGIN = 0;
    public static final int LOGGED_OUT_DUE_INACTIVITY = 1;

    private BottomNavigationView bottom_navigation;

    @Inject
    SuncorApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getIntent().hasExtra(LOGGED_OUT_EXTRA)) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setPositiveButton("OK", null);
            adb.setTitle(R.string.alert_signed_out_title);

            if (getIntent().getIntExtra(LOGGED_OUT_EXTRA, -1) == LOGGED_OUT_DUE_CONFLICTING_LOGIN) {
                adb.setMessage(getString(R.string.alert_signed_out_conflicting_login));
            } else {
                adb.setMessage(getString(R.string.alert_signed_out_inactivity));
            }
            adb.show();
        }


        bottom_navigation = findViewById(R.id.bottom_navigation);

        bottom_navigation.setOnNavigationItemSelectedListener(this);
        View mainDivider = findViewById(R.id.mainDivider);
        if (!application.isSplashShown()) {
            Animation animslideUp = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
            animslideUp.setDuration(500);
            bottom_navigation.startAnimation(animslideUp);
            mainDivider.startAnimation(animslideUp);
            application.setSplashShown(true);
        }

        openFragment(R.id.menu_home);

        if (!isLoggedIn()) {
            bottom_navigation.getMenu().findItem(R.id.menu_profile).setVisible(false);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        openFragment(menuItem.getItemId());
        return true;
    }


    @Override
    protected void onLogout() {
        super.onLogout();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onLoginStatusChanged();
            }
        }

        bottom_navigation.getMenu().findItem(R.id.menu_profile).setVisible(false);
    }

    @Override
    protected void onLoginSuccess() {
        super.onLoginSuccess();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onLoginStatusChanged();
            }
        }

        bottom_navigation.getMenu().findItem(R.id.menu_profile).setVisible(true);
    }

    public void openFragment(@IdRes int menuItemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
        if (fragment != null) {
            fragmentTransaction.detach(fragment);
        }

        switch (menuItemId) {
            case R.id.menu_home:
                fragment = fragmentManager.findFragmentByTag(DASHBOARD_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentTransaction.attach(fragment);
                } else {
                    fragment = new DashboardFragment();
                    fragmentTransaction.add(R.id.frame_layout_home, fragment, DASHBOARD_FRAGMENT_TAG);
                }
                break;
            case R.id.menu_stations:
                fragment = fragmentManager.findFragmentByTag(StationsFragment.STATIONS_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentTransaction.attach(fragment);
                } else {
                    fragment = new StationsFragment();
                    fragmentTransaction.add(R.id.frame_layout_home, fragment, StationsFragment.STATIONS_FRAGMENT_TAG);
                }
                break;
            case R.id.menu_profile:
                fragment = fragmentManager.findFragmentByTag(ProfileFragment.PROFILE_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentTransaction.attach(fragment);
                } else {
                    fragment = new ProfileFragment();
                    fragmentTransaction.add(R.id.frame_layout_home, fragment, ProfileFragment.PROFILE_FRAGMENT_TAG);
                }
                break;
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
        bottom_navigation.getMenu().findItem(menuItemId).setChecked(true);
    }
}
