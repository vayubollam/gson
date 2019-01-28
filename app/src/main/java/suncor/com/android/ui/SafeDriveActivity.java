package suncor.com.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.ui.home.HomeActivity;

public class SafeDriveActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout SafetyMessageLayout;
    Handler safetyMessageHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_drive);
        SafetyMessageLayout = findViewById(R.id.SafetyMessageLayout);
        SafetyMessageLayout.setOnClickListener(this);
        //for testing purposes
        int SPLASH_DISPLAY_LENGTH = 30000;
        //real value (3 sec)
        //int SPLASH_DISPLAY_LENGTH = 3000;
        safetyMessageHandler.postDelayed(() -> {
            Intent mainIntent = new Intent(SafeDriveActivity.this, HomeActivity.class);
            SafeDriveActivity.this.startActivity(mainIntent);
            SafeDriveActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onClick(View v) {
        if (v == SafetyMessageLayout)
            openHomeActivity();
    }

    private void openHomeActivity() {
        safetyMessageHandler.removeCallbacksAndMessages(null);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
