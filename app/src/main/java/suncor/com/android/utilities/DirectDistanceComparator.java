package suncor.com.android.utilities;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

import suncor.com.android.ui.home.stationlocator.StationItem;

public class DirectDistanceComparator implements Comparator<StationItem> {

    private LatLng origin;

    public DirectDistanceComparator(LatLng origin) {
        this.origin = origin;
    }

    @Override
    public int compare(StationItem o1, StationItem o2) {
        double distance1 = LocationUtils.calculateDistance(origin, new LatLng(o1.getStation().getAddress().getLatitude(), o1.getStation().getAddress().getLongitude()));
        double distance2 = LocationUtils.calculateDistance(origin, new LatLng(o2.getStation().getAddress().getLatitude(), o2.getStation().getAddress().getLongitude()));
        return (int) (distance1 - distance2);
    }
}
