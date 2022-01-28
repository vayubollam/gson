package suncor.com.android.ui.main.profile;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
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
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment;
import suncor.com.android.utilities.AnalyticsUtils;


public class ProfileFragment extends MainActivityFragment implements OnBackPressedListener {
    private FragmentProfileBinding binding;
    private ProfileSharedViewModel profileSharedViewModel;
    private float appBarElevation;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        profileSharedViewModel = ViewModelProviders.of(requireActivity()).get(ProfileSharedViewModel.class);
        profileSharedViewModel.alertObservable.observe(requireActivity(), event -> {
            ProfileSharedViewModel.Alert alert = event.getContentIfNotHandled();
            if (alert != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                if (alert.title != -1) {
                    dialog.setTitle(alert.title);
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error, new Pair<>(AnalyticsUtils.Param.errorMessage, getString(alert.title)),
                            new Pair<>(AnalyticsUtils.Param.formName, "My petro points Account Navigation List"));

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
    public void onDestroy() {
        profileSharedViewModel.toastObservable.removeObservers(requireActivity());
        profileSharedViewModel.alertObservable.removeObservers(requireActivity());
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int[] headerLocation = new int[2];
            int[] appBarLocation = new int[2];

            binding.header.getLocationInWindow(headerLocation);
            binding.appBar.getLocationInWindow(appBarLocation);
            int appBarBottom = appBarLocation[1] + binding.appBar.getMeasuredHeight();
            int headerBottom = headerLocation[1];

            if (headerBottom <= appBarBottom) {
                binding.appBar.setTitle(binding.header.getText());
                ViewCompat.setElevation(binding.appBar, appBarElevation);
                binding.appBar.findViewById(R.id.collapsed_title).setAlpha(Math.min(1, (float) (appBarBottom - headerBottom) / 100));
            } else {
                binding.appBar.setTitle("");
                ViewCompat.setElevation(binding.appBar, 0);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sessionManager.getProfile() == null) {
            return;
        }
        binding.emailOutput.setText(sessionManager.getProfile().getEmail());
        initBuild();

        binding.signoutButton.setOnClickListener((v) -> {
            AnalyticsUtils.logEvent(requireActivity().getApplicationContext(), "alert",
                    new Pair<>("alertTitle", getString(R.string.profil_sign_out_alert_title)+"()"),
                    new Pair<>("formName","My petro points Account Navigation List")
            );
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.profil_sign_out_alert_title))
                    .setPositiveButton(getString(R.string.profil_sign_out_dialog_positive_button), (dialog, which) -> {
                        AnalyticsUtils.logEvent(requireActivity().getApplicationContext(), "alert_interaction",
                                new Pair<>("alertTitle", getString(R.string.profil_sign_out_alert_title)+"()"),
                                new Pair<>("alertSelection",getString(R.string.profil_sign_out_dialog_positive_button)),
                                new Pair<>("formName","My petro points Account Navigation List")
                        );
                        signUserOut();
                    })
                    .setNegativeButton(getString(R.string.profil_sign_out_dialog_negative_button), ((dialog, which) -> {
                        AnalyticsUtils.logEvent(requireActivity().getApplicationContext(), "alert_interaction",
                                new Pair<>("alertTitle", getString(R.string.profil_sign_out_alert_title)+"()"),
                                new Pair<>("alertSelection",getString(R.string.profil_sign_out_dialog_negative_button)),
                                new Pair<>("formName","My petro points Account Navigation List")
                        );
                        dialog.dismiss();
                    }));
            builder.create().show();

        });
        binding.getHelpButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_FAQFragment));
        binding.transactionButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_transactionsFragment));
        binding.personalInformationsButton.setOnClickListener(v -> {
            AnalyticsUtils.logEvent(getContext(), "form_start", new Pair<>("formName", "Update Personal Information"));
            if (profileSharedViewModel.getEcryptedSecurityAnswer() != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_personalInfoFragment);
            } else {
                ProfileFragmentDirections.ActionProfileTabToSecurityQuestionValidationFragment2 action = ProfileFragmentDirections.actionProfileTabToSecurityQuestionValidationFragment2(PersonalInfoFragment.PERSONAL_INFO_FRAGMENT);
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
        binding.preferencesButton.setOnClickListener(v -> {
            AnalyticsUtils.logEvent(getContext(),"form_start", new Pair<>("formName","Change Preferences"));
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_preferencesFragment);
        });
        binding.aboutButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_aboutFragment));
        binding.addressButton.setOnClickListener(v -> {
            AnalyticsUtils.logEvent(getContext(),"form_start", new Pair<>("formName","Update Address"));
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_addressFragment);
           /* if (profileSharedViewModel.getEcryptedSecurityAnswer() != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_profile_tab_to_addressFragment);
            } else {
                ProfileFragmentDirections.ActionProfileTabToSecurityQuestionValidationFragment2 action = ProfileFragmentDirections.actionProfileTabToSecurityQuestionValidationFragment2(AddressFragment.ADDRESS_FRAGMENT);
                Navigation.findNavController(requireView()).navigate(action);
            }*/
        });
        binding.appBar.setNavigationOnClickListener(v -> goBack());
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
                Navigation.findNavController(requireView()).navigate(R.id.home_tab);

                AnalyticsUtils.logEvent(getContext(), "logout");
            } else if (result.status == Resource.Status.ERROR) {
                AnalyticsUtils.logEvent(this.getContext(), AnalyticsUtils.Event.formError,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, getString(R.string.msg_e001_title)),
                        new Pair<>(AnalyticsUtils.Param.formName, "My petro points Account Navigation List"));
                binding.signOutPB.setVisibility(View.GONE);
                Alerts.prepareGeneralErrorDialog(getActivity(), "My petro points Account Navigation List").show();
            }
        });

    }

    public String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }


    public void initBuild() {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = requireActivity().getAssets();
            InputStream inputStream = assetManager.open("buildSettings.properties");
            properties.load(inputStream);
            String buildDate = properties.getProperty("buildDate");
            String buildSHA = properties.getProperty("buildSHA");

            TextView lblBuildDate = requireView().findViewById(R.id.lblBuildDate);
            TextView lblBuildSHA = requireView().findViewById(R.id.lblBuildSHA);

            lblBuildDate.setText("Build Date: " + buildDate);
            lblBuildSHA.setText("Build SHA: " + buildSHA);
        } catch (Exception e) {
            //Show nothing
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        profileSharedViewModel.setEcryptedSecurityAnswer(null);
        Navigation.findNavController(requireView()).popBackStack();
    }
}
