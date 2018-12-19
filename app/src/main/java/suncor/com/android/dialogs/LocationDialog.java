package suncor.com.android.dialogs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;
import suncor.com.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;

public class LocationDialog extends AppCompatActivity implements View.OnClickListener {
      private AppCompatTextView btn_location_alert_cancel, btn_location_alert_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_dialog);
        btn_location_alert_cancel=findViewById(R.id.btn_location_alert_cancel);
        btn_location_alert_ok=findViewById(R.id.btn_location_alert_ok);
        btn_location_alert_cancel.setOnClickListener(this);
        btn_location_alert_ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==btn_location_alert_cancel)
        {
            finish();
        }
        if(v==btn_location_alert_ok)
        {
            finish();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
}
