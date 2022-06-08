package suncor.com.android.ui.main.profile.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.analytics.profile.ProfileAnalytics;
import suncor.com.android.databinding.FragmentAboutBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.webview.WebDialogFragment;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class AboutFragment extends MainActivityFragment {
    private FragmentAboutBinding binding;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SuncorApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.appVersionButton.setText(getString(R.string.profile_about_app_version) + ' ' + BuildConfig.VERSION_NAME);
        binding.legalButton.setOnClickListener(v -> showDialog(getString(R.string.profile_about_legal_link), getString(R.string.profile_about_legal_header)));
        binding.privacyPolicyButton.setOnClickListener(v -> showDialog(getString(R.string.profile_about_privacy_policy_link), getString(R.string.profile_about_privacy_policy_header)));
        return binding.getRoot();
    }

    void showDialog(String url, String header) {
        WebDialogFragment webDialogFragment = WebDialogFragment.newInstance(url, header);
        webDialogFragment.show(getFragmentManager(), WebDialogFragment.TAG);
        ProfileAnalytics.logInterSiteURL(requireContext(),url);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
    }

    private void goBack() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-about-view";
    }
}
