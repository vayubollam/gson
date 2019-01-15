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
import java.util.ArrayList;
import java.util.Collections;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;

public class SearchViewModel extends ViewModel {

    private final static int DEFAULT_DISTANCE_API = 25000;
    public final static int DEFAULT_MAP_ZOOM = 5000;


    private ArrayList<StationItem> cachedStations;
    private LatLngBounds cachedStationsBounds;

    public MutableLiveData<Resource<ArrayList<StationItem>>> stationsAround = new MutableLiveData<>();
    public MutableLiveData<StationItem> selectedStation = new MutableLiveData<>();
    public LatLng userLocation;
    public LatLngBounds visibleBounds;
    private float regionRatio = 1f;
    public MutableLiveData<Boolean> isBusy=new MutableLiveData<>();


    public void refreshStations(LatLng mapCenter, LatLngBounds bounds) {
        isBusy.postValue(true);
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
                        ArrayList<StationItem> stations = new ArrayList<>();
                        stations.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            Station station = gson.fromJson(jo.toString(), Station.class);
                            stations.add(new StationItem(station));
                        }
                        Collections.sort(stations, (o1, o2) -> {
                            double distance1 = LocationUtils.calculateDistance(userLocation, new LatLng(o1.station.get().getAddress().getLatitude(), o1.station.get().getAddress().getLongitude()));
                            double distance2 = LocationUtils.calculateDistance(userLocation, new LatLng(o2.station.get().getAddress().getLatitude(), o2.station.get().getAddress().getLongitude()));
                            return (int) (distance1 - distance2);
                        });
                        cachedStationsBounds = apiBounds;
                        cachedStations = stations;
                        stationsAround.postValue(Resource.success(stations));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isBusy.postValue(false);
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d("mfp_error", wlFailResponse.getErrorMsg());
                    isBusy.postValue(false);
                }
            });
        }
    }

    private ArrayList<StationItem> filterStations(LatLngBounds bounds) {
        if (bounds.equals(cachedStationsBounds)) {
            return cachedStations;
        }
        ArrayList<StationItem> stations = new ArrayList<>();
        for (StationItem stationItem : cachedStations) {
            if (bounds.contains(new LatLng(stationItem.station.get().getAddress().getLatitude(), stationItem.station.get().getAddress().getLongitude()))) {
                stations.add(stationItem);
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
