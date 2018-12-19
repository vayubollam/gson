package suncor.com.android.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import suncor.com.android.R;
import suncor.com.android.constants.GeneralConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUserName, txtPassword;
    private BroadcastReceiver loginErrorReceiver, loginRequiredReceiver, loginSuccessReceiver;
    private LoginActivity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _this = this;
        txtUserName = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);

        findViewById(R.id.btn_signin).setOnClickListener(btnSignIn_click);

        //Login error
        loginErrorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                alertError(intent.getStringExtra("errorMsg"));
            }
        };

        //Login required
        loginRequiredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                Runnable run = new Runnable() {
                    public void run() {
                        //Set error label on activity
                        //lblErrorMessage.setText(intent.getStringExtra("errorMsg"));

                        //Display remaining attempts
                        if(intent.getIntExtra("remainingAttempts",-1) > -1) {
                            //set number of atemp label
//                            lblRemainingAttempts.setText(getString(R.string.remaining_attempts, intent.getIntExtra("remainingAttempts",-1)));
                        }
                    }
                };
                _this.runOnUiThread(run);
            }
        };

        //Login success
        loginSuccessReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
        Intent intent = new Intent();
        intent.setAction(GeneralConstants.ACTION_LOGIN_CANCELLED);
        LocalBroadcastManager.getInstance(_this).sendBroadcast(intent);
    }

    //region  UI events
    View.OnClickListener btnSignIn_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(txtUserName.getText().toString().isEmpty() || txtPassword.getText().toString().isEmpty()){
                alertError("Username and password are required");
            }
            else{
                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("email",txtUserName.getText().toString());
                    credentials.put("password",txtPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setAction(GeneralConstants.ACTION_LOGIN_SUBMIT_ANSWER);
                intent.putExtra("credentials",credentials.toString());
                LocalBroadcastManager.getInstance(_this).sendBroadcast(intent);
            }
        }
    };
    //endregion

    //region custom methods
    public void alertError(final String msg) {
        Runnable run = new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setMessage(msg)
                        .setTitle("Error");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        _this.runOnUiThread(run);
    }
    //endregion
}

