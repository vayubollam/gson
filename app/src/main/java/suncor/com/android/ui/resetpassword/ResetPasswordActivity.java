package suncor.com.android.ui.resetpassword;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.android.support.DaggerAppCompatActivity;
import okhttp3.HttpUrl;
import suncor.com.android.R;
import suncor.com.android.ui.common.OnBackPressedListener;

public class ResetPasswordActivity extends DaggerAppCompatActivity {
    private Fragment mNavHostFragment;
    private NavController navController;
    private String ID = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mNavHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        ID = getIDFromApplink(appLinkData.toString());

        if(ID != null) {
            Bundle bundle = new Bundle();
            bundle.putString("appLinkData", ID);

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.setGraph(R.navigation.reset_password_nagivation, bundle);
        }
    }

    private String getIDFromApplink(String appLinkData) {
        final HttpUrl url = HttpUrl.parse(appLinkData);
        if (url != null) {
            final String id = url.queryParameter("id");
            return id;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if(ID != null) {
            final Fragment currentFragment = mNavHostFragment.getChildFragmentManager().getFragments().get(0);
            final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
            if (currentFragment instanceof OnBackPressedListener)
                ((OnBackPressedListener) currentFragment).onBackPressed();
            else if (!controller.popBackStack())
                finish();
        }
    }


}
