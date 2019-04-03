package suncor.com.android.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;

public class LoginActivity extends DaggerAppCompatActivity {

    @Inject
    SessionManager sessionManager;
    @Inject
    ViewModelFactory viewModelFactory;
    ActivityLoginBinding loginActivityBinding;
    LoginViewModel loginViewModel;
    private AlertDialog.Builder createAlert(final String title, final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
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
        loginActivityBinding.executePendingBindings();
        loginViewModel.loginLiveData.observe(this, result -> {
            String title;
            String message;
            if (result.status == Resource.Status.SUCCESS) {
                SigninResponse response = result.data;
                switch (response.getStatus()){
                    case SUCCESS:
                        finish();
                        break;
                    case WRONG_CREDENTIALS:
                        int remainingAttempts = response.getRemainingAttempts();
                        title = getString(R.string.login_invalid_credentials_dialog_title);

                        if (remainingAttempts == SessionManager.LOGIN_ATTEMPTS - 1 || remainingAttempts == -1) {
                            message = getString(R.string.login_invalid_credentials_dialog_1st_message);
                            createAlert(title, message).show();
                        }else {
                            message = getString(R.string.login_invalid_credentials_dialog_2nd_message, remainingAttempts, SessionManager.LOCK_TIME_MINUTES);
                            AlertDialog.Builder dialog = createAlert(title, message);
                            dialog.setNegativeButton(R.string.login_invalid_credentials_reset_password, (dialogInterface, which) -> {
                                Toast.makeText(getApplicationContext(), "This will open the reset password screen when developed", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                        });
                        }
                        break;
                    case SOFT_LOCKED:
                        title = getString(R.string.login_invalid_credentials_dialog_title);
                        message = getString(R.string.login_account_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, response.getTimeOut() / (1000 * 60));
                        createAlert(title, message).show();
                        break;
                    case HARD_LOCKED:
                        title = getString(R.string.login_hard_lock_alert_title);
                        message = getString(R.string.login_hard_lock_alert_message);
                        createAlert(title, message).show();
                        break;
                    case OTHER_FAILURE:
                        Alerts.prepareGeneralErrorDialog(this).show();
                        break;
        }
            }
            else if (result.status == Resource.Status.ERROR){
                Alerts.prepareGeneralErrorDialog(this).show();
            }
        });
    }
}