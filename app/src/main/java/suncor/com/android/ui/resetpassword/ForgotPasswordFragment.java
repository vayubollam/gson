package suncor.com.android.ui.resetpassword;

import android.content.Context;
import android.content.res.Resources;
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
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentForgotPasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

import static suncor.com.android.mfp.ErrorCodes.ERR_ACCOUNT_SOFT_LOCK;
import static suncor.com.android.mfp.ErrorCodes.ERR_PROFILE_NOT_FOUND;
import static suncor.com.android.mfp.ErrorCodes.ERR_RESTRICTED_DOMAIN;

public class ForgotPasswordFragment extends MainActivityFragment {

    FragmentForgotPasswordBinding binding;
    ForgotPasswordViewModel viewModel;

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
                    hideKeyBoard();
                    break;
                case SUCCESS:
                    getFragmentManager().popBackStack();
                    SuncorToast.makeText(getActivity(), R.string.forgot_password_toast_msg, Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    AnalyticsUtils.logEvent(this.getContext(), AnalyticsUtils.Event.error,
                            new Pair<>(AnalyticsUtils.Param.errorMessage,getString(R.string.msg_e001_title)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, "Forgot Password"));

                    try {
                        String[] arrayResponse = resource.message.split(";");
                        String errorMessage = arrayResponse[0];

                        if (errorMessage.equals(ERR_ACCOUNT_SOFT_LOCK)) {
                            String remainingMinutes = arrayResponse[1].toString();
                            String alertMessage = getResources().getString(R.string.security_answer_soft_lock_retry_alert_message);
                            alertMessage = alertMessage.replace("X", remainingMinutes);

                            Alerts.prepareCustomDialogOk(getResources().getString(R.string.login_soft_lock_alert_title), alertMessage, getActivity(), ((dialog, which) -> {
                                dialog.dismiss();
                                getFragmentManager().popBackStack();
                            }), "forgot password").show();
                        } else if (errorMessage.equals(ERR_PROFILE_NOT_FOUND)) {
                            Alerts.prepareCustomDialog(
                                    getContext(),
                                    getString(R.string.forgot_password_alert_title),
                                    getString(R.string.forgot_password_alert_message),
                                    "",
                                    "Ok",
                                    (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                    }, "forgot password").show();
                        } else if (errorMessage.equals(ERR_RESTRICTED_DOMAIN)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle(R.string.enrollment_email_restricted_alert_title);
                            dialog.setPositiveButton(R.string.ok, (d, w) -> {
                                d.dismiss();
                            });
                            dialog.show();
                        } else {
                            Alerts.prepareGeneralErrorDialog(getActivity(), "forgot password").show();
                        }
                        break;
                    } catch (Resources.NotFoundException e) {
                        Alerts.prepareGeneralErrorDialog(getActivity(), "forgot password").show();
                    }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        scrollToView(binding.forgotPasswordEmailInput);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        return binding.getRoot();
    }

    private void goBack() {
        hideKeyBoard();
        getFragmentManager().popBackStack();
    }

    private void hideKeyBoard() {
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
        return "forgot-password";
    }
}
