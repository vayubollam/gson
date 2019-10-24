package suncor.com.android.ui.resetpassword;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentResetPasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.uicomponents.SuncorSelectInputLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

public class ForgotPasswordFragment extends MainActivityFragment {

    FragmentResetPasswordBinding binding;
    ForgotPasswordViewModel viewModel;
    private ArrayList<SuncorTextInputLayout> requiredFields = new ArrayList<>();

    @Inject
    ViewModelFactory viewModelFactory;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ForgotPasswordViewModel.class);

        viewModel.sendEmailApiCall.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    getFragmentManager().popBackStack();
                    SuncorToast.makeText(getActivity(), R.string.forgot_password_toast_msg, Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Alerts.prepareGeneralErrorDialog(getActivity()).show();
                    getFragmentManager().popBackStack();
                    break;

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);

        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.setEventHandler(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        requiredFields.add(binding.forgotPasswordEmailInput);

        return binding.getRoot();
    }

    private void goBack() {
        hideKeyBoard();
        getFragmentManager().popBackStack();
    }


    public void generateForgotPasswordLink() {
        hideKeyBoard();

        int itemWithError = viewModel.validateAndReset();
        if (itemWithError != -1) {
            focusOnItem(requiredFields.get(itemWithError));
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void focusOnItem(SuncorTextInputLayout input) {
        input.getEditText().requestFocus();
        if (!(input instanceof SuncorSelectInputLayout)) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input.getEditText(), InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
