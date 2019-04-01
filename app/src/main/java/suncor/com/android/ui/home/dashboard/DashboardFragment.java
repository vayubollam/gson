package suncor.com.android.ui.home.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.NavigationAppsHelper;

public class DashboardFragment extends BaseFragment {

    public static final String DASHBOARD_FRAGMENT_TAG = "dashboard-tag";
    private DashboardViewModel mViewModel;
    private AppCompatTextView nearStationTitle, distanceText, openHoursText;
    private MaterialButton directionsButton;
    private ProgressBar progressBar;
    private MaterialCardView stationCard;
    private Location userLocation;
    private RecyclerView carouselRecyclerView;
    private DashboardAdapter dashboardAdapter;
    private LocationLiveData locationLiveData;
    private boolean inAnimationShown;
    private AppCompatTextView welcomeMessage;
    private FrameLayout welcomeLayout;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SessionManager sessionManager;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(DashboardViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        stationCard = view.findViewById(R.id.station_card);
        nearStationTitle = view.findViewById(R.id.station_title_text);
        distanceText = view.findViewById(R.id.distance_text);
        openHoursText = view.findViewById(R.id.txt_station_open);
        directionsButton = view.findViewById(R.id.directions_button);
        carouselRecyclerView = view.findViewById(R.id.card_recycler);
        welcomeMessage = view.findViewById(R.id.welcome_message);
        welcomeLayout = view.findViewById(R.id.welcome_layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!inAnimationShown) {
            inAnimationShown = true;
            Animation animFromLet = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_dashboard);
            animFromLet.setDuration(500);
            Animation animslideUp = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            animslideUp.setDuration(500);
            carouselRecyclerView.startAnimation(animFromLet);
            stationCard.startAnimation(animslideUp);
        }
        dashboardAdapter = new DashboardAdapter(getActivity(), sessionManager);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(carouselRecyclerView);
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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationLiveData.observe(getViewLifecycleOwner(), (location -> {
                userLocation = location;
            }));
        }

        Observer<Resource<Station>> stationObserver = resource -> {
            progressBar.setVisibility(resource.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Station station = resource.data;
                StationItem stationItem = new StationItem(station);
                stationCard.setVisibility(View.VISIBLE);
                if (stationItem.isOpen24Hrs()) {
                    openHoursText.setText(getString(R.string.station_open_24hrs));
                } else if (stationItem.isOpen()) {
                    openHoursText.setText(getString(R.string.station_open_generic, stationItem.getCloseHour()));
                } else {
                    openHoursText.setText(getString(R.string.station_closed));
                }

                if (userLocation != null) {
                    LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                    LatLng origin = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                    DirectionsApi.getInstance().enqueuJob(origin, dest)
                            .observe(getViewLifecycleOwner(), result -> {
                                progressBar.setVisibility(result.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
                                distanceText.setVisibility(result.status == Resource.Status.LOADING ? View.GONE : View.VISIBLE);
                                if (result.status == Resource.Status.SUCCESS) {
                                    distanceText.setText(DirectionsResult.formatDistanceDuration(getContext(), result.data));
                                    distanceText.setTextColor(getResources().getColor(R.color.black_80));
                                } else if (result.status == Resource.Status.ERROR) {
                                    distanceText.setText(R.string.station_distance_unavailable);
                                    distanceText.setTextColor(getResources().getColor(R.color.black_60));
                                }
                            });
                } else {
                    distanceText.setVisibility(View.VISIBLE);
                    distanceText.setText(R.string.station_distance_unavailable);
                    distanceText.setTextColor(getResources().getColor(R.color.black_60));
                }

                directionsButton.setOnClickListener((v) -> {
                    NavigationAppsHelper.openNavigationApps(getActivity(), station);
                });
            }
        };

        mViewModel.nearestStation.observe(getViewLifecycleOwner(), stationObserver);
        //TODO use this if (mViewModel.getAccountState() == SessionManager.AccountState.JUST_ENROLLED) {
        if (sessionManager.isUserLoggedIn()) {
            showWelcomeMessage();
        }

    }

    private void showWelcomeMessage() {
        //TODO improve this
        welcomeLayout.setVisibility(View.VISIBLE);
        Profile profile = sessionManager.getProfile();
        welcomeMessage.setText(getString(R.string.dashboard_enrolled_welcome_message, profile.getFirstName()));
    }

    @Override
    public void onLoginStatusChanged() {
        if (sessionManager.isUserLoggedIn()) {
            showWelcomeMessage();
        } else {
            welcomeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

