package suncor.com.android.ui.main.stationlocator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.utilities.DirectDistanceComparator;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.Timber;

public class StationsViewModel extends ViewModel {

    public final static int DEFAULT_MAP_ZOOM = 5000;
    private final static int DEFAULT_DISTANCE_API = 25000;
    private FavouriteRepository favouriteRepository;
    private StationsApi stationsApi;
    private ArrayList<StationItem> cachedStations;
    private LatLngBounds cachedStationsBounds;
    private MutableLiveData<Resource<ArrayList<StationItem>>> _stationsAround = new MutableLiveData<>();
    public LiveData<Resource<ArrayList<StationItem>>> stationsAround = _stationsAround;
    private MutableLiveData<StationItem> _selectedStation = new MutableLiveData<>();
    public LiveData<StationItem> selectedStation = _selectedStation;
    private MutableLiveData<ArrayList<String>> _filters = new MutableLiveData<>();
    public LiveData<ArrayList<String>> filters = _filters;
    private MutableLiveData<LatLng> _userLocation = new MutableLiveData<>();
    public LiveData<LatLng> userLocation = _userLocation;
    private UserLocationType userLocationType;

    private LatLng lastGpsLocation;

    private MutableLiveData<LatLngBounds> _mapBounds = new MutableLiveData<>();
    private Observer<Boolean> favouritesLoadedObserver = b -> refreshFavouriteState();
    public LiveData<LatLngBounds> mapBounds = _mapBounds;

    private MutableLiveData<String> _queryText = new MutableLiveData<>();
    public LiveData<String> queryText = _queryText;

    private StationItem selectedNearbyStationFromSearch = null;

    private float regionRatio = 1f;

    private boolean shouldUpdateSectedStation ;

