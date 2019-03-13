package suncor.com.android.ui.home.profile;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Properties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.HomeActivity;
import suncor.com.android.ui.home.common.BaseFragment;


public class ProfileFragment extends BaseFragment {

    public static String PROFILE_FRAGMENT_TAG = "profile";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            Properties properties = new Properties();
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("buildSettings.properties");
            properties.load(inputStream);
            String buildDate = properties.getProperty("buildDate");
            String buildSHA = properties.getProperty("buildSHA");

            TextView lblBuildDate = view.findViewById(R.id.lblBuildDate);
            TextView lblBuildSHA = view.findViewById(R.id.lblBuildSHA);

            lblBuildDate.setText("Build Date: "+buildDate);
            lblBuildSHA.setText("Build SHA: "+buildSHA);
        } catch (Exception e){
            //Show nothing
        }

        view.findViewById(R.id.signout_button).setOnClickListener((v) -> {
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.logout().observe(this, (result) -> {
                if (result.status == Resource.Status.SUCCESS) {
                    ((HomeActivity) getActivity()).openFragment(R.id.menu_home);
                } else {
                    //TODO error handling
                }
            });
        });
    }
}
