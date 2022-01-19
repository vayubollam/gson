package suncor.com.android.data;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import suncor.com.android.BuildConfig;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.utilities.Constants;
import suncor.com.android.utilities.Timber;

@Singleton
public class DistanceApi {

    private OkHttpClient client;

    @Inject
    public DistanceApi() {
        client = new OkHttpClient.Builder()
                .connectTimeout(SuncorApplication.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(SuncorApplication.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    public LiveData<Resource<DirectionsResult>> enqueuJob(LatLng origin, LatLng dest) {
        Timber.d("Getting distance from " + origin + " to " + dest);
        MutableLiveData<Resource<DirectionsResult>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));

        String str_origin = "origins=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destinations=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode = driving";
        String mapKey = "key=" + BuildConfig.GOOGLE_API_KEY;
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + mapKey;

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/distancematrix/json?" + parameters)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result.postValue(Resource.error(e.toString(), null));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        String status = jsonObj.getString(Constants.STATUS);
                        Timber.d("Distance result for " + origin + " to " + dest + " is " + status);
                        if (status.equals("OK")) {
                            JSONObject distanceResult = jsonObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                            if ("OK".equals(distanceResult.getString(Constants.STATUS))) {
                                Location locationA = new Location("point A");

                                locationA.setLatitude(origin.latitude);
                                locationA.setLongitude(origin.longitude);

                                Location locationB = new Location("point B");

                                locationB.setLatitude(dest.latitude);
                                locationB.setLongitude(dest.longitude);

                                float distance = locationA.distanceTo(locationB);

                                result.postValue(Resource.success(
                                        new DirectionsResult(
                                                (int) distance,
                                                distanceResult.getJSONObject("duration").getInt("value"))));
                            } else {
                                result.postValue(Resource.error(distanceResult.getString(Constants.STATUS), null));
                            }
                        } else {
                            result.postValue(Resource.error(status, null));
                        }
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
