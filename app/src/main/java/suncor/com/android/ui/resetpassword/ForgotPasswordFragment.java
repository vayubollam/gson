package suncor.com.android.ui.resetpassword;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentForgotPasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.common.MainActivityFragment;

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
                    Alerts.prepareGeneralErrorDialog(getActivity()).show();
                    getFragmentManager().popBackStack();
                    break;
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

}
