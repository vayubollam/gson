package suncor.com.android.ui.home.stationlocator.search;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.PlaceSuggestionsProvider;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.LocationUtils;

public class SearchViewModel extends ViewModel {

    private final static int DEFAULT_DISTANCE_API = 25000;
    public MutableLiveData<Resource<ArrayList<StationNearbyItem>>> nearbyStations = new MutableLiveData<>();
    public LiveData<Resource<ArrayList<PlaceSuggestion>>> placeSuggestions;
    private LatLng userLocation;
    private float regionRatio = 1f;

    public MutableLiveData<String> query = new MutableLiveData<>();

    public SearchViewModel(PlaceSuggestionsProvider suggestionsProvider) {
        query.setValue("");
        placeSuggestions = Transformations.switchMap(query, (suggestionsProvider::getSuggestions));
    }

    public void refreshStations(LatLng mapCenter) {
        if (userLocation == null)
            return;
        nearbyStations.postValue(Resource.loading(null));
        LatLngBounds _25KmBounds = LocationUtils.calculateBounds(mapCenter, DEFAULT_DISTANCE_API, regionRatio);
        double southWestLat = _25KmBounds.southwest.latitude;
        double southWestLong = _25KmBounds.southwest.longitude;
        double northEastLat = _25KmBounds.northeast.latitude;
        double northEastLong = _25KmBounds.northeast.longitude;


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
                    ArrayList<StationNearbyItem> stations = new ArrayList<>();
                    stations.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Station station = gson.fromJson(jo.toString(), Station.class);
                        LatLng stationLocation = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                        stations.add(new StationNearbyItem(station, getDistance(userLocation, stationLocation, 1000)));
                    }
                    Collections.sort(stations, (o1, o2) -> {
                        double distance1 = LocationUtils.calculateDistance(userLocation, new LatLng(o1.station.get().getAddress().getLatitude(), o1.station.get().getAddress().getLongitude()));
                        double distance2 = LocationUtils.calculateDistance(userLocation, new LatLng(o2.station.get().getAddress().getLatitude(), o2.station.get().getAddress().getLongitude()));
                        return (int) (distance1 - distance2);
                    });
                    nearbyStations.postValue(Resource.success(stations));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.d("mfp_error", wlFailResponse.getErrorMsg());
            }
        });
    }


    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        refreshStations(userLocation);
    }

    private Double getDistance(LatLng userLocation, LatLng des, int unit) {
        double distance = LocationUtils.calculateDistance(userLocation, new LatLng(des.latitude, des.longitude)) / unit;
        return Double.parseDouble(new DecimalFormat("##.#").format(distance));
    }

    public void setSearchQuery(String input) {
        query.setValue(input);
    }
}
