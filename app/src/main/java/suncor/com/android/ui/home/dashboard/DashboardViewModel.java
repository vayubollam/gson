package suncor.com.android.ui.home.dashboard;

import android.util.Log;

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

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.model.Station;

public class DashboardViewModel extends ViewModel {

    public MutableLiveData<Station> nearest_station = new MutableLiveData<>();


    public MutableLiveData<Station> getNearestStation() {
        URI adapterPath = null;
        try {
            adapterPath = new URI("/adapters/suncor/v1/locations?southWestLat=" + 0.0 + "&southWestLong=" + 0.0 + "0&northEastLat=" + 0.0 + "&northEastLong=" + 0.0);
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

                    JSONObject jo = jsonArray.getJSONObject(0);
                    Station station = gson.fromJson(String.valueOf(jo), Station.class);

                    nearest_station.postValue(station);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.d("mfp_error", wlFailResponse.getErrorMsg());

            }
        });
        return nearest_station;
    }

}
