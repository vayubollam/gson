package suncor.com.android.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;
import suncor.com.android.ui.home.dashboard.DashboardFragment;
import suncor.com.android.ui.home.stationlocator.StationsFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottom_navigation;
    private Fragment selectedFragment;

    //request code for requesting permissions
    private int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottom_navigation = findViewById(R.id.bottom_navigation);

        bottom_navigation.setOnNavigationItemSelectedListener(this);

        //check for runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkforPermission();

        openFragment(R.id.menu_home);
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
                this.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
            }

    }


    //when user clicks on one of the bottom navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        openFragment(menuItem.getItemId());
        return true;
    }

    private void openFragment(@IdRes int menuItemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }

        switch (menuItemId) {
            case R.id.menu_home:
                fragment = fragmentManager.findFragmentByTag(DashboardFragment.DASHBOARD_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragment = new DashboardFragment();
                    fragmentTransaction.add(R.id.frame_layout_home, fragment, DashboardFragment.DASHBOARD_FRAGMENT_TAG);
                }
                break;
            case R.id.menu_stations:
                fragment = fragmentManager.findFragmentByTag(StationsFragment.STATIONS_FRAGMENT_TAG);
                if (fragment != null) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragment = new StationsFragment();
                    fragmentTransaction.add(R.id.frame_layout_home, fragment, StationsFragment.STATIONS_FRAGMENT_TAG);
                }
                break;
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

}
