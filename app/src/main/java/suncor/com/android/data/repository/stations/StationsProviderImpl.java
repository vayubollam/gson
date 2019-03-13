package suncor.com.android.data.repository.stations;

import android.util.Log;

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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class StationsProviderImpl implements StationsProvider {

    private static final String BASE_PATH = "/adapters/suncor/v1/locations";

    @Override
    public LiveData<Resource<ArrayList<Station>>> getStations(LatLngBounds bounds) {
        MutableLiveData<Resource<ArrayList<Station>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterURI = new URI(BASE_PATH + "?southWestLat=" + bounds.southwest.latitude + "&southWestLong=" + bounds.southwest.longitude + "0&northEastLat=" + bounds.northeast.latitude + "&northEastLong=" + bounds.northeast.longitude);
            WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.GET);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    try {
                        final JSONArray jsonArray = new JSONArray(jsonText);
                        Gson gson = new Gson();
                        ArrayList<Station> stations = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            Station station = gson.fromJson(jo.toString(), Station.class);
                            stations.add(station);
                        }
                        result.postValue(Resource.success(stations));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(Resource.error(e.getMessage()));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d(StationsProviderImpl.class.getSimpleName(), wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            result.postValue(Resource.error(e.getMessage()));
        }
        return result;
    }
}
