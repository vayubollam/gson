package suncor.com.android.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.worklight.wlclient.api.WLClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import suncor.com.android.GeneralConstants;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUserName, txtPassword;
    private BroadcastReceiver loginErrorReceiver, loginRequiredReceiver, loginSuccessReceiver;

    private UserLoginChallengeHandler userLoginChallengeHandler;
    private LinearLayout progressLayout;
    private TextInputLayout email_layout, password_layout;
    private TextView emailError, passwordError;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance();
        userLoginChallengeHandler = (UserLoginChallengeHandler) WLClient.getInstance().getSecurityCheckChallengeHandler(GeneralConstants.SECURITY_CHECK_NAME_LOGIN);

        txtUserName = findViewById(R.id.txt_email);
        txtUserName.setFilters(new InputFilter[]{emailfilter});
        txtPassword = findViewById(R.id.txt_password);
        txtPassword.addTextChangedListener(passwordTextWatcher);
        txtUserName.addTextChangedListener(emailTextWatcher);
        progressLayout = findViewById(R.id.progress_layout);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);
        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);

        findViewById(R.id.signing_button).setOnClickListener(btnSignIn_click);
        findViewById(R.id.back_button).setOnClickListener(btnBack_click);

        //Login error
        loginErrorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressLayout.setVisibility(View.GONE);
                String title = getString(R.string.invalid_credentials_dialog_title);
                String content = getString(R.string.sign_in_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, sessionManager.remainingTimeToUnblock() / (1000 * 60));
                alertError(content, title);
            }
        };

        //Login required
        loginRequiredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                Runnable run = () -> {
                    progressLayout.setVisibility(View.GONE);
                    if (intent.getIntExtra(UserLoginChallengeHandler.REMAINING_ATTEMPTS, -1) > -1) {
                        String message = getString(R.string.invalid_credentials_dialog_message, intent.getIntExtra(UserLoginChallengeHandler.REMAINING_ATTEMPTS, 0), SessionManager.LOCK_TIME_MINUTES);
                        String title = getString(R.string.invalid_credentials_dialog_title);
                        alertError(message, title);
                    }
                };
                runOnUiThread(run);
            }
        };

        //Login success
        loginSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressLayout.setVisibility(View.VISIBLE);
                finish();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(loginRequiredReceiver, new IntentFilter(GeneralConstants.ACTION_LOGIN_REQUIRED));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginErrorReceiver, new IntentFilter(GeneralConstants.ACTION_LOGIN_FAILURE));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginSuccessReceiver, new IntentFilter(GeneralConstants.ACTION_LOGIN_SUCCESS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginErrorReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginRequiredReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginSuccessReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            sessionManager.cancelLogin();
        } catch (Exception ignored) {
        }
    }

    //region  UI events
    View.OnClickListener btnSignIn_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!validateInput()) {
                return;
            }

            if (txtUserName.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()) {
                alertError("Username and password are required", getString(R.string.error));
            } else {

                if (sessionManager.isAccountBlocked()) {
                    String title = getString(R.string.invalid_credentials_dialog_title);
                    String content = getString(R.string.sign_in_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, sessionManager.remainingTimeToUnblock() / (1000 * 60));
                    alertError(content, title);
                } else {
                    progressLayout.setVisibility(View.VISIBLE);
                    sessionManager.login(txtUserName.getText().toString(), txtPassword.getText().toString());
                }
            }
        }
    };

    private boolean validateInput() {
        Boolean allGood = true;
        if (txtUserName.getText().toString().isEmpty()) {
            email_layout.setError(" ");
            password_layout.setErrorEnabled(true);
            emailError.setVisibility(View.VISIBLE);
            allGood = false;
        }
        if (txtPassword.getText().toString().isEmpty()) {
            password_layout.setError(" ");
            password_layout.setErrorEnabled(true);
            passwordError.setVisibility(View.VISIBLE);
            password_layout.setPasswordVisibilityToggleDrawable(R.drawable.ic_alert);
            allGood = false;
        }
        return allGood;
    }

    View.OnClickListener btnBack_click = v -> onBackPressed();

    public void alertError(final String msg, String title) {
        Runnable run = () -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msg)
                    .setTitle(title);
            builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                // User clicked OK button
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        };
        runOnUiThread(run);
    }


    TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty() && password_layout.getPasswordVisibilityToggleDrawable().getConstantState() == getDrawable(R.drawable.ic_alert).getConstantState()) {
                password_layout.setPasswordVisibilityToggleDrawable(R.drawable.show_hide_password_background);
                password_layout.setError("");
                passwordError.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                email_layout.setError("");
                emailError.setVisibility(View.GONE);
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
}


