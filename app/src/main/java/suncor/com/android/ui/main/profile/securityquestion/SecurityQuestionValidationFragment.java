package suncor.com.android.ui.main.profile.securityquestion;

import static androidx.navigation.Navigation.findNavController;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
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
import suncor.com.android.databinding.FragmentSecurityQuestionValidationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.ui.main.profile.account.AccountDeleteFragment;
import suncor.com.android.ui.main.profile.address.AddressFragment;
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.Constants;

public class SecurityQuestionValidationFragment extends MainActivityFragment {

    private SecurityQuestionValidationViewModel mViewModel;
    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    SessionManager sessionManager;
    private FragmentSecurityQuestionValidationBinding binding;
    private ProfileSharedViewModel sharedViewModel;
    private String destination;

    public static SecurityQuestionValidationFragment newInstance() {
        return new SecurityQuestionValidationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(SecurityQuestionValidationViewModel.class);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(ProfileSharedViewModel.class);
        mViewModel.securityQuestionLiveData.observe(this, securityQuestionResource -> {
            switch (securityQuestionResource.status) {
                case SUCCESS:
                    binding.getRoot().post(() -> {
                        binding.questionAnswerInput.getHintTextView().setText(securityQuestionResource.data.getQuestion());
                        binding.questionAnswerInput.getEditText().requestFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(binding.questionAnswerInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                    });
                    break;

            }
        });
        mViewModel.securityAnswerLiveData.observe(this, stringResource -> {
            switch (stringResource.status) {
                case SUCCESS:
                    sharedViewModel.setEcryptedSecurityAnswer(stringResource.data);
                    if (PersonalInfoFragment.PERSONAL_INFO_FRAGMENT.equalsIgnoreCase(destination)) {
                        AnalyticsUtils.logEvent(getContext(),AnalyticsUtils.Event.FORMSTEP,
                                new Pair<>(AnalyticsUtils.Param.FORMNAME, Constants.UPDATE_PERSONAL_INFORMATION),
                                new Pair<>(AnalyticsUtils.Param.STEPNAME, Constants.ANSWER_SECURITY_QUESTION)
                        );
                        Navigation.findNavController(getView()).navigate(R.id.action_securityQuestionValidationFragment_to_personalInfoFragment);
                    } else if (AddressFragment.ADDRESS_FRAGMENT.equalsIgnoreCase(destination)) {
                        AnalyticsUtils.logEvent(getContext(),AnalyticsUtils.Event.FORMSTEP,
                                new Pair<>(AnalyticsUtils.Param.FORMNAME, Constants.UPDATE_ADDRESS),
                                new Pair<>(AnalyticsUtils.Param.STEPNAME, Constants.ANSWER_SECURITY_QUESTION)
                        );
                        Navigation.findNavController(getView()).navigate(R.id.action_securityQuestionValidationFragment_to_addressFragment);
                    } else if (AccountDeleteFragment.DELETE_ACCOUNT_FRAGMENT.equalsIgnoreCase(destination)){
                        AnalyticsUtils.logEvent(getContext(),AnalyticsUtils.Event.FORMSTEP,
                                new Pair<>(AnalyticsUtils.Param.FORMNAME, Constants.DELETE_ACCOUNT),
                                new Pair<>(AnalyticsUtils.Param.STEPNAME, Constants.ANSWER_SECURITY_QUESTION)
                        );
                        Navigation.findNavController(requireView()).navigate(R.id.action_securityQuestionValidationFragment_to_deleteAccountFragment);
                    }
                    break;
                case ERROR:
                    if (Objects.requireNonNull(stringResource.message).equalsIgnoreCase(ErrorCodes.ERR_INVALID_SECURITY_ANSWER)) {
                        Alerts.prepareCustomDialogWithTryAgain(getResources().getString(R.string.profile_security_question_wrong_answer_alert_title), null, getContext(), ((dialog, which) -> {
                            binding.questionAnswerInput.setText("");
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(binding.questionAnswerInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                            dialog.dismiss();
                        }), "Security Question Validation").show();
                    } else if (Objects.requireNonNull(stringResource.message).equalsIgnoreCase(ErrorCodes.ERR_ACCOUNT_SOFT_LOCK)) {
                        Alerts.prepareCustomDialogOk(getResources().getString(R.string.login_soft_lock_alert_title), getResources().getString(R.string.security_answer_soft_lock_alert_message), getContext(), ((dialog, which) -> {
                            sessionManager.logout().observe(this, (result) -> {
                                if (result.status == Resource.Status.SUCCESS) {
                                    Navigation.findNavController(getView()).navigate(R.id.home_tab);
                                } else if (result.status == Resource.Status.ERROR) {
                                }
                            });
                            dialog.dismiss();
                        }), "Security Question Validation").show();
                    } else {
                        Alerts.prepareGeneralErrorDialogWithTryAgain(getContext(), (dialog, which) -> {
                            // TODO: 2022-05-04 log alert_interaction
                            mViewModel.validateAndContinue();
                            dialog.dismiss();
                        }, "Security Question Validation").show();
                    }
                    break;
                case LOADING:
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
                    break;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        destination = SecurityQuestionValidationFragmentArgs.fromBundle(getArguments()).getDestinationFragment();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_security_question_validation, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.profile_security_question_load_try_again, () -> mViewModel.loadQuestion()));
        binding.questionAnswerInput.getEditText().setOnFocusChangeListener((v, f) -> onFocusChange(binding.questionAnswerInput, f));
        return binding.getRoot();
    }

    private void goBack() {
        findNavController(getView()).popBackStack();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            scrollToView(view);
        }
    }

    private void scrollToView(View view) {
        binding.scrollView.postDelayed(() -> {
            int scrollPosition = view.getTop();
            binding.scrollView.smoothScrollTo(0, scrollPosition);
        }, 400);
    }

    @Override
    protected String getScreenName() {
        return "security-question-validation";
    }
}
