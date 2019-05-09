package suncor.com.android.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import androidx.fragment.app.Fragment;

public class LocationUtils {

    public static LatLngBounds calculateBounds(LatLng center, int distanceMeter, float regionRatio) {
        int verticalDistance = (int) (distanceMeter * regionRatio);
        LatLng westBound = SphericalUtil.computeOffsetOrigin(center, distanceMeter / 2, 90.0);
        LatLng eastBound = SphericalUtil.computeOffset(center, distanceMeter / 2, 90.0);
        LatLng northBound = SphericalUtil.computeOffsetOrigin(center, verticalDistance / 2, 180.0);
        LatLng southBound = SphericalUtil.computeOffset(center, verticalDistance / 2, 180.0);

        return new LatLngBounds(
                new LatLng(southBound.latitude, westBound.longitude),
                new LatLng(northBound.latitude, eastBound.longitude));
    }

    public static LatLngBounds calculateSquareBounds(LatLng center, int distanceMeter) {

        LatLng westBound = SphericalUtil.computeOffsetOrigin(center, distanceMeter / 2, 90.0);
        LatLng eastBound = SphericalUtil.computeOffset(center, distanceMeter / 2, 90.0);
        LatLng northBound = SphericalUtil.computeOffsetOrigin(center, distanceMeter / 2, 180.0);
        LatLng southBound = SphericalUtil.computeOffset(center, distanceMeter / 2, 180.0);

        return new LatLngBounds(
                new LatLng(southBound.latitude, westBound.longitude),
                new LatLng(northBound.latitude, eastBound.longitude));
    }



    public static double calculateDistance(LatLng origin, LatLng destination) {
        return SphericalUtil.computeDistanceBetween(origin, destination);
    }

    //Assumes that both bounds have the same center
    public static LatLngBounds getLargerBounds(LatLngBounds bounds1, LatLngBounds bounds2) {
        return bounds1.contains(bounds2.southwest) ? bounds1 : bounds2;
    }

    public static boolean isLocationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    public static double getHorizontalDistance(LatLngBounds lngBounds) {
        LatLng westBound = new LatLng(lngBounds.southwest.latitude, lngBounds.southwest.longitude);
        LatLng eastBound = new LatLng(lngBounds.southwest.latitude, lngBounds.northeast.longitude);

        return calculateDistance(westBound, eastBound);
    }

    public static void openLocationSettings(Fragment fragment, int REQUEST_CHECK_SETTINGS) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationRequest mLocationRequestBalancedPowerAccuracy = LocationRequest.create();
        mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(fragment.getContext()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = result.getResult(ApiException.class);

            } catch (ApiException ex) {
                switch (ex.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                            fragment.startIntentSenderForResult(resolvableApiException.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException intentException) {
                            Timber.w(intentException);
                        } catch (ClassCastException classException) {
                            Timber.w(classException);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        fragment.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        break;
                }
            }
        });
    }
}
