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
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.uicomponents.SuncorAppBarLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

public class LoginActivity extends DaggerAppCompatActivity {
    TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                passwordLayout.setError("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private EditText userNameEditText, passwordEditText;
    private LinearLayout progressLayout;
    private SuncorTextInputLayout emailLayout, passwordLayout;
    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                emailLayout.setError("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Inject
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);

        userNameEditText = emailLayout.getEditText();
        userNameEditText.requestFocus();

        passwordEditText = passwordLayout.getEditText();
        passwordEditText.addTextChangedListener(passwordTextWatcher);
        userNameEditText.addTextChangedListener(emailTextWatcher);
        progressLayout = findViewById(R.id.progress_layout);

        findViewById(R.id.signing_button).setOnClickListener((v) -> {
            if (validateInput()) {
                userNameEditText.clearFocus();
                passwordEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);


                progressLayout.setVisibility(View.VISIBLE);
                sessionManager.login(userNameEditText.getText().toString(), passwordEditText.getText().toString())
                        .observe(this, (result) -> {
                            progressLayout.setVisibility(View.GONE);
                            if (result.status == Resource.Status.SUCCESS) {
                                SigninResponse response = result.data;
                                switch (response.getStatus()) {
                                    case SUCCESS:
                                        finish();
                                        break;
                                    case WRONG_CREDENTIALS: {
                                        String title = getString(R.string.login_invalid_credentials_dialog_title);
                                        String message;
                                        passwordEditText.setText("");
                                        if (response.getRemainingAttempts() == SessionManager.LOGIN_ATTEMPTS - 1 || response.getRemainingAttempts() == -1) {
                                            message = getString(R.string.login_invalid_credentials_dialog_1st_message);
                                            createAlert(title, message).show();
                                        } else {
                                            message = getString(R.string.login_invalid_credentials_dialog_2nd_message, response.getRemainingAttempts(), SessionManager.LOCK_TIME_MINUTES);
                                            AlertDialog.Builder dialog = createAlert(title, message);
                                            dialog.setNegativeButton(R.string.login_invalid_credentials_reset_password, (dialogInterface, which) -> {
                                                Toast.makeText(getApplicationContext(), "This will open the reset password screen when developed", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                            });
                                            dialog.show();
                                        }
                                        break;
                                    }
                                    case SOFT_LOCKED: {
                                        String title = getString(R.string.login_invalid_credentials_dialog_title);
                                        String content = getString(R.string.login_account_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, response.getTimeOut());
                                        createAlert(title, content).show();
                                        break;
                                    }
                                    case HARD_LOCKED: {
                                        String title = getString(R.string.login_hard_lock_alert_title);
                                        String content = getString(R.string.login_hard_lock_alert_message);//TODO this message should be updated
                                        AlertDialog.Builder dialog = createAlert(title, content);
                                        dialog.setNegativeButton(R.string.login_hard_lock_alert_call_button, (dialogInterface, which) -> {
                                            //TODO handle call
                                            dialogInterface.dismiss();
                                        });
                                        dialog.show();
                                    }
                                    default:
                                        Alerts.prepareGeneralErrorDialog(this).show();
                                }
                            } else if (result.status == Resource.Status.ERROR) {
                                Alerts.prepareGeneralErrorDialog(this).show();
                            }
                        });
            }
        });
        ((SuncorAppBarLayout) findViewById(R.id.app_bar)).setNavigationOnClickListener((v) -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            sessionManager.cancelLogin();
        } catch (Exception ignored) {
        }
    }

    private boolean validateInput() {
        Boolean allGood = true;
        if (userNameEditText.getText().toString().isEmpty()) {
            emailLayout.setError(getString(R.string.login_email_field_error));
            allGood = false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError(getString(R.string.login_password_field_error));
            allGood = false;
        }
        return allGood;
    }

    private AlertDialog.Builder createAlert(final String title, final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder;
    }
}


