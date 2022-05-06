package suncor.com.android.ui.enrollment;

import android.os.Bundle;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.model.account.Province;
import suncor.com.android.ui.common.OnBackPressedListener;

public class EnrollmentActivity extends DaggerAppCompatActivity {
    private Fragment mNavHostFragment;
    private ArrayList<Province> provinces = new ArrayList<>();

    public ArrayList<Province> getProvinces() {
        return provinces;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        mNavHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        boolean isCome = getIntent().getBooleanExtra("some_argument", false);

        Bundle bundle = new Bundle();
        bundle.putBoolean("some_argument", isCome);

        Navigation.findNavController(this, R.id.nav_host_fragment).setGraph(R.navigation.enrollment_navigation, bundle);



        String[] provincesArray = getResources().getStringArray(R.array.province_names);

        for (String provinceCodeName : provincesArray) {
            String[] nameCode = provinceCodeName.split(";");
            provinces.add(new Province(nameCode[1], nameCode[0], nameCode[2]));
        }
    }

    @Override
    public void onBackPressed() {
        final Fragment currentFragment = mNavHostFragment.getChildFragmentManager().getFragments().get(0);
        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (currentFragment instanceof OnBackPressedListener)
            ((OnBackPressedListener) currentFragment).onBackPressed();
        else if (!controller.popBackStack())
            finish();
    }
}
