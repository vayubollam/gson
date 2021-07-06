package suncor.com.android.ui.main.stationlocator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentNearestStationBinding;
import suncor.com.android.databinding.HomeNearestCardBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpFragmentDirections;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.utilities.PermissionManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class NearestStationFragment extends MainActivityFragment implements OnBackPressedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentNearestStationBinding binding;
    private ObservableBoolean isLoading = new ObservableBoolean(true);

    @Inject
    ViewModelFactory viewModelFactory;
    private HomeViewModel mViewModel;
    private LocationLiveData locationLiveData;
    @Inject
    PermissionManager permissionManager;
    private HomeNearestCardBinding nearestCard;

    private OnClickListener tryAgainLister = v -> {
        if (mViewModel.getUserLocation() != null) {
            mViewModel.isLoading.set(true);
            mViewModel.setUserLocation(mViewModel.getUserLocation());
        } else {
            mViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
        }
    };

    private OnClickListener showCardDetail = v -> {
        Resource<StationItem> resource = mViewModel.nearestStation.getValue();

        if (resource != null && resource.data != null && !mViewModel.isLoading.get()) {
            resource.data.isFavourite = resource.data.favouriteRepository.isFavourite(resource.data.getStation());
            StationDetailsDialog.showCard(this, resource.data, nearestCard.getRoot(), false);
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
            } else {
                isLoading.set(false);
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
        View view = setupLayout(inflater, container);

        isLoading.set(true);
        binding.setIsLoading(isLoading);
        AnalyticsUtils.setCurrentScreenName(getActivity(), "offsite-nearest-station-loading");

        //Setup nearest card click listeners
        nearestCard.getRoot().setOnClickListener(showCardDetail);
        nearestCard.tryAgainButton.setOnClickListener(tryAgainLister);

        nearestCard.settingsButton.setOnClickListener(v -> {
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

        mViewModel.isPAPAvailable().observe(getViewLifecycleOwner(), value -> {
            nearestCard.mobilePaymentText.setVisibility(value.status == Resource.Status.LOADING ? View.INVISIBLE : View.VISIBLE);
            nearestCard.mobilePaymentProgressBar.setVisibility(value.status != Resource.Status.LOADING ? View.GONE : View.VISIBLE);

            nearestCard.mobilePaymentText.setText(value.data != null && value.data.papAvailable() ? R.string.mobile_payment_accepted : R.string.mobile_payment_not_accepted);
            nearestCard.imgMobilePayment.setImageResource(value.data != null && value.data.papAvailable() ? R.drawable.ic_check : R.drawable.ic_close);

            if (value.data != null && value.data.fuelUpAvailable()) {
                    HomeNavigationDirections.ActionToSelectPumpFragment action = SelectPumpFragmentDirections.actionToSelectPumpFragment(
                            value.data.nearestStation.data.getStation().getId(),
                            getString(R.string.action_location, value.data.nearestStation.data.getStation().getAddress().getAddressLine()));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            } else if (value.status != Resource.Status.LOADING) {
                isLoading.set(false);
            }
        });

        return view;
    }

    private View setupLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentNearestStationBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        nearestCard = binding.nearestCard;

        nearestCard.getRoot().setOnClickListener((v) -> {
            if (mViewModel.nearestStation.getValue().data != null && !mViewModel.isLoading.get()) {
                if (binding.scrollView.getScrollY() > binding.nearestCard.getRoot().getTop() - 200) {
                    binding.scrollView.smoothScrollTo(0, binding.nearestCard.getRoot().getTop() - 200);
                    binding.scrollView.postDelayed(() -> {
                        StationDetailsDialog.showCard(this, mViewModel.nearestStation.getValue().data, nearestCard.getRoot(), false);
                    }, 100);
                } else {
                    StationDetailsDialog.showCard(this, mViewModel.nearestStation.getValue().data, nearestCard.getRoot(), false);
                }
            }
        });

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.refreshLayout.setColorSchemeResources(R.color.red);
        binding.refreshLayout.setOnRefreshListener(this);

        return binding.getRoot();
    }

    private void showRequestLocationDialog(boolean previouselyDeniedWithNeverASk) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        AnalyticsUtils.logEvent(getActivity().getApplicationContext(), AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                new Pair<>(AnalyticsUtils.Param.formName, "Nearest Station"));
        adb.setTitle(R.string.enable_location_dialog_title);
        adb.setMessage(R.string.enable_location_dialog_message);
        adb.setNegativeButton(R.string.cancel, (dialog, which) -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), AnalyticsUtils.Event.alertInteraction,
                    new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                    new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.cancel)),
                    new Pair<>(AnalyticsUtils.Param.formName, "Nearest Station")
            );
        });
        adb.setPositiveButton(R.string.ok, (dialog, which) -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), AnalyticsUtils.Event.alertInteraction,
                    new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                    new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.ok)),
                    new Pair<>(AnalyticsUtils.Param.formName, "Nearest Station")
            );
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
    public void onStart() {
        super.onStart();
        checkAndRequestPermission();

        if (mViewModel.isUserLoggedIn()) {
            getView().post(() -> {
                int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
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



    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        // TODO: Implement refreshing
        binding.refreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "offsite-nearest-station");
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }

}

