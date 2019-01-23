package suncor.com.android.ui.home.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Hour;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    public static final String DASHBOARD_FRAGMENT_TAG = "dashboard-tag";
    private DashboardViewModel mViewModel;
    private AppCompatTextView nearStationTitle, distanceText, openHoursText;
    private MaterialButton directionsButton;
    private ProgressBar progressBar;
    private MaterialCardView stationCard;
    private Location userLocation;
    private RecyclerView carouselRecyclerView;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            new LocationLiveData(getContext())
                    .observe(this, (location -> {
                        userLocation = location;
                    }));
        }

        Observer<Station> stationObserver = station -> {
            stationCard.setVisibility(View.VISIBLE);
            Hour workHour = station.getHours().get(getDayofWeek() - 1);
            int currenthour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int openHour = Integer.parseInt(workHour.getOpen().substring(0, 2));
            int closeHour = Integer.parseInt(workHour.getClose().substring(0, 2));

            int openmin = Integer.parseInt(workHour.getOpen().substring(2, 4));
            int closemin = Integer.parseInt(workHour.getClose().substring(2, 4));
            if (currenthour > openHour && currenthour < closeHour) {
                openHoursText.setText(getString(R.string.open_generic, getTiming(closeHour, closemin)));
            } else {
                openHoursText.setText(getString(R.string.close_generic, getTiming(openHour, openmin)));
            }

            if (userLocation != null) {
                LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                LatLng origin = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                DirectionsApi.getInstance().enqueuJob(origin, dest)
                        .observe(this, result -> { //TODO choose right lifecycle owner
                            progressBar.setVisibility(result.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
                            distanceText.setVisibility(result.status == Resource.Status.LOADING ? View.GONE : View.VISIBLE);
                            if (result.status == Resource.Status.SUCCESS) {
                                progressBar.setVisibility(View.INVISIBLE);
                                distanceText.setText(DirectionsResult.formatDistanceDuration(getContext(), result.data));
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
        carouselRecyclerView = getView().findViewById(R.id.card_recycler);
        DashboardAdapter dashboardAdapter = new DashboardAdapter(getContext());
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

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

                    carouselRecyclerView.smoothScrollToPosition(count);
                    handler.postDelayed(this, speedScroll);
                }
            }
        };

        //handler.postDelayed(runnable, speedScroll);

        carouselRecyclerView.setAdapter(dashboardAdapter);
        Typeface tfGibsonBold = ResourcesCompat.getFont(getContext(), R.font.gibson_semibold);
        Typeface tfGibsonRegular = ResourcesCompat.getFont(getContext(), R.font.gibson_regular);
        nearStationTitle = getView().findViewById(R.id.station_title_text);
        nearStationTitle.setTypeface(tfGibsonBold);
        distanceText = getView().findViewById(R.id.distance_text);
        distanceText.setTypeface(tfGibsonRegular);
        openHoursText = getView().findViewById(R.id.txt_station_open);
        directionsButton = getView().findViewById(R.id.directions_button);
        openHoursText.setTypeface(tfGibsonRegular);
        progressBar = getView().findViewById(R.id.progress_bar);
        stationCard = getView().findViewById(R.id.station_card);
        distanceText.setText("...");

        directionsButton.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        if (v == directionsButton && userLocation != null) {
            NavigationAppsHelper.openNavigationApps(getActivity(), mViewModel.nearest_station.getValue());
        }
    }
}

