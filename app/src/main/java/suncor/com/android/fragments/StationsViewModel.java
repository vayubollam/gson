package suncor.com.android.fragments;
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
import java.util.ArrayList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.dataObjects.Station;

public class StationsViewModel extends ViewModel {
    private String result="ALL_STATIONS";
    private static final int RSS_JOB_ID = 1000;

    public  MutableLiveData<ArrayList<Station>> stations_arround =new MutableLiveData<>();



    public MutableLiveData<ArrayList<Station>> getStations() {
        URI adapterPath = null;


        try {
            adapterPath = new URI("/adapters/suncor/v1/locations?southWestLat=0&southWestLong=0&northEastLat=0&northEastLong=0&amenities=PayAtPump;ULTRA94;PAYPASS,PAYWAVE");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
        request.send(new WLResponseListener() {
            @Override
            public void onSuccess(WLResponse wlResponse) {
               String  jsonText = wlResponse.getResponseText();
                try {
                    final JSONArray jsonArray = new JSONArray(jsonText);
                    Gson gson=new Gson();
                    ArrayList<Station> stations=new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jo=jsonArray.getJSONObject(i);
                        Station station=gson.fromJson(String.valueOf(jo),Station.class);
                        stations.add(station);
                    }
                   stations_arround.postValue(stations);
                    for(Station station : stations)
                        Log.d("json_fields",station.getAddress().getPrimaryCity());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("worker_result","job completed");

            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Log.d("mfp_error",wlFailResponse.getErrorMsg());

            }
        });
        return stations_arround;
    }




}


