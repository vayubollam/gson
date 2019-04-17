package suncor.com.android.ui.home.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.databinding.FragmentDashboardBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.NavigationAppsHelper;

public class DashboardFragment extends BaseFragment {

    public static final String DASHBOARD_FRAGMENT_TAG = "dashboard-tag";
    private DashboardViewModel mViewModel;
    private DashboardAdapter dashboardAdapter;
    private LocationLiveData locationLiveData;
    private boolean inAnimationShown;
    private FragmentDashboardBinding binding;
    public static final int REQUEST_CHECK_SETTINGS = 100;
    ObservableBoolean isLoading = new ObservableBoolean(false);

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
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(isLoading);
        binding.tryAgainButton.setOnClickListener(tryAgainLister);
        binding.stationCard.setOnClickListener(showCardDetail);
        binding.directionsButton.setOnClickListener(openNavigation);
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        mViewModel.locationServiceEnabled.postValue(LocationUtils.isLocationEnabled(getContext()));
        setStatusBarColor(getResources().getColor(R.color.dashboard_back));

    }

    OnClickListener tryAgainLister = v -> {
        isLoading.set(true);
        if (mViewModel.getUserLocation() != null) {
            mViewModel.setUserLocation(mViewModel.getUserLocation());
        } else {
            mViewModel.locationServiceEnabled.setValue(LocationUtils.isLocationEnabled(getContext()));
        }
    };

    OnClickListener openNavigation = v -> {
        if (mViewModel.stationItem != null) {
            NavigationAppsHelper.openNavigationApps(getActivity(), mViewModel.stationItem.getStation());
        }
    };

    OnClickListener showCardDetail = v -> {
        if (mViewModel.stationItem != null && !isLoading.get()) {
            StationDetailsDialog.showCard(this, mViewModel.stationItem, binding.stationCard, false);
        }
    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!inAnimationShown) {
            inAnimationShown = true;
            Animation animFromLet = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_dashboard);
            animFromLet.setDuration(500);
            Animation animslideUp = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            animslideUp.setDuration(500);
            binding.carouselCardRecycler.startAnimation(animFromLet);
            binding.stationCard.startAnimation(animslideUp);
        }
        dashboardAdapter = new DashboardAdapter(getActivity(), sessionManager);
        binding.carouselCardRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(binding.carouselCardRecycler);
        binding.carouselCardRecycler.setAdapter(dashboardAdapter);
        Observer<Resource<Station>> stationObserver = resource -> {
            switch (resource.status) {
                case LOADING:
                    isLoading.set(true);
                    break;
                case SUCCESS:
                    isLoading.set(false);
                    if (resource.data != null) {
                        Station station = resource.data;
                        StationItem stationItem = new StationItem(station);
                        mViewModel.stationItem = stationItem;
                        binding.setStation(stationItem);
                        if (stationItem.getDistanceDuration() == null) {
                            LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                            LatLng origin = new LatLng(mViewModel.getUserLocation().latitude, mViewModel.getUserLocation().longitude);
                            DirectionsApi.getInstance().enqueuJob(origin, dest)
                                    .observe(getViewLifecycleOwner(), result -> {
                                        if (result.status == Resource.Status.SUCCESS) {
                                            mViewModel.stationItem.setDistanceDuration(result.data);
                                        } else if (result.status == Resource.Status.ERROR) {
                                            mViewModel.stationItem.setDistanceDuration(DirectionsResult.INVALID);
                                        }
                                    });
                        }
                    }
                    break;
                case ERROR:
                    isLoading.set(false);
                    break;
            }
        };

        mViewModel.nearestStation.observe(getViewLifecycleOwner(), stationObserver);
        //TODO use this if (mViewModel.getAccountState() == SessionManager.AccountState.JUST_ENROLLED) {
        if (sessionManager.isUserLoggedIn()) {
            showWelcomeMessage();
        }

        binding.settingsButton.setOnClickListener(v -> {
            openLocationSettings();
        });
        mViewModel.locationServiceEnabled.observe(this, (aBoolean -> {
            if (aBoolean) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isLoading.set(mViewModel.getUserLocation() == null);
                    locationLiveData.observe(getViewLifecycleOwner(), (location -> {
                        mViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }));
                }
            }
        }));

    }

    private void showWelcomeMessage() {
        //TODO improve this
        binding.welcomeLayout.setVisibility(View.VISIBLE);
        Profile profile = sessionManager.getProfile();
        binding.welcomeMessage.setText(getString(R.string.dashboard_enrolled_welcome_message, profile.getFirstName()));
    }

    @Override
    public void onLoginStatusChanged() {
        if (sessionManager.isUserLoggedIn()) {
            showWelcomeMessage();
        } else {
            binding.welcomeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void openLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationRequest mLocationRequestBalancedPowerAccuracy = LocationRequest.create();
        mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mViewModel.locationServiceEnabled.setValue(true);

            } else {
                try {
                    LocationSettingsResponse response = result.getResult(ApiException.class);

                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                                resolvableApiException.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException intentException) {

                            } catch (ClassCastException classException) {

                            }
                            break;

                    }

                }
            }


        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            mViewModel.locationServiceEnabled.setValue(true);
        }
    }
}

