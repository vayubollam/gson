package suncor.com.android.ui.resetpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.Objects;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentResetPasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.resetpassword.ResetPasswordRequest;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.BaseFragment;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.utilities.AnalyticsUtils;

public class ResetPasswordFragment extends BaseFragment {

    private FragmentResetPasswordBinding binding;
    private ResetPasswordViewModel viewModel;
    private ResetPasswordRequest request;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ResetPasswordViewModel.class);
        request = ResetPasswordFragmentArgs.fromBundle(getArguments()).getResetPasswordRequest();
        viewModel.setRequest(request);


        viewModel.resetPasswordLiveData.observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    hideKeyboard();
                    break;
                case SUCCESS:
                    hideKeyboard();
                    SuncorToast.makeText(getActivity(), R.string.reset_password_success_toast, Toast.LENGTH_LONG).show();
                    moveToLogin();
                    break;
                case ERROR:
                    hideKeyboard();
                    if (Objects.requireNonNull(resource.message).equalsIgnoreCase(ErrorCodes.ERR_PASSWORD_USED_EARLIER)) {
                        Alerts.prepareCustomDialogOk(
                                getString(R.string.msg_used_password_title),
                                getString(R.string.msg_used_password_message),
                                getActivity(),
                                (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    moveToLogin();
//                                    goBack();
                                }, "Reset Password").show();
                    } else {
                        Alerts.prepareGeneralErrorDialog(getActivity(), "Reset Password").show();
                    }
                    AnalyticsUtils.logEvent(this.getContext(), AnalyticsUtils.Event.FORMERROR,
                            new Pair<>(AnalyticsUtils.Param.errorMessage,getString(R.string.msg_e001_title)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, "Reset Password"));
                    break;

            }
        });
    }

    private void moveToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.LOGIN_FROM_RESET_PASSWORD_EXTRA, true);
        startActivity(intent);
        if(getActivity() != null) getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.passwordInput.getPasswordToggle().setVisibility(View.VISIBLE);
        binding.passwordInput.getEditText().setOnFocusChangeListener((v, f) -> viewModel.getPasswordField().setHasFocus(f));
        scrollToView(binding.passwordInput);
        return binding.getRoot();
    }

    private void goBack() {
        hideKeyboard();
        Navigation.findNavController(getView()).popBackStack();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    private void scrollToView(View view) {
        binding.scrollView.postDelayed(() -> {
            int scrollPosition = view.getTop();
            binding.scrollView.smoothScrollTo(0, scrollPosition);
        }, 400);
    }

    @Override
    protected String getScreenName() {
        return "reset-password";
    }
}