package suncor.com.android.data.stations;

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
import suncor.com.android.model.station.Station;
import suncor.com.android.utilities.Timber;

@Singleton
public class StationsApiImpl implements StationsApi {

    private static final String GET_LOCATIONS_ADAPTER__PATH = "/adapters/suncor/v3/rfmp-secure/locations";
    private Gson gson;

    public StationsApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ArrayList<Station>>> getStations(LatLngBounds bounds, boolean tryNearest) {
        Timber.d("Retrieving stations for :" + bounds);

        MutableLiveData<Resource<ArrayList<Station>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterURI = new URI(GET_LOCATIONS_ADAPTER__PATH + "?southWestLat=" + bounds.southwest.latitude + "&southWestLong=" + bounds.southwest.longitude + "0&northEastLat=" + bounds.northeast.latitude + "&northEastLong=" + bounds.northeast.longitude + "&tryNearestLocation=" + tryNearest);
            WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Locations API response:\n" + jsonText);

                    try {
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
