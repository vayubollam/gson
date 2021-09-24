package suncor.com.android.data.stations;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;

public interface StationsApi {
    LiveData<Resource<ArrayList<Station>>> getStations(LatLngBounds bounds, boolean tryNearest);
}