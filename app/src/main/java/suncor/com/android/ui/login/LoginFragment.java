package suncor.com.android.ui.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentLoginBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;

public class LoginFragment extends DaggerFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);

        viewModel.getLoginFailedEvent().observe(this, (event) -> {
            LoginViewModel.LoginFailResponse response = event.getContentIfNotHandled();
            if (response != null) {

                AlertDialog.Builder dialog = createAlert(response.title, response.message);
                if (response.callback != null) {
                    dialog.setNegativeButton(response.buttonTitle, (i, w) -> {
                        response.callback.call();
                        i.dismiss();
                    });
                }
                dialog.show();
            }
        });

        viewModel.getLoginSuccessEvent().observe(this, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        getActivity().finish();
                    }
                }
        );

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.emailLayout.getEditText().clearFocus();
                binding.passwordLayout.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            }
        });

        viewModel.getPasswordResetEvent().observe(this, (event -> {
            if (event.getContentIfNotHandled() != null) {
                Toast.makeText(getContext(), "Reset password", Toast.LENGTH_SHORT).show();
            }
        }));

        viewModel.getCallCustomerService().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                String customerSupportNumber = getString(R.string.customer_support_number);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + customerSupportNumber));
                startActivity(intent);
            }
        });

        viewModel.getCreatePasswordEvent().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new CreatePasswordFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private AlertDialog.Builder createAlert(int title, LoginViewModel.ErrorMessage msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String message;
        if (msg.args != null) {
            message = getString(msg.content, msg.args);
        } else {
            message = getString(msg.content);
        }
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener((v) -> getActivity().finish());
        return binding.getRoot();
    }
}
