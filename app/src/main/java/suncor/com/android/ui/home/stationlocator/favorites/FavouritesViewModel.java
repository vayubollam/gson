package suncor.com.android.ui.home.stationlocator.favorites;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;

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
                this.stations.postValue(sortStatiosn(stationItems));

            }
        });
    }

    private ArrayList<StationItem> sortStatiosn(ArrayList<StationItem> stationItems) {
        if (userLocation != null) {
            Collections.sort(stationItems, (o1, o2) -> {

                double distance1 = LocationUtils.calculateDistance(getUserLocation(), new LatLng(o1.getStation().getAddress().getLatitude(), o1.getStation().getAddress().getLongitude()));
                double distance2 = LocationUtils.calculateDistance(getUserLocation(), new LatLng(o2.getStation().getAddress().getLatitude(), o2.getStation().getAddress().getLongitude()));
                return (int) (distance1 - distance2);
            });
            Collections.sort(stationItems, (o1, o2) -> {
                int comparison = Boolean.compare(o2.isOpen(), o1.isOpen());
                return comparison;
            });
            return stationItems;
        } else {
            Collections.sort(stationItems, (o1, o2) -> {
                int comparison = Boolean.compare(o2.isOpen(), o1.isOpen());
                return comparison;
            });
            Collections.sort(stationItems, (o1, o2) -> o1.getStation().getAddress().getAddressLine().compareTo(o2.getStation().getAddress().getAddressLine()));
            return stationItems;

        }

    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }
}
