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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentHomeGuestBinding;
import suncor.com.android.databinding.FragmentHomeSignedinBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
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
    @Inject
    PermissionManager permissionManager;

    private CardView nearestCard;

    private OnClickListener tryAgainLister = v -> {
        if (mViewModel.getUserLocation() != null) {
            mViewModel.setUserLocation(mViewModel.getUserLocation());
        } else {
            mViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
        }
    };

    private OnClickListener showCardDetail = v -> {
        if (mViewModel.nearestStation.getValue().data != null && !mViewModel.isLoading.get()) {
            StationDetailsDialog.showCard(this, mViewModel.nearestStation.getValue().data, nearestCard, false);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        mViewModel.locationServiceEnabled.observe(this, (enabled -> {
            if (enabled) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    mViewModel.isLoading.set(mViewModel.getUserLocation() == null);
                    locationLiveData.observe(getViewLifecycleOwner(), (location -> mViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()))));
                }
            }
        }));
        mViewModel.openNavigationApps.observe(this, event -> {
            Station station = event.getContentIfNotHandled();
            if (station != null) {
                NavigationAppsHelper.openNavigationApps(getActivity(), station);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (!mViewModel.isUserLoggedIn()) {
            return setupGuestLayout(inflater, container);
        } else {
            return setupSignedInLayout(inflater, container);
        }
    }

    private View setupSignedInLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentHomeSignedinBinding binding = FragmentHomeSignedinBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(mViewModel.isLoading);
        binding.tryAgainButton.setOnClickListener(tryAgainLister);
        binding.stationCard.setOnClickListener(showCardDetail);
        nearestCard = binding.stationCard;
        rewardsAdapter = new RewardsAdapter(getActivity(), mViewModel);
        binding.carouselCardRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(binding.carouselCardRecycler);
        binding.carouselCardRecycler.setAdapter(rewardsAdapter);
        return binding.getRoot();
    }

    private View setupGuestLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentHomeGuestBinding binding = FragmentHomeGuestBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(mViewModel.isLoading);
        binding.tryAgainButton.setOnClickListener(tryAgainLister);
        binding.stationCard.setOnClickListener(showCardDetail);
        nearestCard = binding.stationCard;
        rewardsAdapter = new RewardsAdapter(getActivity(), mViewModel);
        binding.carouselCardRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(binding.carouselCardRecycler);
        binding.carouselCardRecycler.setAdapter(rewardsAdapter);

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

        if (!inAnimationShown && !mViewModel.isUserLoggedIn()) {
            inAnimationShown = true;
            Animation animFromLet = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_home);
            animFromLet.setDuration(500);
            Animation animslideUp = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            animslideUp.setDuration(500);
            binding.carouselCardRecycler.startAnimation(animFromLet);
            nearestCard.startAnimation(animslideUp);
        }

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
    public void onStart() {
        super.onStart();
        checkAndRequestPermission();
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

    @Override
    public void onLoginStatusChanged() {
        if (!this.isDetached()) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            mViewModel.setLocationServiceEnabled(true);

        }
    }

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

