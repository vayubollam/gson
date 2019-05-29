package suncor.com.android.ui.home.profile.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import afu.org.checkerframework.checker.nullness.qual.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FragmentPreferencesBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.profile.ProfileSharedViewModel;

public class PreferencesFragment extends BaseFragment {


    private FragmentPreferencesBinding binding;
    private PreferencesViewModel viewModel;
    private ProfileSharedViewModel profileSharedViewModel;

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
        return binding.getRoot();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }
}
