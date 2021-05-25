package suncor.com.android.ui.main.profile.preferences;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FragmentPreferencesBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.utilities.AnalyticsUtils;

public class PreferencesFragment extends MainActivityFragment {


    private FragmentPreferencesBinding binding;
    private PreferencesViewModel viewModel;
    private ProfileSharedViewModel profileSharedViewModel;
    public static final String PREFERENCES_FRAGMENT = "preferences_fragment";

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SuncorApplication application;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PreferencesViewModel.class);
        profileSharedViewModel = ViewModelProviders.of(getActivity()).get(ProfileSharedViewModel.class);
        viewModel.setProfileSharedViewModel(profileSharedViewModel);

        viewModel.navigateToProfile.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                goBack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        AnalyticsUtils.logEvent(this.getContext(), "form_start", new Pair<>("formName","Change Preferences"));
        return binding.getRoot();
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-preferences-view";
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }
}
