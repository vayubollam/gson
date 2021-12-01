package suncor.com.android.ui.main.actionmenu;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentActionButtonMenuBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragmentDirections;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpFragmentDirections;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.wallet.payments.add.AddPaymentViewModel;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.PermissionManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static suncor.com.android.utilities.Constants.ACTIVE_SESSION;
import static suncor.com.android.utilities.Constants.AUTHORIZED;
import static suncor.com.android.utilities.Constants.NEW;

public class ActionMenuFragment extends BottomSheetDialogFragment {
    private FragmentActionButtonMenuBinding binding;
    private ActionMenuViewModel viewModel;
    private HomeViewModel homeViewModel;

    private LocationLiveData locationLiveData;

    private boolean inProximity = false;
    private boolean activeSession = false;
    private String storeId;
    private int geoFenceLimit;

    @Inject
    PermissionManager permissionManager;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    public ActionMenuFragment(){
        //do nothing
    };

    @Override
    public void onStart() {
        super.onStart();
        checkAndRequestPermission();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ActionMenuViewModel.class);
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        homeViewModel.locationServiceEnabled.observe(this, (enabled -> {
            if (enabled) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    homeViewModel.isLoading.set(homeViewModel.getUserLocation() == null);
                    locationLiveData.observe(getViewLifecycleOwner(), (location -> homeViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()))));
                }
            }
        }));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActionButtonMenuBinding.inflate(inflater, container, false);

        binding.actionAccountButton.setOnClickListener(view -> {
            AnalyticsUtils.logEvent(getActivity(), AnalyticsUtils.Event.menuTap, new Pair<>(AnalyticsUtils.Param.menuSelection, getString(R.string.action_menu_account)));
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_profile_tab);
            dismiss();
        });
        binding.actionFuelUpButton.setOnClickListener(view -> {
            AnalyticsUtils.logEvent(getActivity(), AnalyticsUtils.Event.menuTap, new Pair<>(AnalyticsUtils.Param.menuSelection, getString(R.string.action_fuel_up)));
            if (activeSession) {
                FuelUpFragmentDirections.ActionFuelUpToFuellingFragment action = FuelUpFragmentDirections.actionFuelUpToFuellingFragment("");
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
                dismiss();
            }
            else if (!inProximity) {
                // Handle Offsite navigation
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_nearestStationFragment);
                dismiss();
            } else {
                // Handle onsite transaction PAP
                HomeNavigationDirections.ActionToSelectPumpFragment action = HomeNavigationDirections.actionToSelectPumpFragment(storeId, binding.actionLocation.getText().toString());
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
                dismiss();
            }
        });
        binding.actionScanCardButton.setOnClickListener(view -> {
            AnalyticsUtils.logEvent(getActivity(), AnalyticsUtils.Event.menuTap, new Pair<>(AnalyticsUtils.Param.menuSelection, getString(R.string.action_menu_scan_card)));

            HomeNavigationDirections.ActionToCardsDetailsFragment action = HomeNavigationDirections.actionToCardsDetailsFragment();
            action.setLoadType(CardsLoadType.PETRO_POINT_ONLY);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            dismiss();
        });
        binding.actionWashCarButton.setOnClickListener(view -> {
            AnalyticsUtils.logEvent(getActivity(), AnalyticsUtils.Event.menuTap, new Pair<>(AnalyticsUtils.Param.menuSelection, getString(R.string.action_menu_wash_car)));

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_carWashFragment);
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activeSession = homeViewModel.activeFuellingSession.get();

        viewModel.getGeoFenceLimit().observe(getViewLifecycleOwner(), result -> this.geoFenceLimit = result );
        viewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                activeSession = result.data.activeSession;

                binding.actionFuelUpButton.setText(activeSession ?
                        result.data.status.equalsIgnoreCase(NEW)
                                || result.data.status.equalsIgnoreCase(AUTHORIZED) ? R.string.fuelling_about_to_begin : R.string.action_fuelling : R.string.action_fuel_up);

                AnalyticsUtils.logEvent(getActivity(), AnalyticsUtils.Event.menuTap, new Pair<>(AnalyticsUtils.Param.menuSelection, activeSession ?
                        result.data.status.equalsIgnoreCase(NEW)
                                || result.data.status.equalsIgnoreCase(AUTHORIZED)? getString(R.string.fuelling_about_to_begin) : getString(R.string.action_fuelling) : getString(R.string.action_fuel_up)));

                binding.actionFuelUpButton.setLoading(activeSession);
            }
        });

        homeViewModel.nearestStation.observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null
                    && result.data.getDistanceDuration() != null && result.data.getDistanceDuration().getDistance() < geoFenceLimit) {
                inProximity = true;
                StationItem nearestStation = result.data;
                storeId = nearestStation.getStation().getId();

                binding.actionLocation.setText(getString(R.string.action_location, nearestStation.getStation().getAddress().getAddressLine()));

                binding.actionLocation.setVisibility(View.VISIBLE);
            } else {
                inProximity = false;
            }
        });
      //  ObserverActiveSession();
    }

    //This is for fixing bottom sheet dialog not fully extended issue.
    //See: https://medium.com/@OguzhanAlpayli/bottom-sheet-dialog-fragment-expanded-full-height-65b725c8309
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(mDialog -> {
            BottomSheetDialog d = (BottomSheetDialog) mDialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void checkAndRequestPermission() {
        permissionManager.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                if (!permissionManager.isAlertShown()) {
                    permissionManager.setAlertShown(true);
                    //showRequestLocationDialog(false);
                }
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                //in case in the future we would show any rational
            }

            @Override
            public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
                //do nothing
            }

            @Override
            public void onPermissionGranted() {
                homeViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
            }
        });
    }

    private void ObserverActiveSession(){
        homeViewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext(), ACTIVE_SESSION ).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                if (result.data.activeSession && result.data.status != null) {
                    if (result.data.status.equalsIgnoreCase(NEW) || result.data.status.equalsIgnoreCase(AUTHORIZED)) {
                        updateFuellingSession(true, getString(R.string.fuelling_about_to_begin));
                    } else if (result.data.status.equals("BeginFueling")) {
                        updateFuellingSession(true, getString(R.string.fueling_up));
                    } else {
                        //todo handle processing and session end state
                        updateFuellingSession(true, getString(R.string.fueling_up));
                    }
                } else {
                    updateFuellingSession(false, getString(R.string.action_fuel_up));
                }
            }
        });
    }

    //Call this method when fuelling state change
    private void updateFuellingSession(boolean isActiveFuellingSession, String stateMessage){
        binding.actionFuelUpButton.setLoading(isActiveFuellingSession);
        binding.actionFuelUpButton.setText(stateMessage);
    }

}
