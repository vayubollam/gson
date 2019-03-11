package suncor.com.android.data.repository.stations;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public interface StationsProvider {
    LiveData<Resource<ArrayList<Station>>> getStations(LatLngBounds bounds);
}
