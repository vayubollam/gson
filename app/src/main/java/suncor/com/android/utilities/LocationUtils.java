package suncor.com.android.utilities;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

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
}
