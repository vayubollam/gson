package suncor.com.android.adapters;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import suncor.com.android.R;
import suncor.com.android.dataObjects.Hour;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.workers.DirectionsWorker;

public class StationAdapter  extends RecyclerView.Adapter<StationAdapter.StationViewHolder>  {

    private LayoutInflater layoutInflater;
    private ArrayList<Station> stations;
    private Context context;
    private LatLng userLocation;
    private FragmentActivity activity;
    public static final String ORIGIN_LAT = "origin_lat";
    public static final String ORIGIN_LNG = "origin_lng";
    public static final String DEST_LAT = "dest_lat";
    public static final String DEST_LNG = "dest_lng";

    OnDirClick mListener;

    public StationAdapter(ArrayList<Station> stations, Context context,LatLng userLocation,FragmentActivity activity) {
        this.stations = stations;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
        this.userLocation=userLocation;
        this.activity=activity;

        mListener= (OnDirClick) context;
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

      //  recyclerView.getLayoutManager().
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
           // mListener.onDirectionsClicked();
            }
        });
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
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(hour+":"+min);
            time = new SimpleDateFormat("hh:mm aa").format(dateObj);
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
            txt_title=itemView.findViewById(R.id.txt_station_title);
            txt_km=itemView.findViewById(R.id.txt_km_station);
            txt_open=itemView.findViewById(R.id.txt_station_open);
            img_bottom_sheet=itemView.findViewById(R.id.img_bottom_sheet);
            btn_card_directions=itemView.findViewById(R.id.btn_card_directions);
            br=itemView.findViewById(R.id.br_km_card);
            img_car_station=itemView.findViewById(R.id.img_car_station);
            img_car_station.setVisibility(View.INVISIBLE);
            txt_km.setText("...");

        }


    }

    public interface OnDirClick{
         void onDirectionsClicked();
    }
}
