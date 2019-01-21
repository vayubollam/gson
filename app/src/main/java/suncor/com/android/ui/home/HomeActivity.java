package suncor.com.android.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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

        selectedFragment = new DashboardFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frame_layout_home, selectedFragment);
        transaction.addToBackStack(null);
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.commit();
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
        FragmentManager fm = getSupportFragmentManager();

        if (selectedFragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(selectedFragment);
            ft.commit();
        }
        switch (menuItem.getItemId()) {
            case R.id.menu_home: {
                menuItem.setChecked(true);
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.findFragmentByTag(DashboardFragment.DASHBOARD_FRAGMENT_TAG) != null) {
                    ft.show(fm.findFragmentByTag(DashboardFragment.DASHBOARD_FRAGMENT_TAG));
                } else {
                    selectedFragment = new DashboardFragment();
                    ft.add(R.id.frame_layout_home, selectedFragment, DashboardFragment.DASHBOARD_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    //fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                ft.commit();
                break;

            }
            case R.id.menu_stations: {
                menuItem.setChecked(true);
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.findFragmentByTag(StationsFragment.STATIONS_FRAGMENT_TAG) != null) {
                    ft.show(fm.findFragmentByTag(StationsFragment.STATIONS_FRAGMENT_TAG));
                } else {
                    selectedFragment = new StationsFragment();
                    ft.add(R.id.frame_layout_home, selectedFragment, StationsFragment.STATIONS_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    //fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                ft.commit();
                break;

            }
            default: {

            }


        }

        return true;
    }


    public void hideBottomNavigation() {
        bottom_navigation.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
