package suncor.com.android.fragments;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
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
import java.util.HashMap;
import java.util.Objects;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dialogs.LocationDialog;

public class StationsViewModel extends ViewModel {

    public MutableLiveData<ArrayList<Station>> stationsAround = new MutableLiveData<>();
    public MutableLiveData<Marker> selectedMarker=new MutableLiveData<>();
    public MutableLiveData<Boolean> stillInRegion=new MutableLiveData<>();
    public LatLngBounds lastLatLngBounds=null;
    public MutableLiveData<HashMap<Marker,Station>> stationMarkers=new MutableLiveData<>();
    public MutableLiveData<Marker> lastMarker=new MutableLiveData<>();
    public MutableLiveData<Integer> stationPosition=new MutableLiveData<>();
    public MutableLiveData<Boolean> shouldHideCards=new MutableLiveData<>();
    public Boolean animatingToUserLocation=false;


    public void refreshStations(GoogleMap googleMap) {
         LatLngBounds latLngBounds=getRegion(googleMap);
        double southWestLat=latLngBounds.southwest.latitude;
        double southWestLong=latLngBounds.southwest.longitude;
        double northEastLat=latLngBounds.northeast.latitude;
        double northEastLong=latLngBounds.northeast.longitude;


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
                    ArrayList<Station> stations = new ArrayList<>();
                    stations.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Station station = gson.fromJson(String.valueOf(jo), Station.class);
                        stations.add(station);
                    }
                    stationsAround.postValue(stations);


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

    public void checkRegion(GoogleMap googleMap)
    {

        LatLngBounds currentBounds=getRegion(googleMap);
        if(lastLatLngBounds==null){
            lastLatLngBounds=currentBounds;
            stillInRegion.setValue(false);

        }else {
            if(lastLatLngBounds.contains(currentBounds.northeast) && lastLatLngBounds.contains(currentBounds.southwest)){
                stillInRegion.setValue(true);

            }else{
                lastLatLngBounds=currentBounds;
                stillInRegion.setValue(false);
                lastMarker.setValue(null);
                selectedMarker.setValue(null);
            }
            if(animatingToUserLocation)
            {
                shouldHideCards.setValue(false);
                animatingToUserLocation=false;
            }else {
                shouldHideCards.setValue(true);
            }
    }
    }


    public void CheckMarker(Marker marker){
         if(lastMarker.getValue()==null){
             Objects.requireNonNull(lastMarker).setValue(marker);
             selectedMarker.setValue(marker);
         }else{
                 lastMarker.setValue(selectedMarker.getValue());
                 selectedMarker.setValue(marker);

         }

         stationPosition.setValue(stationsAround.getValue().indexOf(stationMarkers.getValue().get(marker)));
    }


    private LatLngBounds getRegion(GoogleMap mGoogleMap){
        VisibleRegion vr = mGoogleMap.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double top = vr.latLngBounds.northeast.latitude;
        double right = vr.latLngBounds.northeast.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;
        return new LatLngBounds(new LatLng(bottom,left),new LatLng(top,right));
    }
    public void alertUser(FragmentManager fragmentManager) {
        LocationDialog dialogFragment = new LocationDialog ();
            dialogFragment.show(fragmentManager, "location dialog");
        }

}


