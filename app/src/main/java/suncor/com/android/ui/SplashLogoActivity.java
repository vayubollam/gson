package suncor.com.android.ui;

import androidx.appcompat.app.AppCompatActivity;
import suncor.com.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashLogoActivity extends AppCompatActivity {
    private int SPLASH_DISPLAY_LENGTH=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_logo);

           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   Intent mainIntent = new Intent(SplashLogoActivity.this,SplashActivity.class);
                   SplashLogoActivity.this.startActivity(mainIntent);
                   SplashLogoActivity.this.finish();
               }
           },SPLASH_DISPLAY_LENGTH);
    }
}
