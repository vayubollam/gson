package suncor.com.android.ui.login;

import static suncor.com.android.analytics.login.LoginAnalyticsKt.FORM_NAME_LOGIN_FORCE_NEW_PASSWORD;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.analytics.login.CreatePasswordAnalytics;
import suncor.com.android.databinding.FragmentCreatePasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.BaseFragment;

public class CreatePasswordFragment extends BaseFragment {

    private static final String EMAIL_EXTRA = "email";
    private static final String ENCRYPTED_EMAIL_EXTRA = "encrypted_email";

    @Inject
    ViewModelFactory viewModelFactory;
    private CreatePasswordViewModel viewModel;
    private FragmentCreatePasswordBinding binding;

    public static CreatePasswordFragment newInstance(String email, String encryptedEmail) {
        CreatePasswordFragment fragment = new CreatePasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EMAIL_EXTRA, email);
        bundle.putString(ENCRYPTED_EMAIL_EXTRA, encryptedEmail);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CreatePasswordFragment() {
        //do nothing
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CreatePasswordViewModel.class);
        viewModel.setEmail(getArguments().getString(EMAIL_EXTRA));
        viewModel.setEmailEncrypted(getArguments().getString(ENCRYPTED_EMAIL_EXTRA));

        viewModel.api.observe(this, (r) -> {
            if (r.status == Resource.Status.LOADING) {
                binding.passwordInput.getEditText().clearFocus();
                hideKeyboard();
            } else if (r.status == Resource.Status.SUCCESS) {
                // TODO: 2022-03-29 Update with userId and buildNumber
                CreatePasswordAnalytics.logResetPwd(requireContext(),"","");

                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else if (r.status == Resource.Status.ERROR) {
                if (ErrorCodes.ERR_PASSWORD_DUPLICATED.equals(r.message)) {

                    CreatePasswordAnalytics.logInvalidPwdError(requireContext());
                    CreatePasswordAnalytics.logInvalidPwdAlertShown(requireContext(),getString(R.string.login_create_password_duplicated_alert_title));

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle(R.string.login_create_password_duplicated_alert_title);
                    alertBuilder.setMessage(R.string.login_create_password_duplicated_alert_message);
                    alertBuilder.setPositiveButton(R.string.ok, ((dialog, which) -> {
                        CreatePasswordAnalytics.logAlertInteraction(requireContext(),
                                getString(R.string.login_create_password_duplicated_alert_title)+"("+getString(R.string.login_create_password_duplicated_alert_message)+")"
                                ,getString(R.string.ok)
                        );
                        binding.passwordInput.setText("");
                        dialog.dismiss();
                    }));
                    alertBuilder.show();
                } else {
                    Dialog dialog = Alerts.prepareGeneralErrorDialog(getActivity(), FORM_NAME_LOGIN_FORCE_NEW_PASSWORD);
                    dialog.setOnDismissListener(dialogInterface -> getActivity().finish());
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CreatePasswordAnalytics.logScreenNameClass(requireContext(),CreatePasswordAnalytics.SCREEN_NAME_FORCE_PASSWORD_CHANGE,this.getClass().getSimpleName());
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePasswordBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener((v) -> {
            hideKeyboard();
            getFragmentManager().popBackStack();
        });
        binding.mainLayout.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_APPEARING);
        binding.getRoot().post(() -> {
            binding.passwordInput.getEditText().requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.passwordInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
        });

        return binding.getRoot();
    }
}
