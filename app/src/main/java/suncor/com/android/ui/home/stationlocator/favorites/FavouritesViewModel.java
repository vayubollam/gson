package suncor.com.android.ui.home.stationlocator.favorites;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;

public class FavouritesViewModel extends ViewModel {

    private FavouriteRepository favouriteRepository;
    MutableLiveData<ArrayList<StationItem>> stations = new MutableLiveData<>();
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
                this.stations.postValue(stationItems);

            }
        });
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
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
