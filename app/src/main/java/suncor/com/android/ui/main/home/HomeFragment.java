package suncor.com.android.ui.main.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.databinding.FragmentHomeBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.utilities.PermissionManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeFragment extends BottomNavigationFragment {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Inject
    ViewModelFactory viewModelFactory;
    private HomeViewModel mViewModel;
    private RewardsAdapter rewardsAdapter;
    private LocationLiveData locationLiveData;
    private boolean inAnimationShown;
    private FragmentHomeBinding binding;
    @Inject
    PermissionManager permissionManager;

    private OnClickListener tryAgainLister = v -> {
        if (mViewModel.getUserLocation() != null) {
            mViewModel.setUserLocation(mViewModel.getUserLocation());
        } else {
            mViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
        }
    };
    private OnClickListener openNavigation = v -> {
        if (mViewModel.stationItem != null) {
            NavigationAppsHelper.openNavigationApps(getActivity(), mViewModel.stationItem.getStation());
        }
    };
    private OnClickListener showCardDetail = v -> {
        if (mViewModel.stationItem != null && !mViewModel.isLoading.get()) {
            StationDetailsDialog.showCard(this, mViewModel.stationItem, binding.stationCard, false);
        }
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        mViewModel.nearestStation.observe(this, stationObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(mViewModel.isLoading);
        binding.tryAgainButton.setOnClickListener(tryAgainLister);
        binding.stationCard.setOnClickListener(showCardDetail);
        binding.directionsButton.setOnClickListener(openNavigation);
        return binding.getRoot();
    }


    private void showRequestLocationDialog(boolean previouselyDeniedWithNeverASk) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle(R.string.enable_location_dialog_title);
        adb.setMessage(R.string.enable_location_dialog_message);
        adb.setNegativeButton(R.string.cancel, null);
        adb.setPositiveButton(R.string.ok, (dialog, which) -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED && !LocationUtils.isLocationEnabled(getContext())) {
                LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS);
                return;
            }

            permissionManager.setFirstTimeAsking(Manifest.permission.ACCESS_FINE_LOCATION, false);
            if (previouselyDeniedWithNeverASk) {
                PermissionManager.openAppSettings(getActivity());
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
            dialog.dismiss();
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.black_4);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!inAnimationShown) {
            inAnimationShown = true;
            Animation animFromLet = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_home);
            animFromLet.setDuration(500);
            Animation animslideUp = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            animslideUp.setDuration(500);
            binding.carouselCardRecycler.startAnimation(animFromLet);
            binding.stationCard.startAnimation(animslideUp);
        }
        checkAndRequestPermission();

        rewardsAdapter = new RewardsAdapter(getActivity(), mViewModel);
        binding.carouselCardRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(binding.carouselCardRecycler);
        binding.carouselCardRecycler.setAdapter(rewardsAdapter);
        if (mViewModel.isUserLoggedIn()) {
            showWelcomeMessage();
        }

        binding.settingsButton.setOnClickListener(v -> {
            permissionManager.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
                @Override
                public void onNeedPermission() {

                    showRequestLocationDialog(false);
                }

                @Override
                public void onPermissionPreviouslyDenied() {
                    //in case in the future we would show any rational 
                    showRequestLocationDialog(false);
                }

                @Override
                public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
                    showRequestLocationDialog(true);
                }

                @Override
                public void onPermissionGranted() {
                    showRequestLocationDialog(false);
                }
            });
        });
        mViewModel.locationServiceEnabled.observe(this, (enabled -> {
            if (enabled) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    mViewModel.isLoading.set(mViewModel.getUserLocation() == null);
                    locationLiveData.observe(getViewLifecycleOwner(), (location -> mViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()))));
                }
            }
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                if (LocationUtils.isLocationEnabled(getContext())) {
                    mViewModel.setLocationServiceEnabled(true);
                } else {
                    LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS);
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showWelcomeMessage() {
        //TODO improve this
        binding.welcomeLayout.setVisibility(View.VISIBLE);
        Profile profile = mViewModel.getProfile();
        binding.welcomeMessage.setText(getString(R.string.dashboard_enrolled_welcome_message, profile.getFirstName(), mViewModel.getRewardedPoints()));
    }

    @Override
    public void onLoginStatusChanged() {
        if (mViewModel.isUserLoggedIn()) {
            showWelcomeMessage();
        } else {
            binding.welcomeLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            mViewModel.setLocationServiceEnabled(true);

        }
    }

    Observer<Resource<Station>> stationObserver = resource -> {
        if (resource.status == Resource.Status.SUCCESS) {
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
        }
    };


    private void checkAndRequestPermission() {
        permissionManager.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                if (!permissionManager.isAlertShown()) {
                    permissionManager.setAlertShown(true);
                    showRequestLocationDialog(false);
                }
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                //in case in the future we would show any rational
            }

            @Override
            public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
            }

            @Override
            public void onPermissionGranted() {
                mViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
            }
        });
    }

}
