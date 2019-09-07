package suncor.com.android.ui.main.profile;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentProfileBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.utilities.AnalyticsUtils;


public class ProfileFragment extends BottomNavigationFragment {
    private FragmentProfileBinding binding;
    private ProfileSharedViewModel profileSharedViewModel;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileSharedViewModel = ViewModelProviders.of(getActivity()).get(ProfileSharedViewModel.class);
        profileSharedViewModel.alertObservable.observe(getActivity(), event -> {
            ProfileSharedViewModel.Alert alert = event.getContentIfNotHandled();
            if (alert != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                if (alert.title != -1) {
                    dialog.setTitle(alert.title);
                    AnalyticsUtils.logEvent(getContext(), "error_log", new Pair<>("errorMessage",getString(alert.title)));

                }
                if (alert.message != -1) {
                    dialog.setMessage(alert.message);
                }
                if (alert.positiveButton != -1) {
                    dialog.setPositiveButton(alert.positiveButton, (i, w) -> {
                        if (alert.positiveButtonClick != null) {
                            alert.positiveButtonClick.run();
                        }
                        i.dismiss();
                    });
                }
                if (alert.negativeButton != -1) {
                    dialog.setNegativeButton(alert.negativeButton, (i, w) -> {
                        if (alert.negativeButtonClick != null) {
                            alert.negativeButtonClick.run();
                        }
                        i.dismiss();
                    });
                }
                dialog.show();
            }
        });

        profileSharedViewModel.toastObservable.observe(this, event -> {
            Integer message = event.getContentIfNotHandled();
            if (message != null) {
                SuncorToast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        binding.getRoot().post(() -> {
            int fullScreenHeight = binding.getRoot().getHeight();
            binding.headerLayout.getLayoutParams().height = fullScreenHeight / 2;
            binding.headerLayout.requestLayout();
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sessionManager.getProfile() == null) {
            return;
        }
        String fullName = capitalize(sessionManager.getProfile().getFirstName()) + " " + capitalize(sessionManager.getProfile().getLastName());
        binding.fullNameOutput.setText(fullName);
        binding.emailOutput.setText(sessionManager.getProfile().getEmail());
        initBuild();

        binding.signoutButton.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.profil_sign_out_alert_title))
                    .setPositiveButton(getString(R.string.profil_sign_out_dialog_positive_button), (dialog, which) -> {
                        signUserOut();
                    })
                    .setNegativeButton(getString(R.string.profil_sign_out_dialog_negative_button), ((dialog, which) -> dialog.dismiss()));
            builder.create().show();

        });
        binding.getHelpButton.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_profile_tab_to_FAQFragment));
        binding.transactionButton.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_profile_tab_to_transactionsFragment));
        binding.personalInformationsButton.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_profile_tab_to_personalInfoFragment));
        binding.preferencesButton.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_profile_tab_to_preferencesFragment));
        binding.aboutButton.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_profile_tab_to_aboutFragment));
        binding.addressButton.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_profile_tab_to_addressFragment));
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-navigation-list";
    }

    private void signUserOut() {
        binding.signOutPB.setVisibility(View.VISIBLE);
        sessionManager.logout().observe(this, (result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                binding.signOutPB.setVisibility(View.GONE);
                Navigation.findNavController(getView()).navigate(R.id.home_tab);

                AnalyticsUtils.logEvent(getContext(), "logout");
            } else if (result.status == Resource.Status.ERROR) {
                binding.signOutPB.setVisibility(View.GONE);
                Alerts.prepareGeneralErrorDialog(getActivity()).show();
            }
        });

    }

    public String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public void initBuild() {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("buildSettings.properties");
            properties.load(inputStream);
            String buildDate = properties.getProperty("buildDate");
            String buildSHA = properties.getProperty("buildSHA");

            TextView lblBuildDate = getView().findViewById(R.id.lblBuildDate);
            TextView lblBuildSHA = getView().findViewById(R.id.lblBuildSHA);

            lblBuildDate.setText("Build Date: " + buildDate);
            lblBuildSHA.setText("Build SHA: " + buildSHA);
        } catch (Exception e) {
            //Show nothing
        }
    }
}
