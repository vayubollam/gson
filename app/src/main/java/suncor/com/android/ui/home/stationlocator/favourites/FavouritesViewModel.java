package suncor.com.android.ui.home.stationlocator.favourites;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;

public class FavouritesViewModel extends ViewModel {

    private FavouriteRepository favouriteRepository;
    private MutableLiveData<ArrayList<StationItem>> _stations = new MutableLiveData<>();
    public LiveData<ArrayList<StationItem>> stations = _stations;
    private LatLng userLocation;

    public FavouritesViewModel(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    public void refreshStations() {
        favouriteRepository.loadFavourites().observeForever(booleanResource -> {
            if (booleanResource.status == Resource.Status.SUCCESS) {

                ArrayList<Station> stations = favouriteRepository.getFavouriteList();
                ArrayList<StationItem> stationItems = new ArrayList<>();
                for (Station station : stations) {
                    StationItem stationItem = new StationItem(station, null);
                    stationItems.add(stationItem);
                }
                StationsComparator comparator = new StationsComparator();
                Collections.sort(stationItems, comparator);
                this._stations.postValue(stationItems);

            }
        });
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public LiveData<Resource<Boolean>> removeStation(StationItem stationItem) {
        int index = _stations.getValue().indexOf(stationItem);
        _stations.getValue().remove(index);
        _stations.postValue(_stations.getValue());
        return Transformations.map(favouriteRepository.removeFavourite(stationItem.getStation()), r -> {
            if (r.status == Resource.Status.ERROR) {
                _stations.getValue().add(index, stationItem);
                _stations.postValue(stations.getValue());
            }
            return r;
        });
    }

    private class StationsComparator implements Comparator<StationItem> {

        @Override
        public int compare(StationItem o1, StationItem o2) {
            int openComparison = Boolean.compare(o2.isOpen(), o1.isOpen());
            if (openComparison != 0) {
                return openComparison;
            } else {
                if (userLocation != null) {
                    double distance1 = LocationUtils.calculateDistance(getUserLocation(), new LatLng(o1.getStation().getAddress().getLatitude(), o1.getStation().getAddress().getLongitude()));
                    double distance2 = LocationUtils.calculateDistance(getUserLocation(), new LatLng(o2.getStation().getAddress().getLatitude(), o2.getStation().getAddress().getLongitude()));
                    return (int) (distance1 - distance2);
                } else {
                    String streetName1 = o1.getStation().getAddress().getAddressLine().replaceFirst("\\d+", "");
                    String streetName2 = o2.getStation().getAddress().getAddressLine().replaceFirst("\\d+", "");
                    return streetName1.compareTo(streetName2);
                }
            }
        }
    }
}
