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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.ui.common.TextInputLayoutEx;

public class LoginActivity extends AppCompatActivity {
    private EditText userNameEditText, passwordEditText;
    private BroadcastReceiver loginErrorReceiver, loginRequiredReceiver, loginSuccessReceiver;

    private LinearLayout progressLayout;
    private TextInputLayoutEx emailLayout, passwordLayout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance();

        userNameEditText = findViewById(R.id.txt_email);
        userNameEditText.setFilters(new InputFilter[]{emailfilter});
        passwordEditText = findViewById(R.id.txt_password);
        passwordEditText.addTextChangedListener(passwordTextWatcher);
        userNameEditText.addTextChangedListener(emailTextWatcher);
        progressLayout = findViewById(R.id.progress_layout);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);

        emailLayout.setErrorLabelColor(getResources().getColor(R.color.black_80));
        passwordLayout.setErrorLabelColor(getResources().getColor(R.color.black_80));

        findViewById(R.id.signing_button).setOnClickListener((v) -> {
            if (validateInput()) {
                if (sessionManager.isAccountBlocked()) {
                    String title = getString(R.string.invalid_credentials_dialog_title);
                    String content = getString(R.string.sign_in_blocked_dialog_message, SessionManager.LOGIN_ATTEMPTS, sessionManager.remainingTimeToUnblock() / (1000 * 60));
                    alertError(content, title);
                } else {
                    progressLayout.setVisibility(View.VISIBLE);
                    sessionManager.login(userNameEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });
        findViewById(R.id.back_button).setOnClickListener((v) -> {
            onBackPressed();
        });

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
        LocalBroadcastManager.getInstance(this).registerReceiver(loginRequiredReceiver, new IntentFilter(SessionManager.ACTION_LOGIN_REQUIRED));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginErrorReceiver, new IntentFilter(SessionManager.ACTION_LOGIN_FAILURE));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginSuccessReceiver, new IntentFilter(SessionManager.ACTION_LOGIN_SUCCESS));
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

    private boolean validateInput() {
        Boolean allGood = true;
        if (userNameEditText.getText().toString().isEmpty()) {
            emailLayout.setError(getString(R.string.email_required));
            allGood = false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError(getString(R.string.password_required), getDrawable(R.drawable.ic_alert));
            allGood = false;
        }
        return allGood;
    }

    private void alertError(final String msg, String title) {
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
            if (!s.toString().isEmpty()) {
                passwordLayout.setError("");
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
                emailLayout.setError("");
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


