package suncor.com.android.fragments;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import suncor.com.android.R;
import suncor.com.android.adapters.DashboardAdapter;
import suncor.com.android.constants.GeneralConstants;
import suncor.com.android.dataObjects.Hour;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dialogs.OpenWithDialog;
import suncor.com.android.workers.DirectionsWorker;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel mViewModel;
    private Observer<Station> stationObserver;
    private AppCompatTextView txt_title, txt_km, txt_open;
    private AppCompatImageView img_car_station;
    private MaterialButton btn_card_directions;
    private ProgressBar br;
    private MaterialCardView station_card;
    public static final String ORIGIN_LAT = "origin_lat";
    public static final String ORIGIN_LNG = "origin_lng";
    public static final String DEST_LAT = "dest_lat";
    public static final String DEST_LNG = "dest_lng";
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location userLocation;
    private RecyclerView card_recycler;
    private SharedPreferences prefs;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        prefs = getContext().getSharedPreferences(GeneralConstants.USER_PREFS_NAME, Context.MODE_PRIVATE);
        if(isAdded())
              mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = location;
                mLocationManager.removeUpdates(mLocationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);

        stationObserver=new Observer<Station>() {
            @Override
            public void onChanged(Station station) {
            station_card.setVisibility(View.VISIBLE);
                Hour workHour=station.getHours().get(getDayofWeek()-1);
                int currenthour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int openHour=Integer.parseInt(workHour.getOpen().substring(0,2));
                int closeHour=Integer.parseInt(workHour.getClose().substring(0,2));

                int openmin=Integer.parseInt(workHour.getOpen().substring(2,4));
                int closemin=Integer.parseInt(workHour.getClose().substring(2,4));
                if(currenthour>openHour && currenthour<closeHour)
                {
                    txt_open.setText("Open. closes at "+ getTiming(closeHour,closemin));
                }else{
                    txt_open.setText("Close. opens at "+ getTiming(openHour,openmin));
                }

                if(userLocation!=null){
                    Data locationData = new Data.Builder()
                            .putDouble(DEST_LAT, station.getAddress().getLatitude())
                            .putDouble(DEST_LNG, station.getAddress().getLongitude())
                            .putDouble(ORIGIN_LAT, userLocation.getLatitude())
                            .putDouble(ORIGIN_LNG,userLocation.getLongitude())
                            .build();
                    Constraints myConstraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();
                    OneTimeWorkRequest getDirectionsWork = new OneTimeWorkRequest.Builder(DirectionsWorker.class).
                            setConstraints(myConstraints)
                            .setInputData(locationData)
                            .build();
                    WorkManager.getInstance().enqueue(getDirectionsWork);

                   if(isAdded())
                            WorkManager.getInstance().getWorkInfoByIdLiveData(getDirectionsWork.getId())
                            .observe(getActivity(), workInfo -> {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    String distance=workInfo.getOutputData().getString("distance");
                                    String duration=workInfo.getOutputData().getString("duration");
                                    br.setVisibility(View.INVISIBLE);
                                    txt_km.setText(distance+" away . "+duration);
                                   img_car_station.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        };

        if (isAdded())
        mViewModel.getNearestStation().observe(getActivity(),stationObserver);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        card_recycler=getView().findViewById(R.id.card_recycler);
        DashboardAdapter dashboardAdapter=new DashboardAdapter(getContext(),getActivity());
        card_recycler.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

        final int speedScroll = 2500;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;
            @Override
            public void run() {
                if(count < dashboardAdapter.getItemCount()){
                    if(count==dashboardAdapter.getItemCount()-1){
                        flag = false;
                    }else if(count == 0){
                        flag = true;
                    }
                    if(flag) count++;
                    else count=0;

                    card_recycler.smoothScrollToPosition(count);
                    handler.postDelayed(this,speedScroll);
                }
            }
        };

        handler.postDelayed(runnable,speedScroll);

        card_recycler.setAdapter(dashboardAdapter);
        Typeface tfGibsonBold=ResourcesCompat.getFont(getContext(),R.font.gibson_semibold);
        Typeface tfGibsonRegular=ResourcesCompat.getFont(getContext(),R.font.gibson_regular);
        txt_title=getView().findViewById(R.id.txt_station_title);
        txt_title.setTypeface(tfGibsonBold);
        txt_km=getView().findViewById(R.id.txt_km_station);
        txt_km.setTypeface(tfGibsonRegular);
        txt_open=getView().findViewById(R.id.txt_station_open);
        btn_card_directions=getView().findViewById(R.id.btn_card_directions);
        txt_open.setTypeface(tfGibsonRegular);
        br=getView().findViewById(R.id.br_km_card);
        img_car_station=getView().findViewById(R.id.img_car_station);
        station_card=getView().findViewById(R.id.station_card);
        img_car_station.setVisibility(View.INVISIBLE);
        txt_km.setText("...");

        btn_card_directions.setOnClickListener(this);

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

    public int getDayofWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btn_card_directions && userLocation!=null)
        {
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
                    bundle.putDouble("lat",userLocation.getLatitude());
                    bundle.putDouble("lng",userLocation.getLongitude());
                    OpenWithDialog openWithDialog=new OpenWithDialog();
                    openWithDialog.setArguments(bundle);
                    openWithDialog.show(getActivity().getSupportFragmentManager(),"choosing");
                }
            }else{

                    Bundle bundle=new Bundle();
                    bundle.putDouble("lat",userLocation.getLatitude());
                    bundle.putDouble("lng",userLocation.getLongitude());
                    OpenWithDialog openWithDialog=new OpenWithDialog();
                    openWithDialog.setArguments(bundle);
                    openWithDialog.show(getActivity().getSupportFragmentManager(),"choosing");

            }






        }
        }

    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getContext().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public void openGoogleMAps(){
        if(isGoogleMapsInstalled()){
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + userLocation.getLatitude() +"," + userLocation.getLongitude());//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            getContext().startActivity(mapIntent);
        }else{
            final String appPackageName ="com.google.android.apps.maps";
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public void openWaze(){
        if(isWazeInstalled()){
            String url = "waze://?ll="+userLocation.getLatitude()+","+userLocation.getLongitude()+"&navigate=yes";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mapIntent.setPackage("com.waze");
            getContext().startActivity(mapIntent);
        }else{
            final String appPackageName ="com.waze";
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public boolean isWazeInstalled()
    {
        try
        {
            ApplicationInfo info = getContext().getPackageManager().getApplicationInfo("com.waze", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
    }

