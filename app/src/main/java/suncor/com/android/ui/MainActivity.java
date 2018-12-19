package suncor.com.android.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.worklight.wlclient.api.WLAccessTokenListener;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;
import com.worklight.wlclient.auth.AccessToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import suncor.com.android.R;
import suncor.com.android.constants.GeneralConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BroadcastReceiver logoutReceiver, loginReceiver, loginRequiredReceiver;
    private MainActivity _this;
    private Button btnLoginLogOut;
    private Button btn_open_Splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _this = this;
        findViewById(R.id.btnGetToken).setOnClickListener(btnGetToken_click);
        findViewById(R.id.btnGetStations).setOnClickListener(btnGetStations_click);
        findViewById(R.id.btnCallProtectedAPI).setOnClickListener(btnProtectedAPI_click);
        btn_open_Splash=findViewById(R.id.btn_splash);
        btn_open_Splash.setOnClickListener(this);

        btnLoginLogOut = findViewById(R.id.btnLoginOut);
        btnLoginLogOut.setOnClickListener(btnLogInOut_click);

        loginRequiredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                openLoginActivity();
            }
        };

        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //remove profile tab and adjust UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "You are logedout",Toast.LENGTH_SHORT).show();
                        btnLoginLogOut.setVisibility(View.INVISIBLE);
                    }
                });
            }
        };

        loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //add profile tab and adjust UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "You are loggedin",Toast.LENGTH_SHORT).show();
                        btnLoginLogOut.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(loginRequiredReceiver, new IntentFilter(GeneralConstants.ACTION_LOGIN_REQUIRED));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, new IntentFilter(GeneralConstants.ACTION_LOGIN_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, new IntentFilter(GeneralConstants.ACTION_LOGOUT_SUCCESS));
//        LocalBroadcastManager.getInstance(this).registerReceiver(errorReceiver, new IntentFilter(GeneralConstants.ACTION_CHALLENGE_FAILURE));

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginRequiredReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
    }

    void openLoginActivity() {
        System.out.println("----------------> emailTxt:");
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    //region click listeners
    View.OnClickListener btnGetToken_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WLAuthorizationManager.getInstance().obtainAccessToken(null, new WLAccessTokenListener() {
                @Override
                public void onSuccess(final AccessToken token) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Yay, here is your token : " + token.getValue(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    System.out.println("Did not receive an access token from server: " + wlFailResponse.getErrorMsg());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Cannot get access token",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    };

    View.OnClickListener btnGetStations_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            URI adapterPath = null;

            try {
                adapterPath = new URI("/adapters/suncor/v1/locations?southWestLat=0&southWestLong=0&northEastLat=0&northEastLong=0&amenities=PayAtPump;ULTRA94;PAYPASS,PAYWAVE");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    try {
                        final JSONArray jsonArray = new JSONArray(jsonText);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Stations loaded (Total: " + jsonArray.length() + ")Cannot get stations",Toast.LENGTH_SHORT).show();                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Toast.makeText(MainActivity.this, "Cannot get stations",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    View.OnClickListener btnProtectedAPI_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            URI adapterPath = null;
            try {
                adapterPath = new URI("/adapters/suncor/v1/balances");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            assert adapterPath != null;
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String balance = "";
                    Log.d("Balance: ", wlResponse.getResponseText());
                    try {
                        final JSONObject balanceResult = wlResponse.getResponseJSON();
                        System.out.println("INFO: ----------------> balanceResult: "+ balanceResult.toString(2));
                        balance = (String) balanceResult.get("balance");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String finalBalance = balance + " points";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Here is your result : " + finalBalance,Toast.LENGTH_SHORT).show();
                            Log.d("Balance: ", "stuff");
                        }
                    });
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d("Failed to get balance: ", wlFailResponse.getErrorMsg());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Balance: ", "stuff");
                        }
                    });
                }
            });

        }
    };

    View.OnClickListener btnLogInOut_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(GeneralConstants.ACTION_LOGOUT);
            LocalBroadcastManager.getInstance(_this).sendBroadcast(intent);

        }
    };

    @Override
    public void onClick(View v) {
        if (v==btn_open_Splash)
            openSplashLogoActivity();
    }//endregion

    private void openSplashLogoActivity() {
        Intent splashlogoActivity =new Intent(this,SplashLogoActivity.class);
        startActivity(splashlogoActivity);
    }
}
