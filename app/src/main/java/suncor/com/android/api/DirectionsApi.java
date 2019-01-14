package suncor.com.android.api;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;

public class DirectionsApi {

    private static DirectionsApi sInstance;
    private OkHttpClient client;

    private DirectionsApi() {
        client = new OkHttpClient();
    }

    public static DirectionsApi getInstance() {
        if (sInstance == null) {
            sInstance = new DirectionsApi();
        }
        return sInstance;
    }

    public LiveData<Resource<DirectionsResult>> enqueuJob(LatLng origin, LatLng dest) {
        MutableLiveData<Resource<DirectionsResult>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode = driving";
        String mapKey = "key=AIzaSyAtC2AuQA0e-jJYbMrteC06unYKysCa1tA";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + mapKey;


        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/directions/json?" + parameters)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result.postValue(Resource.error(e.getMessage(), null));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        JSONArray legs = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                        JSONObject jo = legs.getJSONObject(0);
                        String distance = jo.getJSONObject("distance").getString("text");
                        String duration = jo.getJSONObject("duration").getString("text");
                        result.postValue(Resource.success(new DirectionsResult(distance, duration)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(Resource.error(e.getMessage(), null));
                    }
                } else {
                    result.postValue(Resource.error(response.message(), null));
                }
            }
        });

        return result;
    }
}
