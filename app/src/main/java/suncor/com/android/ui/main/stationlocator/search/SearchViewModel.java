package suncor.com.android.ui.main.stationlocator.search;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.data.suggestions.PlaceSuggestionsProvider;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.UserLocalSettings;

public class SearchViewModel extends ViewModel {

    private final static int DEFAULT_DISTANCE_API = 25000;
    public LiveData<Resource<ArrayList<PlaceSuggestion>>> placeSuggestions;
    public MutableLiveData<String> query = new MutableLiveData<>();
    private StationsApi stationsApi;
    private MutableLiveData<Resource<ArrayList<StationItem>>> _nearbyStations = new MutableLiveData<>();
    public LiveData<Resource<ArrayList<StationItem>>> nearbyStations = _nearbyStations;
    private PlaceSuggestionsProvider suggestionsProvider;
    private LatLng userLocation;
    private float regionRatio = 1f;
    private ArrayList<RecentSearch> recentSearches = new ArrayList<>();
    private UserLocalSettings userLocalSettings;
    protected Gson gson;

    @Inject
    public SearchViewModel(StationsApi stationsApi, PlaceSuggestionsProvider suggestionsProvider, Gson gson, UserLocalSettings userLocalSettings) {
        this.stationsApi = stationsApi;
        this.userLocalSettings = userLocalSettings;
        this.gson = gson;
        this.suggestionsProvider = suggestionsProvider;
        query.setValue("");
        placeSuggestions = Transformations.switchMap(query, (suggestionsProvider::getSuggestions));
        String recent = userLocalSettings.getString(UserLocalSettings.RECENTLY_SEARCHED);
        if (recent != null) {
            this.recentSearches.addAll(Arrays.asList(gson.fromJson(recent, RecentSearch[].class)));
        }
    }

    private void refreshNearbyStations(LatLng mapCenter) {
        if (userLocation == null)
            return;
        LatLngBounds _25KmBounds = LocationUtils.calculateBounds(mapCenter, DEFAULT_DISTANCE_API, regionRatio);
        stationsApi.getStations(_25KmBounds, false).observeForever((resource) -> {
            switch (resource.status) {
                case LOADING:
                    _nearbyStations.postValue(Resource.loading());
                    break;
                case ERROR:
                    _nearbyStations.postValue(Resource.error(resource.message));
                    break;
                case SUCCESS:
                    if (resource.data.isEmpty()) {
                        _nearbyStations.postValue(Resource.success(new ArrayList<>()));
                    } else {
                        ArrayList<StationItem> stations = new ArrayList<>();
                        for (Station station : resource.data) {
                            LatLng stationLocation = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                            StationItem item = new StationItem(station, calculateDistance(userLocation, stationLocation));
                            stations.add(item);
                        }
                        Collections.sort(stations, (o1, o2) -> o1.getDistanceDuration().getDistance() - o2.getDistanceDuration().getDistance());
                        _nearbyStations.postValue(Resource.success(stations));
                    }
                    break;
            }
        });
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        refreshNearbyStations(userLocation);
    }

    public boolean isRecentSearchEmpty() {
        return recentSearches.size() == 0;
    }

    public void addToRecentSearched(RecentSearch recentSearch) {
        if (recentSearches.contains(recentSearch)) {
            recentSearches.remove(recentSearch);
        }
        recentSearches.add(0, recentSearch);
        if (recentSearches.size() > 3)
            userLocalSettings.setString(UserLocalSettings.RECENTLY_SEARCHED, gson.toJson(this.recentSearches.subList(0, 3)));
        else
            userLocalSettings.setString(UserLocalSettings.RECENTLY_SEARCHED, gson.toJson(this.recentSearches));
    }

    public LiveData<Resource<LatLng>> getCoordinatesOfPlace(PlaceSuggestion suggestion) {
        return suggestionsProvider.getCoordinatesOfPlace(suggestion);
    }

    private DirectionsResult calculateDistance(LatLng userLocation, LatLng des) {
        return new DirectionsResult((int) LocationUtils.calculateDistance(userLocation, new LatLng(des.latitude, des.longitude)), 0);
    }

    public void setSearchQuery(String input) {
        query.setValue(input);
    }

    public ArrayList<RecentSearch> getRecentSearches() {
        return recentSearches;
    }

    public void setRecentSearches(ArrayList<RecentSearch> recentSearches) {
        this.recentSearches = recentSearches;
    }
}
