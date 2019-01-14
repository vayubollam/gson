package suncor.com.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.ui.home.HomeActivity;

public class SafeDriveActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_drive);
        btn_ok = findViewById(R.id.btn_drive_safely_ok);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ok)
            openHomeActivity();
    }

    private void openHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
