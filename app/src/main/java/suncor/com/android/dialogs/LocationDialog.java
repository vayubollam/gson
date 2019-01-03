package suncor.com.android.dialogs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import suncor.com.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class LocationDialog extends DialogFragment implements View.OnClickListener {
      private AppCompatTextView btn_location_alert_cancel, btn_location_alert_ok;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_location_dialog,container,false);
        btn_location_alert_cancel=rootView.findViewById(R.id.btn_location_alert_cancel);
        btn_location_alert_ok=rootView.findViewById(R.id.btn_location_alert_ok);
        btn_location_alert_cancel.setOnClickListener(this);
        btn_location_alert_ok.setOnClickListener(this);
        return rootView;
    }




    @Override
    public void onClick(View v) {
        if(v==btn_location_alert_cancel)
        {
           dismiss();
        }
        if(v==btn_location_alert_ok)
        {
            dismiss();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
}
