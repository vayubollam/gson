package suncor.com.android.ui.home.stationlocator;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.LocationUtils;

public class StationsViewModel extends ViewModel {

    private final static int DEFAULT_DISTANCE_API = 25000;
    public final static int DEFAULT_MAP_ZOOM = 5000;

    private FavouriteRepository favouriteRepository;

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

    private MutableLiveData<LatLngBounds> _mapBounds = new MutableLiveData<>();
    public LiveData<LatLngBounds> mapBounds = _mapBounds;

    private MutableLiveData<String> _queryText = new MutableLiveData<>();
    public LiveData<String> queryText = _queryText;

    private float regionRatio = 1f;

    private boolean shouldUpdateSectedStation;

    public StationsViewModel(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
        filters.observeForever((l) -> {
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
    }


    private void refreshStations() {
        LatLng mapCenter = _mapBounds.getValue().getCenter();
        LatLngBounds bounds = _mapBounds.getValue();
        if (userLocation.getValue() == null)
            return;
        if (cachedStationsBounds != null && cachedStationsBounds.contains(bounds.northeast) && cachedStationsBounds.contains(bounds.southwest)) {
            ArrayList<StationItem> filteredStations = filterStations();
            _stationsAround.setValue(Resource.success(filteredStations));

            if (shouldUpdateSectedStation && !filteredStations.isEmpty()) {
                _selectedStation.postValue(filteredStations.get(0));
            }
            shouldUpdateSectedStation = false;
        } else {
            _stationsAround.setValue(Resource.loading(null));

            LatLngBounds _25KmBounds = LocationUtils.calculateBounds(mapCenter, DEFAULT_DISTANCE_API, regionRatio);
            LatLngBounds apiBounds = _mapBounds.getValue() != null ? LocationUtils.getLargerBounds(_mapBounds.getValue(), _25KmBounds) : _25KmBounds;

            double southWestLat = apiBounds.southwest.latitude;
            double southWestLong = apiBounds.southwest.longitude;
            double northEastLat = apiBounds.northeast.latitude;
            double northEastLong = apiBounds.northeast.longitude;


            URI adapterPath = null;
            try {
                adapterPath = new URI("/adapters/suncor/v1/locations?southWestLat=" + southWestLat + "&southWestLong=" + southWestLong + "0&northEastLat=" + northEastLat + "&northEastLong=" + northEastLong + "&amenities=PayAtPump;ULTRA94;PAYPASS,PAYWAVE");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();

                    try {
                        final JSONArray jsonArray = new JSONArray(jsonText);
                        Gson gson = new Gson();
                        ArrayList<StationItem> stations = new ArrayList<>();
                        stations.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            Station station = gson.fromJson(jo.toString(), Station.class);
                            boolean isFavourite = false;
                            if (favouriteRepository.isLoaded()) {
                                isFavourite = favouriteRepository.isFavourite(station);
                            }
                            StationItem item = new StationItem(favouriteRepository, station, isFavourite);
                            stations.add(item);
                        }
                        Collections.sort(stations, (o1, o2) -> {
                            double distance1 = LocationUtils.calculateDistance(userLocation.getValue(), new LatLng(o1.getStation().getAddress().getLatitude(), o1.getStation().getAddress().getLongitude()));
                            double distance2 = LocationUtils.calculateDistance(userLocation.getValue(), new LatLng(o2.getStation().getAddress().getLatitude(), o2.getStation().getAddress().getLongitude()));
                            return (int) (distance1 - distance2);
                        });
                        cachedStationsBounds = apiBounds;
                        cachedStations = stations;
                        ArrayList<StationItem> filteredStations = filterStations();
                        _stationsAround.postValue(Resource.success(filteredStations));
                        if (shouldUpdateSectedStation && !filteredStations.isEmpty()) {
                            _selectedStation.postValue(filteredStations.get(0));
                        }
                        shouldUpdateSectedStation = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        _stationsAround.postValue(Resource.error(e.getMessage(), null));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d("mfp_error", wlFailResponse.getErrorMsg());
                    _stationsAround.postValue(Resource.error(wlFailResponse.getErrorMsg(), null));
                    shouldUpdateSectedStation = false;
                }
            });
        }
    }

    private ArrayList<StationItem> filterStations() {
        ArrayList<StationItem> stationsInBound;
        if (_mapBounds.getValue().equals(cachedStationsBounds)) {
            stationsInBound = new ArrayList<>(cachedStations);
        } else {
            stationsInBound = new ArrayList<>();
            for (StationItem stationItem : cachedStations) {
                if (_mapBounds.getValue().contains(new LatLng(stationItem.getStation().getAddress().getLatitude(), stationItem.getStation().getAddress().getLongitude()))) {
                    stationsInBound.add(stationItem);
                }
            }
        }
        return applyAmenitiesFilter(stationsInBound, filters.getValue());
    }

    private ArrayList<StationItem> applyAmenitiesFilter(ArrayList<StationItem> stations, ArrayList<String> currentFilter) {
        if (currentFilter == null || currentFilter.isEmpty()) {
            return stations;
        }
        for (StationItem stationItem : new ArrayList<>(stations)) {
            for (String filter : currentFilter) {
                if (!stationItem.getStation().getAmenities().contains(filter)) {
                    stations.remove(stationItem);
                    break;
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
        }
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


