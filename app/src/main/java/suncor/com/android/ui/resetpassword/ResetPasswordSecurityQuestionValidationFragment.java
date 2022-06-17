package suncor.com.android.ui.resetpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentResetPasswordSecurityQuestionValidationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.resetpassword.ResetPasswordRequest;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.BaseFragment;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.main.MainActivity;

public class ResetPasswordSecurityQuestionValidationFragment extends BaseFragment {

    private ResetPasswordSecurityQuestionValidationViewModel viewModel;
    private FragmentResetPasswordSecurityQuestionValidationBinding binding;
    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    SessionManager sessionManager;
    ResetPasswordRequest request = new ResetPasswordRequest();

    public static ResetPasswordSecurityQuestionValidationFragment newInstance() {
        return new ResetPasswordSecurityQuestionValidationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUserOut();
        String GUID = ResetPasswordSecurityQuestionValidationFragmentArgs.fromBundle(getArguments()).getAppLinkData();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ResetPasswordSecurityQuestionValidationViewModel.class);
        viewModel.setGUID(GUID);
        request.setGUID(GUID);


        viewModel.securityAnswerLiveData.observe(this, stringResource -> {
            switch (stringResource.status) {
                case SUCCESS:
                    request.setSecurityAnswerEncrypted(stringResource.data);
                    ResetPasswordSecurityQuestionValidationFragmentDirections.ActionResetPasswordSecurityQuestionValidationFragmentToResetPasswordFragment action =
                            ResetPasswordSecurityQuestionValidationFragmentDirections.actionResetPasswordSecurityQuestionValidationFragmentToResetPasswordFragment(Objects.requireNonNull(request));
                    Navigation.findNavController(getView()).navigate(action);
                    break;
                case ERROR:
                    if (Objects.requireNonNull(stringResource.message).equalsIgnoreCase(ErrorCodes.ERR_INVALID_SECURITY_ANSWER)) {
                        Alerts.prepareCustomDialogWithTryAgain(getResources().getString(R.string.profile_security_question_wrong_answer_alert_title), null, getContext(), ((dialog, which) -> {
                            binding.questionAnswerInput.setText("");
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(binding.questionAnswerInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                            dialog.dismiss();
                        }), "Reset Password Security Question Validation").show();
                    } else if (Objects.requireNonNull(stringResource.message).equalsIgnoreCase(ErrorCodes.ERR_ACCOUNT_SOFT_LOCK)) {
                        Alerts.prepareCustomDialogOk(getResources().getString(R.string.login_soft_lock_alert_title), getResources().getString(R.string.security_answer_soft_lock_alert_message), getContext(), ((dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }), "Reset Password Security Question Validation").show();
                    } else {
                        Alerts.prepareGeneralErrorDialogWithTryAgain(getContext(), (dialog, which) -> {
                            // TODO: 2022-05-04 log alert_interaction
                            viewModel.validateAndContinue();
                            dialog.dismiss();
                        }, "Reset Password Security Question Validation").show();
                    }
                    break;
                case LOADING:
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
                    break;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password_security_question_validation, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> getActivity().finish());
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.profile_security_question_load_try_again, () -> viewModel.loadQuestion()));

        viewModel.securityQuestionLiveData.observe(this, securityQuestionResource -> {
            switch (securityQuestionResource.status) {
                case SUCCESS:
                    binding.getRoot().post(() -> {
                        viewModel.setQuestionId(securityQuestionResource.data.getId());
                        viewModel.setProfileIdEncrypted(securityQuestionResource.data.getProfileEncrypted());
                        request.setProfileIdEncrypted(securityQuestionResource.data.getProfileEncrypted());
                        binding.questionAnswerInput.getHintTextView().setText(securityQuestionResource.data.getQuestion());
                        binding.questionAnswerInput.getEditText().requestFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(binding.questionAnswerInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                    });
                    break;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void signUserOut() {
        sessionManager.logout();

    }

    @Override
    protected String getScreenName() {
        return "reset-password-security-question-validation";
    }
}