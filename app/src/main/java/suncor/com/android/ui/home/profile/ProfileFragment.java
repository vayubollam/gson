package suncor.com.android.ui.home.profile;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentProfileBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.home.HomeActivity;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.profile.help.FAQFragment;


public class ProfileFragment extends BaseFragment {
    FragmentProfileBinding binding;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
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

            lblBuildDate.setText("Build Date: " + buildDate);
            lblBuildSHA.setText("Build SHA: " + buildSHA);
        } catch (Exception e) {
            //Show nothing
        }

        binding.signoutButton.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.profil_sign_out_alert_title))
                    .setPositiveButton(getString(R.string.profil_sign_out_dialog_positive_button), (dialog, which) -> {
                        signUserOut();
                    })
                    .setNegativeButton(getString(R.string.profil_sign_out_dialog_negative_button), ((dialog, which) -> dialog.dismiss()));
            builder.create().show();

        });
        binding.getHelpButton.setOnClickListener(v -> {
            launchGetHelpFragment();
        });
    }

    private void signUserOut() {
        binding.signOutPB.setVisibility(View.VISIBLE);
        sessionManager.logout().observe(this, (result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                binding.signOutPB.setVisibility(View.GONE);
                ((HomeActivity) getActivity()).getNavController().navigate(R.id.home_tab);
            } else if (result.status == Resource.Status.ERROR) {
                binding.signOutPB.setVisibility(View.GONE);
                Alerts.prepareGeneralErrorDialog(getActivity()).show();
            }
        });

    }

    public void launchGetHelpFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment getHelpFragment = fragmentManager.findFragmentByTag(FAQFragment.FAQ_FRAGMENT_TAG);
        if (getHelpFragment != null && getHelpFragment.isAdded()) {
            return;
        }
        getHelpFragment = new FAQFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        ft.add(android.R.id.content, getHelpFragment, FAQFragment.FAQ_FRAGMENT_TAG);
        ft.addToBackStack(null);
        ft.commit();
    }
}
