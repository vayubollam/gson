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

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentSecurityQuestionValidationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class SecurityQuestionValidationFragment extends MainActivityFragment {

    private SecurityQuestionValidationViewModel mViewModel;
    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentSecurityQuestionValidationBinding binding;

    public static SecurityQuestionValidationFragment newInstance() {
        return new SecurityQuestionValidationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(SecurityQuestionValidationViewModel.class);
        mViewModel.securityQuestion.observe(this, securityQuestionResource -> {
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_security_question_validation, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.transactions_try_again, () -> mViewModel.loadQuestion()));
        return binding.getRoot();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
