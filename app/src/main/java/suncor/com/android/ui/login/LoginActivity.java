package suncor.com.android.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.databinding.ActivityLoginBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;

public class LoginActivity extends DaggerAppCompatActivity {

    @Inject
    SessionManager sessionManager;
    @Inject
    ViewModelFactory viewModelFactory;
    ActivityLoginBinding loginActivityBinding;
    LoginViewModel loginViewModel;

    private AlertDialog.Builder createAlert(int title, LoginViewModel.ErrorMessage msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        loginActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginActivityBinding.setVm(loginViewModel);
        loginActivityBinding.setLifecycleOwner(this);

        loginViewModel.getLoginFailedEvent().observe(this, (event) -> {
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
        loginViewModel.getLoginSuccessEvent().observe(this, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        finish();
                    }
                }
        );

        loginViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                loginActivityBinding.emailLayout.getEditText().clearFocus();
                loginActivityBinding.passwordLayout.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginActivityBinding.getRoot().getWindowToken(), 0);
            }
        });

        loginViewModel.getNavigateToPasswordResetEvent().observe(this, (event -> {
            if (event.getContentIfNotHandled() != null) {
                Toast.makeText(this, "Navigate to password reset", Toast.LENGTH_SHORT).show();
            }
        }));

        loginViewModel.getCallCustomerService().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Toast.makeText(this, "Call customer service", Toast.LENGTH_SHORT).show();
            }
        });
    }
}