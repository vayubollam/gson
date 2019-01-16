package suncor.com.android.utilities;

import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import suncor.com.android.SuncorApplication;

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

    public static double calculateDistance(LatLng origin, LatLng destination) {
        return SphericalUtil.computeDistanceBetween(origin, destination);
    }

    //Assumes that both bounds have the same center
    public static LatLngBounds getLargerBounds(LatLngBounds bounds1, LatLngBounds bounds2) {
        return bounds1.contains(bounds2.southwest) ? bounds1 : bounds2;
    }

    public static boolean isLocationEnabled() {
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(SuncorApplication.getInstance().getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

}
