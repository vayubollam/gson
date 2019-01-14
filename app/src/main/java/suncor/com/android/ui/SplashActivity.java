package suncor.com.android.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import suncor.com.android.R;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    // Animation
    private Animation animZoomOut;

    private AppCompatImageView img_retail;

    private AppCompatTextView txt_splash;
    private int delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatImageView img_splash = findViewById(R.id.img_splash_full_screen);
        img_retail = findViewById(R.id.img_retail);

        txt_splash = findViewById(R.id.txt_splash);
        Typeface tfGibsonBold = ResourcesCompat.getFont(this, R.font.gibson_semibold);
        txt_splash.setTypeface(tfGibsonBold);
        // load the animation
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoomout);

        // set animation listener
        animZoomOut.setAnimationListener(this);


        img_splash.startAnimation(animZoomOut);
        img_retail.startAnimation(animZoomOut);
        txt_splash.startAnimation(animZoomOut);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        new Handler().postDelayed(() -> {
            Intent safeDrive = new Intent(getApplicationContext(), SafeDriveActivity.class);
            safeDrive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(safeDrive);
            finish();
        }, delay);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