    @Inject
    public StationsViewModel(StationsApi stationsApi, FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
        this.stationsApi = stationsApi;
        filters.observeForever((l) -> {


            if (stationsAround == null && stationsAround.getValue() == null && stationsAround.getValue().status == null) {
                return;
            }

            if (Resource.Status.SUCCESS ==  null){
               return;

            }
            if (stationsAround.getValue().status != Resource.Status.SUCCESS) {
                return;
            }
            ArrayList<StationItem> filteredStations = filterStations();
            _stationsAround.setValue(Resource.success(filteredStations));
        });

        userLocation.observeForever((location) -> {
            shouldUpdateSectedStation = true;
            _mapBounds.setValue(LocationUtils.calculateBounds(location, DEFAULT_MAP_ZOOM, regionRatio));
        });

        _mapBounds.observeForever((bounds -> {
            refreshStations();
        }));

        favouriteRepository.isLoaded().observeForever(favouritesLoadedObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        favouriteRepository.isLoaded().removeObserver(favouritesLoadedObserver);
    }

    public void refreshStations() {
        Timber.d("refreshing stations");
        LatLng mapCenter = _mapBounds.getValue().getCenter();
        LatLngBounds bounds = _mapBounds.getValue();
        if (userLocation.getValue() == null)
            return;
        if (cachedStationsBounds != null && cachedStationsBounds.contains(bounds.northeast) && cachedStationsBounds.contains(bounds.southwest)) {
            Timber.d("Using cached stations");
            Collections.sort(cachedStations, new DirectDistanceComparator(userLocation.getValue()));
            ArrayList<StationItem> filteredStations = filterStations();

            _stationsAround.setValue(Resource.success(filteredStations));
            shouldUpdateSectedStation = true;
            updateSelectedStationIfNeeded(filteredStations);
        } else {
            Timber.d("Load stations from API");

            _stationsAround.setValue(Resource.loading(null));

            LatLngBounds _25KmBounds = LocationUtils.calculateBounds(mapCenter, DEFAULT_DISTANCE_API, regionRatio);
            LatLngBounds apiBounds = _mapBounds.getValue() != null ? LocationUtils.getLargerBounds(_mapBounds.getValue(), _25KmBounds) : _25KmBounds;

            stationsApi.getStations(apiBounds, false).observeForever((resource) -> {
                switch (resource.status) {
                    case LOADING:
                        _stationsAround.postValue(Resource.loading());
                        break;
                    case ERROR:
                        cachedStations = null;
                        cachedStationsBounds = null;
                        _stationsAround.postValue(Resource.error(resource.message));
                        shouldUpdateSectedStation = false;
                        break;
                    case SUCCESS:
                        if (resource.data.isEmpty()) {
                            _stationsAround.postValue(Resource.success(new ArrayList<>()));
                        } else {
                            shouldUpdateSectedStation = true;
                            ArrayList<StationItem> stations = new ArrayList<>();
                            for (Station station : resource.data) {
                                boolean isFavourite = false;
                                if (favouriteRepository.isLoaded().getValue()) {
                                    isFavourite = favouriteRepository.isFavourite(station);
                                }
                                StationItem item = new StationItem(favouriteRepository, station, isFavourite);
                                stations.add(item);
                            }
                            Collections.sort(stations, new DirectDistanceComparator(userLocation.getValue()));
                            cachedStationsBounds = apiBounds;
                            cachedStations = stations;
                            ArrayList<StationItem> filteredStations = filterStations();
                            _stationsAround.postValue(Resource.success(filteredStations));
                            updateSelectedStationIfNeeded(filteredStations);
                        }
                        break;
                }
            });
        }
    }

    private void updateSelectedStationIfNeeded(ArrayList<StationItem> stationItems) {
        if (shouldUpdateSectedStation && !stationItems.isEmpty()) {
            _selectedStation.postValue(stationItems.get(0));
            shouldUpdateSectedStation = false;
        } else if (selectedNearbyStationFromSearch != null) {
            _selectedStation.postValue(selectedNearbyStationFromSearch);
            selectedNearbyStationFromSearch = null;
        }
    }

    private void refreshFavouriteState() {
        if (cachedStations == null || cachedStations.isEmpty()) {
            return;
        }
        ArrayList<StationItem> stationItems = cachedStations;
        for (StationItem item : stationItems) {
            if (favouriteRepository.isLoaded().getValue()) {
                item.setFavourite(favouriteRepository.isFavourite(item.getStation()));
            } else {
                item.setFavourite(false);
            }
        }
        _stationsAround.postValue(Resource.success(filterStations()));
    }

    private ArrayList<StationItem> filterStations() {
        ArrayList<StationItem> stationsInBound;
        if (_mapBounds.getValue().equals(cachedStationsBounds)) {
            stationsInBound = new ArrayList<>(cachedStations);
        } else {
            stationsInBound = new ArrayList<>();
            //app crashes when cachedStations is null
            if (cachedStations != null) {
                for (StationItem stationItem : cachedStations) {
                    if (_mapBounds.getValue().contains(new LatLng(stationItem.getStation().getAddress().getLatitude(), stationItem.getStation().getAddress().getLongitude()))) {
                        stationsInBound.add(stationItem);
                    }
                }
            }

        }
        return applyAmenitiesFilter(stationsInBound, filters.getValue());
    }

    private ArrayList<StationItem> applyAmenitiesFilter(ArrayList<StationItem> stations, ArrayList<String> filtersList) {
        if (filtersList == null || filtersList.isEmpty()) {
            return stations;
        }
        for (StationItem stationItem : new ArrayList<>(stations)) {
            for (String filter : filtersList) {
                if (!filter.equals(FiltersFragment.CARWASH_ALL_WASHES_KEY)) {
                    if (!stationItem.getStation().getAmenities().contains(filter)) {
                        stations.remove(stationItem);
                        break;
                    }
                } else {
                    //If the current filter is "All Washes", we just check if the station has some wash options, if not we filter it out.
                    if (!stationItem.getStation().hasWashOptions()) {
                        stations.remove(stationItem);
                    }
                }
            }
        }
        return stations;
    }

    public void setCurrentFilters(ArrayList<String> filter) {
        _filters.setValue(filter);
    }

    public void clearFilters() {
        setCurrentFilters(new ArrayList<>());
    }

    public void setUserLocation(LatLng userLocation, UserLocationType userLocationType) {
        this.userLocationType = userLocationType;
        _userLocation.postValue(userLocation);
        if (userLocationType == UserLocationType.GPS) {
            _queryText.postValue("");
            lastGpsLocation = userLocation;
        }
    }

    public LatLng getLastGpsLocation() {
        return lastGpsLocation;
    }

    public void setSelectedStationFromSearch(LatLng userLocation, ArrayList<StationItem> stationItems, StationItem selectedStation) {
        //init cache data to default 25km bounds
        cachedStations = stationItems;
        cachedStationsBounds = LocationUtils.calculateBounds(userLocation, DEFAULT_DISTANCE_API, regionRatio);
        clearFilters();
        int mapHorizontalRange = (int) LocationUtils.getHorizontalDistance(_mapBounds.getValue());
        setUserLocation(userLocation, UserLocationType.GPS);
        LatLng selectedStationPosition = new LatLng(selectedStation.getStation().getAddress().getLatitude(), selectedStation.getStation().getAddress().getLongitude());
        selectedNearbyStationFromSearch = selectedStation;
        _mapBounds.postValue(LocationUtils.calculateBounds(selectedStationPosition, mapHorizontalRange, regionRatio));
    }

    public void setMapBounds(LatLngBounds bounds) {
        _mapBounds.setValue(bounds);
    }

    public void setRegionRatio(float screenRatio) {
        this.regionRatio = screenRatio;
    }

    public void setSelectedStation(StationItem station) {
        _selectedStation.setValue(station);
    }

    public UserLocationType getUserLocationType() {
        return userLocationType;
    }

    public void setTextQuery(String query) {
        _queryText.postValue(query);
    }

    public enum UserLocationType {
        GPS, SEARCH
    }
}


