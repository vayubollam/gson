package suncor.com.android.ui;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;
import suncor.com.android.fragments.HomeFragment;
import suncor.com.android.fragments.StationsFragment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottom_navigation;
    private Fragment selectedFragment;

    //request code for requesting permissions
    private int requestCode=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottom_navigation=findViewById(R.id.bottom_navigation);

        bottom_navigation.setOnNavigationItemSelectedListener(this);

        //check for runtime permission
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                 checkforPermission();
    }


    private void checkforPermission() {

     String[] permissionList=new String[] {
             Manifest.permission.ACCESS_FINE_LOCATION,   Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
             Manifest.permission.INTERNET
     };
        List<String> listPermissionsNeeded=new ArrayList<>();

        for(String permission : permissionList)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(this.checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(permission);
            }
        }
        if(!listPermissionsNeeded.isEmpty())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),requestCode);
            }


    }


    //when user clicks on one of the bottom navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
           selectedFragment=null;
        switch (menuItem.getItemId())
        {
            case R.id.menu_home:{
                if(!(selectedFragment instanceof HomeFragment)){
                    selectedFragment=new HomeFragment();
                    menuItem.setChecked(true);
                    FragmentManager fm=getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frame_layout_home, selectedFragment);
                    transaction.addToBackStack(null);
                    fm.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();
                }
                break;

            }
            case R.id.menu_stations:{
                if(!(selectedFragment instanceof StationsFragment))
                {
                    selectedFragment=new StationsFragment();
                    menuItem.setChecked(true);
                    FragmentManager fm=getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.frame_layout_home, selectedFragment);
                    transaction.addToBackStack(null);
                    fm.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();

                }
                break;

            }
            default:{

            }


        }

        return true;
    }


    public void hideBottomNavigation()
    {
        bottom_navigation.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
