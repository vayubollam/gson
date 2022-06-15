package suncor.com.android.ui.main.profile.info;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FragmentPersonalInfoBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.ConnectionUtil;
import suncor.com.android.utilities.SuncorPhoneNumberTextWatcher;


public class PersonalInfoFragment extends MainActivityFragment {
    private FragmentPersonalInfoBinding binding;
    private PersonalInfoViewModel viewModel;
    private ProfileSharedViewModel profileSharedViewModel;
    public static final String PERSONAL_INFO_FRAGMENT = "personal_info_fragment";
    public static final String EMAIL_EXTRA = "email_extra";
    private boolean hasCleared = false;


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


        viewModel.bottomSheetAlertObservable.observe(this, event -> {
            ProfileSharedViewModel.Alert alert = event.getContentIfNotHandled();
            if (alert != null) {
                ModalDialog dialog = new ModalDialog();
                dialog.setCancelable(false);
                dialog.setFormName("Update Personal Information");
                AnalyticsUtils.logEvent(getContext(), "error_log", new Pair<>("errorMessage", getString(alert.title)),
                        new Pair<>("formName","Update Personal Information"));

                dialog.setTitle(getString(alert.title))
                        .setMessage(getString(alert.message))
                        .setCenterButton(getString(alert.positiveButton), (v) -> {
                            alert.positiveButtonClick.run();
                            dialog.dismiss();
                        })
                        .setRightButton(getString(alert.negativeButton), (v) -> {
                            alert.negativeButtonClick.run();
                            dialog.dismiss();
                        })
                        .show(getFragmentManager(), ModalDialog.TAG);
            }
        });

        profileSharedViewModel.alertObservable.observe(getActivity(), event -> {
            ProfileSharedViewModel.Alert alert = event.getContentIfNotHandled();
            if (alert != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                if (alert.title != -1) {
                    dialog.setTitle(alert.title);
                    AnalyticsUtils.logEvent(getContext(), "error_log", new Pair<>("errorMessage", getString(alert.title)),
                            new Pair<>("formName","Update Personal Information"));

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
            if (viewModel.isDuplicateEmail) {
                binding.emailInput.getEditText().requestFocus();
            } else {
                binding.emailInput.getEditText().clearFocus();
            }
        });

        viewModel.isPasswordLoading.observe(this, isLoading -> {
            if (isLoading) {
                hideKeyboard();
            }
            binding.passwordInput.getEditText().clearFocus();
        });


        viewModel.navigateToProfile.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                goBack();
            }
        });
        viewModel.navigateToSignIn.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                if (viewModel.getEmail() != null) {
                    intent.putExtra(EMAIL_EXTRA, viewModel.getEmail());
                }
                startActivity(intent);
                Navigation.findNavController(getView()).navigate(R.id.home_tab);
            }
        });

        viewModel.callEvent.observe(this, event -> {
            Integer customerCareStringRes = event.getContentIfNotHandled();
            if (customerCareStringRes != null) {
                callCostumerSupport(getString(customerCareStringRes));
            }
        });

        viewModel.focusWithError.observe(this, event -> {
            if (event.getContentIfNotHandled() != null){
                binding.emailInput.setError(true);
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
        binding.passwordInput.getPasswordToggle().setVisibility(View.INVISIBLE);
        binding.phoneInput.getEditText().setOnFocusChangeListener((v, f) -> onFocusChange(binding.phoneInput, f));
        binding.emailInput.getEditText().setOnFocusChangeListener((v, f) -> onFocusChange(binding.emailInput, f));
        binding.passwordInput.getEditText().setOnFocusChangeListener((v, f) -> onFocusChange(binding.passwordInput, f));
        binding.emailInput.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        AnalyticsUtils.logEvent(this.getContext(),"form_start", new Pair<>("formName","Update Personal Information"));
        binding.emailInput.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                AnalyticsUtils.logEvent(this.getContext(), "form_complete", new Pair<>("formName","Update Personal Information"));
                viewModel.save(ConnectionUtil.haveNetworkConnection(getContext()));
                return true;
            }
            return false;
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-personal-information-view";
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
        }
        if (view == binding.emailInput) {
            viewModel.getEmailInputField().setHasFocus(hasFocus);
        }
        if (view == binding.passwordInput) {
            viewModel.getPasswordField().setHasFocus(hasFocus);
            if (!hasCleared) {
                binding.passwordInput.getEditText().setText("");
                binding.passwordInput.getPasswordToggle().setVisibility(View.VISIBLE);
                hasCleared = true;
            }
        }
        if (hasFocus) {
            scrollToView(view);
        }

    }

    private void scrollToView(View view) {
        binding.scrollView.postDelayed(() -> {
            int viewYPosition = view.getTop();
            int scrollPosition;
            if (view == binding.passwordInput) {
                scrollPosition = viewYPosition;
            } else {
                int halfHeight = binding.scrollView.getHeight() / 2 - view.getHeight() / 2;
                scrollPosition = Math.max(viewYPosition - halfHeight, 0);
            }
                binding.scrollView.smoothScrollTo(0, scrollPosition);
        }, 400);
    }


    private void callCostumerSupport(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);

        AnalyticsUtils.logEvent(getContext(), "tap_to_call", new Pair<>("phoneNumberTapped", phoneNumber));
    }
}
