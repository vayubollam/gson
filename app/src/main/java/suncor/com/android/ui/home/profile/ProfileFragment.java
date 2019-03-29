package suncor.com.android.ui.home.profile;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import dagger.android.support.AndroidSupportInjection;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.home.HomeActivity;
import suncor.com.android.ui.home.common.BaseFragment;


public class ProfileFragment extends BaseFragment {

    public static String PROFILE_FRAGMENT_TAG = "profile";
    ProgressBar signOutBP;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signOutBP = getView().findViewById(R.id.sign_out_PB);

        try {
            Properties properties = new Properties();
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("buildSettings.properties");
            properties.load(inputStream);
            String buildDate = properties.getProperty("buildDate");
            String buildSHA = properties.getProperty("buildSHA");

            TextView lblBuildDate = view.findViewById(R.id.lblBuildDate);
            TextView lblBuildSHA = view.findViewById(R.id.lblBuildSHA);

            lblBuildDate.setText("Build Date: " + buildDate);
            lblBuildSHA.setText("Build SHA: " + buildSHA);
        } catch (Exception e) {
            //Show nothing
        }

        view.findViewById(R.id.signout_button).setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.profil_sign_out_alert_title))
                    .setPositiveButton(getString(R.string.profil_sign_out_dialog_positive_button), (dialog, which) -> {
                        signUserOut();
                    })
                    .setNegativeButton(getString(R.string.profil_sign_out_dialog_negative_button), ((dialog, which) -> dialog.dismiss()));
            builder.create().show();

        });
    }

    private void signUserOut() {
        signOutBP.setVisibility(View.VISIBLE);
        sessionManager.logout().observe(this, (result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                signOutBP.setVisibility(View.GONE);
                ((HomeActivity) getActivity()).openFragment(R.id.menu_home);
            } else if (result.status == Resource.Status.ERROR) {
                signOutBP.setVisibility(View.GONE);
                Alerts.prepareGeneralErrorDialog(getActivity()).show();
            }
        });
    }
}
