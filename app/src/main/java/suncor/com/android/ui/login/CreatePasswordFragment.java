package suncor.com.android.ui.login;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCreatePasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;

public class CreatePasswordFragment extends DaggerFragment {

    private static final String EMAIL_EXTRA = "email";
    @Inject
    ViewModelFactory viewModelFactory;
    private CreatePasswordViewModel viewModel;
    private FragmentCreatePasswordBinding binding;

    public static CreatePasswordFragment newInstance(String email) {
        CreatePasswordFragment fragment = new CreatePasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EMAIL_EXTRA, email);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CreatePasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CreatePasswordViewModel.class);
        viewModel.setEmail(getArguments().getString(EMAIL_EXTRA));

        viewModel.api.observe(this, (r) -> {
            if (r.status == Resource.Status.LOADING) {
                binding.passwordInput.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            } else if (r.status == Resource.Status.SUCCESS) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else if (r.status == Resource.Status.ERROR) {
                if (ErrorCodes.ERR_PASSWORD_DUPLICATED.equals(r.message)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle(R.string.login_create_password_duplicated_alert_title);
                    alertBuilder.setMessage(R.string.login_create_password_duplicated_alert_message);
                    alertBuilder.setPositiveButton(R.string.ok, ((dialog, which) -> {
                        binding.passwordInput.setText("");
                        dialog.dismiss();
                    }));
                    alertBuilder.show();
                } else {
                    Dialog dialog = Alerts.prepareGeneralErrorDialog(getActivity());
                    dialog.setOnDismissListener(dialogInterface -> getActivity().finish());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePasswordBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener((v) -> {
            getFragmentManager().popBackStack();
        });
        binding.mainLayout.getLayoutTransition().disableTransitionType(LayoutTransition.CHANGE_APPEARING);
        binding.passwordInput.getEditText().requestFocus();

        return binding.getRoot();
    }
}
