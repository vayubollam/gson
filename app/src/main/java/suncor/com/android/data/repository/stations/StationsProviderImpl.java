package suncor.com.android.data.repository.stations;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.Timber;

@Singleton
public class StationsProviderImpl implements StationsProvider {

    private static final String BASE_PATH = "/adapters/suncor/v1/locations";

    @Override
    public LiveData<Resource<ArrayList<Station>>> getStations(LatLngBounds bounds) {
        Timber.d("Retrieving stations for :" + bounds);

        MutableLiveData<Resource<ArrayList<Station>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterURI = new URI(BASE_PATH + "?southWestLat=" + bounds.southwest.latitude + "&southWestLong=" + bounds.southwest.longitude + "0&northEastLat=" + bounds.northeast.latitude + "&northEastLong=" + bounds.northeast.longitude);
            WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Locations API response:\n" + jsonText);

                    try {
                        Gson gson = new Gson();
                        Station[] stations = gson.fromJson(jsonText, Station[].class);
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(stations))));
                    } catch (JsonSyntaxException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        Timber.e("Retrieving locations failed due to " + e.toString());
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.e("Retrieving locations failed due to " + wlFailResponse.toString());
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
