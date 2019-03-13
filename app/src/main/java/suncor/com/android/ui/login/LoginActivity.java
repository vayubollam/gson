package suncor.com.android.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.uicomponents.SuncorAppBarLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

public class LoginActivity extends AppCompatActivity {
    SessionManager sessionManager;
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
    InputFilter emailfilter = (source, start, end, dest, dstart, dend) -> {
        String filtered = "";
        for (int i = start; i < end; i++) {
            char character = source.charAt(i);
            if (!Character.isWhitespace(character)) {
                filtered += character;
            }
        }

        return filtered;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance();
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);

        userNameEditText = emailLayout.getEditText();
        userNameEditText.requestFocus();
        //     userNameEditText.setFilters(new InputFilter[]{emailfilter});
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

                if (sessionManager.isAccountBlocked()) {
                    String title = getString(R.string.login_invalid_credentials_dialog_title);
                    String content = getString(R.string.login_account_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, sessionManager.remainingTimeToUnblock() / (1000 * 60));
                    createAlert(title, content).show();
                } else {
                    progressLayout.setVisibility(View.VISIBLE);
                    sessionManager.login(userNameEditText.getText().toString(), passwordEditText.getText().toString())
                            .observe(this, (status) -> {
                                progressLayout.setVisibility(View.GONE);
                                if (status.status == Resource.Status.SUCCESS) {
                                    finish();
                                } else {
                                    if (status.data == SessionManager.SigninResponse.CHALLENGED) {
                                        String title = getString(R.string.login_invalid_credentials_dialog_title);
                                        String message;
                                        passwordEditText.setText("");
                                        int remainingAttempts = Integer.parseInt(status.message);
                                        if (remainingAttempts == SessionManager.LOGIN_ATTEMPTS - 1) {
                                            message = getString(R.string.login_invalid_credentials_dialog_1st_message);
                                            createAlert(title, message).show();
                                        } else {
                                            message = getString(R.string.login_invalid_credentials_dialog_2nd_message, Integer.parseInt(status.message), SessionManager.LOCK_TIME_MINUTES);
                                            AlertDialog.Builder dialog = createAlert(title, message);
                                            dialog.setNegativeButton(R.string.login_invalid_credentials_reset_password, (dialogInterface, which) -> {
                                                Toast.makeText(getApplicationContext(), "This will open the reset password screen when developed", Toast.LENGTH_SHORT).show();
                                                dialogInterface.dismiss();
                                            });
                                            dialog.show();
                                        }
                                    } else {
                                        String title = getString(R.string.login_invalid_credentials_dialog_title);
                                        String content = getString(R.string.login_account_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, sessionManager.remainingTimeToUnblock() / (1000 * 60));
                                        createAlert(title, content).show();
                                    }
                                }
                            });
                }
            }
        });
        ((SuncorAppBarLayout) findViewById(R.id.app_bar)).setNavigationOnClickListener((v) -> {
            onBackPressed();
        });
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


