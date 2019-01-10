package suncor.com.android.utilities;

import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import suncor.com.android.dataObjects.Station;

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
}
