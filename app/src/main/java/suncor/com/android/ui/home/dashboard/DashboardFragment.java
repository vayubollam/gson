package suncor.com.android.ui.home.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.GeneralConstants;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.model.Hour;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private DashboardViewModel mViewModel;
    private AppCompatTextView txt_title, txt_km, txt_open;
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

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(GeneralConstants.USER_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            new LocationLiveData(getContext())
                    .observe(this, (location -> {
                        userLocation = location;
                    }));
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setMessage("Location Permission Not Granted");
            adb.setPositiveButton("OK", null);
            adb.show();
        }

        Observer<Station> stationObserver = station -> {
            station_card.setVisibility(View.VISIBLE);
            Hour workHour = station.getHours().get(getDayofWeek() - 1);
            int currenthour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int openHour = Integer.parseInt(workHour.getOpen().substring(0, 2));
            int closeHour = Integer.parseInt(workHour.getClose().substring(0, 2));

            int openmin = Integer.parseInt(workHour.getOpen().substring(2, 4));
            int closemin = Integer.parseInt(workHour.getClose().substring(2, 4));
            if (currenthour > openHour && currenthour < closeHour) {
                txt_open.setText(getString(R.string.open_generic, getTiming(closeHour, closemin)));
            } else {
                txt_open.setText(getString(R.string.close_generic, getTiming(openHour, openmin)));
            }

            if (userLocation != null) {
                LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                LatLng origin = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                DirectionsApi.getInstance().enqueuJob(origin, dest)
                        .observe(this, result -> { //TODO choose right lifecycle owner
                            br.setVisibility(result.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
                            txt_km.setVisibility(result.status == Resource.Status.LOADING ? View.GONE : View.VISIBLE);
                            if (result.status == Resource.Status.SUCCESS) {
                                br.setVisibility(View.INVISIBLE);
                                txt_km.setText(getString(R.string.distance_generic, result.data.getDistance(), result.data.getDuration()));
                            }
                            //TODO handle error
                        });
            }
        };

        mViewModel.getNearestStation().observe(this, stationObserver);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        card_recycler = getView().findViewById(R.id.card_recycler);
        DashboardAdapter dashboardAdapter = new DashboardAdapter(getContext());
        card_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        final int speedScroll = 2500;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (count < dashboardAdapter.getItemCount()) {
                    if (count == dashboardAdapter.getItemCount() - 1) {
                        flag = false;
                    } else if (count == 0) {
                        flag = true;
                    }
                    if (flag) count++;
                    else count = 0;

                    card_recycler.smoothScrollToPosition(count);
                    handler.postDelayed(this, speedScroll);
                }
            }
        };

        handler.postDelayed(runnable, speedScroll);

        card_recycler.setAdapter(dashboardAdapter);
        Typeface tfGibsonBold = ResourcesCompat.getFont(getContext(), R.font.gibson_semibold);
        Typeface tfGibsonRegular = ResourcesCompat.getFont(getContext(), R.font.gibson_regular);
        txt_title = getView().findViewById(R.id.txt_station_title);
        txt_title.setTypeface(tfGibsonBold);
        txt_km = getView().findViewById(R.id.txt_km_station);
        txt_km.setTypeface(tfGibsonRegular);
        txt_open = getView().findViewById(R.id.txt_station_open);
        btn_card_directions = getView().findViewById(R.id.btn_card_directions);
        txt_open.setTypeface(tfGibsonRegular);
        br = getView().findViewById(R.id.br_km_card);
        station_card = getView().findViewById(R.id.station_card);
        txt_km.setText("...");

        btn_card_directions.setOnClickListener(this);

    }

    public String getTiming(int hour, int min) {

        String time;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(hour + ":" + min);
            System.out.println(dateObj);
            time = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
            time = "";
        }

        return time;
    }

    public int getDayofWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_card_directions && userLocation != null) {
            NavigationAppsHelper.openNavigationApps(getActivity(), mViewModel.nearest_station.getValue());
        }
    }
}

