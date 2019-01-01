package suncor.com.android.adapters;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import suncor.com.android.R;
import suncor.com.android.constants.GeneralConstants;
import suncor.com.android.dataObjects.Hour;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dialogs.OpenWithDialog;
import suncor.com.android.workers.DirectionsWorker;

public class StationAdapter  extends RecyclerView.Adapter<StationAdapter.StationViewHolder>   {

    private LayoutInflater layoutInflater;
    private ArrayList<Station> stations;
    private Context context;
    private LatLng userLocation;
    private FragmentActivity activity;
    private BottomSheetBehavior bottomSheetBehavior;
    public static final String ORIGIN_LAT = "origin_lat";
    public static final String ORIGIN_LNG = "origin_lng";
    public static final String DEST_LAT = "dest_lat";
    public static final String DEST_LNG = "dest_lng";

    private LatLng directionslatlng;
    private SharedPreferences prefs;

    public StationAdapter(ArrayList<Station> stations, Context context,LatLng userLocation,FragmentActivity activity, BottomSheetBehavior bottomSheetBehavior) {
        this.stations = stations;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
        this.userLocation=userLocation;
        this.activity=activity;
        this.bottomSheetBehavior=bottomSheetBehavior;
        prefs = context.getSharedPreferences(GeneralConstants.USER_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView=layoutInflater.inflate(R.layout.card_station_item,parent,false);
        StationViewHolder viewHolder=new StationViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {


        holder.txt_title.setText(stations.get(position).getAddress().getAddressLine());


        Hour workHour=stations.get(position).getHours().get(getDayofWeek()-1);
        int currenthour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int openHour=Integer.parseInt(workHour.getOpen().substring(0,2));
        int closeHour=Integer.parseInt(workHour.getClose().substring(0,2));

        int openmin=Integer.parseInt(workHour.getOpen().substring(2,4));
        int closemin=Integer.parseInt(workHour.getClose().substring(2,4));
        if(currenthour>openHour && currenthour<closeHour)
        {
            holder.txt_open.setText("Open. closes at "+ getTiming(closeHour,closemin));
        }else{
            holder.txt_open.setText("Close. opens at "+ getTiming(openHour,openmin));
        }
        Data locationData = new Data.Builder()
                        .putDouble(DEST_LAT, stations.get(position).getAddress().getLatitude())
                        .putDouble(DEST_LNG, stations.get(position).getAddress().getLongitude())
                        .putDouble(ORIGIN_LAT, userLocation.latitude)
                        .putDouble(ORIGIN_LNG,userLocation.longitude)
                        .build();
        Constraints myConstraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
        OneTimeWorkRequest getDirectionsWork = new OneTimeWorkRequest.Builder(DirectionsWorker.class).
                       setConstraints(myConstraints)
                        .setInputData(locationData)
                        .build();
        WorkManager.getInstance().enqueue(getDirectionsWork);
        WorkManager.getInstance().getWorkInfoByIdLiveData(getDirectionsWork.getId())
                        .observe(activity, workInfo -> {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                String distance=workInfo.getOutputData().getString("distance");
                                String duration=workInfo.getOutputData().getString("duration");
                                holder.br.setVisibility(View.INVISIBLE);
                                holder.txt_km.setText(distance+" away . "+duration);
                                holder.img_car_station.setVisibility(View.VISIBLE);
                            }
                        });



        holder.btn_card_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directionslatlng=new LatLng(stations.get(position).getAddress().getLatitude(),stations.get(position).getAddress().getLongitude());
                Boolean always = prefs.getBoolean("always", false);
                if (always) {
                    int choice=prefs.getInt("choice",0);
                    if(choice==1)
                    {
                        openGoogleMAps();
                    }
                    if(choice==2)
                    {
                        openWaze();
                    }
                    if(choice==0)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putDouble("lat",stations.get(position).getAddress().getLatitude());
                        bundle.putDouble("lng",stations.get(position).getAddress().getLongitude());
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        OpenWithDialog openWithDialog=new OpenWithDialog();
                        openWithDialog.setArguments(bundle);
                        openWithDialog.show(activity.getSupportFragmentManager(),"choosing");
                    }
                }else{
                    Bundle bundle=new Bundle();
                    bundle.putDouble("lat",stations.get(position).getAddress().getLatitude());
                    bundle.putDouble("lng",stations.get(position).getAddress().getLongitude());
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    OpenWithDialog openWithDialog=new OpenWithDialog();
                    openWithDialog.setArguments(bundle);
                    openWithDialog.show(activity.getSupportFragmentManager(),"choosing");
                }






            }
        });
    }


    public boolean isGoogleMapsInstalled()
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
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + directionslatlng.latitude +"," + directionslatlng.longitude);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }else{
            final String appPackageName ="com.google.android.apps.maps";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public void openWaze(){
        if(isWazeInstalled()){
            String url = "waze://?ll="+directionslatlng.latitude+","+directionslatlng.longitude+"&navigate=yes";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mapIntent.setPackage("com.waze");
            context.startActivity(mapIntent);
        }else{
            final String appPackageName ="com.waze";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
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

    public int getHeight()
    {
        WindowManager windowmanager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimension = new DisplayMetrics();
        windowmanager.getDefaultDisplay().getMetrics(dimension);
        return  dimension.heightPixels;
    }

    public int getDayofWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    public String getTiming(int hour, int min)
    {

        String time;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(hour+":"+min);
            System.out.println(dateObj);
            time=new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
            time="";
        }

       return time;
    }



    @Override
    public int getItemCount() {
        return stations.size();
    }




    public class StationViewHolder extends RecyclerView.ViewHolder  {

        AppCompatTextView txt_title,txt_km,txt_open;
        AppCompatImageView img_bottom_sheet,img_car_station;
        MaterialButton btn_card_directions;
        ProgressBar br;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            Typeface tfGibson_bold=ResourcesCompat.getFont(context,R.font.gibson_bold);
            Typeface tfGibson_regular=ResourcesCompat.getFont(context,R.font.gibson_regular);
            Typeface tfGibson_semibold=ResourcesCompat.getFont(context,R.font.gibson_semibold);
            txt_title=itemView.findViewById(R.id.txt_station_title);
            txt_title.setTypeface(tfGibson_bold);
            txt_km=itemView.findViewById(R.id.txt_km_station);
            txt_km.setTypeface(tfGibson_regular);
            txt_open=itemView.findViewById(R.id.txt_station_open);
            txt_open.setTypeface(tfGibson_regular);
            img_bottom_sheet=itemView.findViewById(R.id.img_bottom_sheet);
            btn_card_directions=itemView.findViewById(R.id.btn_card_directions);
            btn_card_directions.setTypeface(tfGibson_semibold);
            br=itemView.findViewById(R.id.br_km_card);
            img_car_station=itemView.findViewById(R.id.img_car_station);
            img_car_station.setVisibility(View.INVISIBLE);
            txt_km.setText("...");

        }


    }




}
