package suncor.com.android.ui.main.profile.about;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FragmentAboutBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.webview.WebDialogFragment;
import suncor.com.android.ui.main.common.BaseFragment;

public class AboutFragment extends BaseFragment {
    private FragmentAboutBinding binding;
    public static final String TAG = "WebDialog";

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
        binding.legalButton.setOnClickListener(v -> showDialog(getString(R.string.profile_about_legal_link) , getString(R.string.profile_about_legal_header)));
        binding.privacyPolicyButton.setOnClickListener(v -> showDialog(getString(R.string.profile_about_privacy_policy_link) , getString(R.string.profile_about_privacy_policy_header)));
        return binding.getRoot();
    }
    void showDialog(String url, String header) {
        WebDialogFragment webDialogFragment = WebDialogFragment.newInstance(url, header);
        webDialogFragment.show(getFragmentManager(), TAG);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
