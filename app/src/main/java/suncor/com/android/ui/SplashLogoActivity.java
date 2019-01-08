package suncor.com.android.ui;

import androidx.appcompat.app.AppCompatActivity;
import suncor.com.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashLogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_logo);

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashLogoActivity.this,SplashActivity.class);
            SplashLogoActivity.this.startActivity(mainIntent);
            SplashLogoActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
