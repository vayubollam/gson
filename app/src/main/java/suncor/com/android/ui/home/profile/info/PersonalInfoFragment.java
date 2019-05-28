package suncor.com.android.ui.home.profile.info;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import afu.org.checkerframework.checker.nullness.qual.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FragmentPersonalInfoBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.profile.ProfileSharedViewModel;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.utilities.SuncorPhoneNumberTextWatcher;


public class PersonalInfoFragment extends BaseFragment {

    private FragmentPersonalInfoBinding binding;
    private PersonalInfoViewModel viewModel;
    private ProfileSharedViewModel profileSharedViewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SuncorApplication application;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PersonalInfoViewModel.class);
        profileSharedViewModel = ViewModelProviders.of(getActivity()).get(ProfileSharedViewModel.class);
        viewModel.setProfileSharedViewModel(profileSharedViewModel);
        viewModel.showSaveButtonEvent.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                binding.setIsEditing(true);
            }
        });
        viewModel.getEmailInputField().getHasFocusObservable().observe(this, event -> {
            if (event.getContentIfNotHandled()) {
                binding.setShowEmailSubcopy(true);
            }
        });

        profileSharedViewModel.alertObservable.observe(getActivity(), event -> {

            ProfileSharedViewModel.Alert alert = event.getContentIfNotHandled();
            if (alert != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                if (alert.title != -1) {
                    dialog.setTitle(alert.title);
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
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                hideKeyboard();
            }
            binding.emailInput.getEditText().clearFocus();
        });

        viewModel.navigateToProfile.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                goBack();
            }
        });
        viewModel.navigateToSignIn.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                Navigation.findNavController(getView()).navigate(R.id.home_tab);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        binding.phoneInput.getEditText().addTextChangedListener(new SuncorPhoneNumberTextWatcher());
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.phoneInput.getEditText().setOnFocusChangeListener((v, f) -> onFocusChange(binding.phoneInput, f));
        binding.emailInput.getEditText().setOnFocusChangeListener((v, f) -> onFocusChange(binding.emailInput, f));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
    }

    private void goBack() {
        hideKeyboard();
        Navigation.findNavController(getView()).popBackStack();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void onFocusChange(View view, boolean hasFocus) {
        if (view == binding.phoneInput) {
            viewModel.getPhoneField().setHasFocus(hasFocus);
        } else if (view == binding.emailInput) {
            viewModel.getEmailInputField().setHasFocus(hasFocus);
        }
        if (hasFocus) {
            binding.scrollView.postDelayed(() -> {
                int viewYPosition = view.getTop();

                int halfHeight = binding.scrollView.getHeight() / 2 - view.getHeight() / 2;
                int scrollPosition = Math.max(viewYPosition - halfHeight, 0);

                binding.scrollView.smoothScrollTo(0, scrollPosition);
            }, 200);
        }
    }
}
