package suncor.com.android.fragments;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import java.util.Comparator;
import java.util.HashMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.dataObjects.Resource;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.utilities.LocationUtils;

public class StationsViewModel extends ViewModel {

    private final static int DEFAULT_DISTANCE_API = 25000;
    public final static int DEFAULT_MAP_ZOOM = 5000;


    private ArrayList<StationViewModel> cachedStations;
    private LatLngBounds cachedStationsBounds;

    public MutableLiveData<Resource<ArrayList<StationViewModel>>> stationsAround = new MutableLiveData<>();
    public MutableLiveData<StationViewModel> selectedStation = new MutableLiveData<>();
    public LatLng userLocation;
    public LatLngBounds visibleBounds;
    private float regionRatio = 1f;


    public void refreshStations(LatLng mapCenter, LatLngBounds bounds) {
        if (userLocation == null)
            return;
        if (bounds != null && cachedStationsBounds != null && cachedStationsBounds.contains(bounds.northeast) && cachedStationsBounds.contains(bounds.southwest)) {
            visibleBounds = bounds;
            stationsAround.postValue(Resource.success(filterStations(bounds)));
        } else {
            stationsAround.postValue(Resource.loading(null));
            if (bounds != null) {
                visibleBounds = bounds;
            }
            LatLngBounds _25KmBounds = LocationUtils.calculateBounds(mapCenter, DEFAULT_DISTANCE_API, regionRatio);
            LatLngBounds apiBounds = LocationUtils.getLargerBounds(visibleBounds, _25KmBounds);
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
                        ArrayList<StationViewModel> stations = new ArrayList<>();
                        stations.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            Station station = gson.fromJson(jo.toString(), Station.class);
                            stations.add(new StationViewModel(station));
                        }
                        Collections.sort(stations, (o1, o2) -> {
                            double distance1 = LocationUtils.calculateDistance(userLocation, new LatLng(o1.station.get().getAddress().getLatitude(), o1.station.get().getAddress().getLongitude()));
                            double distance2 = LocationUtils.calculateDistance(userLocation, new LatLng(o2.station.get().getAddress().getLatitude(), o2.station.get().getAddress().getLongitude()));
                            return (int) (distance1 - distance2);
                        });
                        cachedStationsBounds = apiBounds;
                        cachedStations = stations;
                        stationsAround.postValue(Resource.success(filterStations(visibleBounds)));
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
    }

    private ArrayList<StationViewModel> filterStations(LatLngBounds bounds) {
        if (bounds.equals(cachedStationsBounds)) {
            return cachedStations;
        }
        ArrayList<StationViewModel> stations = new ArrayList<>();
        for (StationViewModel stationViewModel : cachedStations) {
            if (bounds.contains(new LatLng(stationViewModel.station.get().getAddress().getLatitude(), stationViewModel.station.get().getAddress().getLongitude()))) {
                stations.add(stationViewModel);
            }
        }
        return stations;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        visibleBounds = LocationUtils.calculateBounds(userLocation, DEFAULT_MAP_ZOOM, regionRatio);
        refreshStations(userLocation, null);
    }

    public void setRegionRatio(float screenRatio) {
        this.regionRatio = screenRatio;
    }
}


