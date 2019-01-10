package suncor.com.android.dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.constants.GeneralConstants;

public class OpenWithDialog extends BottomSheetDialogFragment implements View.OnClickListener {
     private MaterialCardView google_maps;
    private MaterialCardView waze;
    private int choice=0;
    private MaterialButton btn_once;
    private MaterialButton btn_always;
    private Context context;
    private double lat;
    private double lng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.choose_nav_app,container,false);
        google_maps=rootView.findViewById(R.id.card_google_maps);
        waze=rootView.findViewById(R.id.card_waze);
        google_maps.setOnClickListener(this);
        waze.setOnClickListener(this);
        btn_once=rootView.findViewById(R.id.btn_choose_once);
        btn_always=rootView.findViewById(R.id.btn_choose_always);
        btn_once.setOnClickListener(this);
        btn_always.setOnClickListener(this);
        lat=Objects.requireNonNull(getArguments()).getDouble("lat");
        lng=getArguments().getDouble("lng");
        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       this.context=context;
    }

    @Override
    public void onClick(View v) {
        if(v==google_maps)
        {
            google_maps.setBackgroundColor(getResources().getColor(R.color.choose_back));
            waze.setBackgroundColor(getResources().getColor(R.color.white));
            choice=1;

        }
        if(v==waze)
        {
            waze.setBackgroundColor(getResources().getColor(R.color.choose_back));
            google_maps.setBackgroundColor(getResources().getColor(R.color.white));
            choice=2;

        }
        if(v==btn_once){
            if(choice==1){
                openGoogleMAps();
            }
            if(choice==2){
                openWaze();
            }
        }

        if(v==btn_always){
            if(choice==1){
                saveUserChoice(1);
                openGoogleMAps();
            }
            if(choice==2){
                saveUserChoice(2);
                openWaze();
            }

        }


    }

    private void saveUserChoice(int choice) {
        SharedPreferences.Editor editor = context.getSharedPreferences(GeneralConstants.USER_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("always",true);
        editor.putInt("choice", choice);
        editor.apply();
    }

    private boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public void openGoogleMAps(){
        if(isGoogleMapsInstalled()){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + lat +"," + lng);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
            dismiss();
        }else{
            final String appPackageName ="com.google.android.apps.maps";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public void openWaze(){
        if(isWazeInstalled()){
            String url = "waze://?ll="+lat+","+lng+"&navigate=yes";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mapIntent.setPackage("com.waze");
            context.startActivity(mapIntent);
            dismiss();
        }else{
            final String appPackageName ="com.waze";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public boolean isWazeInstalled()
    {
        try
        {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.waze", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }





}
