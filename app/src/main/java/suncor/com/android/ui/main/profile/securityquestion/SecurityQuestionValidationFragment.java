package suncor.com.android.ui.main.profile.securityquestion;

import android.content.Context;
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
import suncor.com.android.databinding.FragmentSecurityQuestionValidationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.ui.main.profile.address.AddressFragment;
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment;

import static androidx.navigation.Navigation.findNavController;

public class SecurityQuestionValidationFragment extends MainActivityFragment {

    private SecurityQuestionValidationViewModel mViewModel;
    @Inject
    ViewModelFactory viewModelFactory;
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
                        Navigation.findNavController(getView()).navigate(R.id.action_securityQuestionValidationFragment_to_personalInfoFragment);
                    } else if (AddressFragment.ADDRESS_FRAGMENT.equalsIgnoreCase(destination)) {
                        Navigation.findNavController(getView()).navigate(R.id.action_securityQuestionValidationFragment_to_addressFragment);
                    }
                    break;
                case ERROR:
                    if (Objects.requireNonNull(stringResource.message).equalsIgnoreCase(ErrorCodes.ERR_INVALID_SECURITY_ANSWER)) {
                        Alerts.prepareCustomDialogWithTryAgain(getResources().getString(R.string.profile_security_question_wrong_answer_alert_title), null, getContext(), ((dialog, which) -> {
                            binding.questionAnswerInput.setText("");
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(binding.questionAnswerInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                            dialog.dismiss();
                        })).show();
                    } else {
                        Alerts.prepareGeneralErrorDialogWithTryAgain(getContext(), (dialog, which) -> {
                            mViewModel.validateAndContinue();
                            dialog.dismiss();
                        }).show();
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
        return binding.getRoot();
    }

    private void goBack() {
        findNavController(getView()).popBackStack();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
