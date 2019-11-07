package suncor.com.android.ui.main.actionmenu;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentActionButtonMenuBinding;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.PermissionManager;
import suncor.com.android.utilities.UserLocalSettings;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ActionMenuFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private LocationLiveData locationLiveData;
    private LatLng currentLocation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        locationLiveData.observe(this, location -> {
            Log.i("TTT", "i am here");
            currentLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentActionButtonMenuBinding binding = FragmentActionButtonMenuBinding.inflate(inflater, container, false);
        binding.actionAccountButton.setOnClickListener(view -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_profile_tab);
            dismiss();
        });
        binding.actionScanCardButton.setOnClickListener(view -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_cardsDetailsFragment);
            dismiss();
        });
        binding.actionWashCarButton.setOnClickListener(view -> {
            if (checkLocationPermission()) {
                Station station = ((MainActivity) getActivity()).getNearestCarWashStation();
                LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                LatLng origin = new LatLng(currentLocation.latitude, currentLocation.longitude);
                Log.i("TTT", "distance is " + LocationUtils.calculateDistance(dest, origin));
                if (LocationUtils.calculateDistance(dest, origin) < 70 && station.getCarWashType().equals("Doesn't accept Season Pass or Wash & Go")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("This is an independently owned car wash")
                            .setMessage("Petro-Canada car wash cards and tickets are not supported at this location. You can still purchase a car wash ticket at this location in order to wash your car.")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_carWashFragment);
                    dismiss();
                }
            } else {
                showRequestLocationDialog(false);
            }


        });

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                if (LocationUtils.isLocationEnabled(getContext())) {
                    Log.i("TTT", "user enabled location service");
                } else {
                    LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS);
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

}
