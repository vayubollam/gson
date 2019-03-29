package suncor.com.android.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.common.SessionAwareActivity;
import suncor.com.android.ui.home.dashboard.DashboardFragment;
import suncor.com.android.ui.home.profile.ProfileFragment;
import suncor.com.android.ui.home.stationlocator.StationsFragment;

public class HomeActivity extends SessionAwareActivity implements BottomNavigationView.OnNavigationItemSelectedListener, HasSupportFragmentInjector {
    //request code for requesting permissions
    private final static int PERMISSION_REQUEST_CODE = 1;
    public static final String LOGGED_OUT_EXTRA = "logged_out_extra";
    private BottomNavigationView bottom_navigation;

    @Inject
    DispatchingAndroidInjector<Fragment> injector;

    @Inject
    SuncorApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_home);

        if (getIntent().getBooleanExtra(LOGGED_OUT_EXTRA, false)) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Signed out");
            adb.setMessage("You've been signed out as this account was being used on another device. Please sign-in to continue.");
            adb.setPositiveButton("OK", null);
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
        //check for runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkforPermission();

        openFragment(R.id.menu_home);

        if (!isLoggedIn()) {
            bottom_navigation.getMenu().findItem(R.id.menu_profile).setVisible(false);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkforPermission() {

        String[] permissionList = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        };
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String permission : permissionList) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
            }

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
                fragment = fragmentManager.findFragmentByTag(DashboardFragment.DASHBOARD_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentTransaction.attach(fragment);
                } else {
                    fragment = new DashboardFragment();
                    fragmentTransaction.add(R.id.frame_layout_home, fragment, DashboardFragment.DASHBOARD_FRAGMENT_TAG);
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

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return injector;
    }
}
