package suncor.com.android.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ImageViewCompat;
import suncor.com.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {
    // Animation
    private Animation animZoomOut;

    private AppCompatImageView img_splash;
    private AppCompatImageView img_retail;

    private  AppCompatTextView txt_splash;
    private int delay=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img_splash=findViewById(R.id.img_splash_full_screen);
        img_retail=findViewById(R.id.img_retail);
        txt_splash=findViewById(R.id.txt_splash);

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent safeDrive=new Intent(getApplicationContext(),SafeDriveActivity.class);
                startActivity(safeDrive);
                finish();
            }
        },delay);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
