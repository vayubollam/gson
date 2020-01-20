package suncor.com.android.data.favourite;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

import javax.inject.Singleton;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.utilities.Timber;

@Singleton
public class FavouriteRepositoryImpl implements FavouriteRepository {

    private final ArrayList<Station> FAVOURITES = new ArrayList<>();

    private SessionManager sessionManager;
    private Gson gson;

    private URI adapterURI;

    private MutableLiveData<Boolean> isLoaded = new MutableLiveData<>();
    private boolean loading;

    public FavouriteRepositoryImpl(SessionManager sessionManager, Gson gson) {
        try {
            adapterURI = new URI("/adapters/suncor/v2/favourite-stations");
            this.sessionManager = sessionManager;
            this.gson = gson;
            isLoaded.setValue(false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void observeSessionChanges() {
        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_IN && !isLoaded.getValue()) {
                if (!loading) {
                    Timber.d("Loading favourites on login");
                    loadFavourites();
                }
            }
            if (state == SessionManager.LoginState.LOGGED_OUT) {
                Timber.d("Clearing favourites due to logging out");
                FAVOURITES.clear();
                isLoaded.postValue(false);
            }
        });
    }

    @Override
    public LiveData<Resource<Boolean>> loadFavourites() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        loading = true;
        WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
        Timber.d("Loading favourites");
        request.send(new WLResponseListener() {
            @Override
            public void onSuccess(WLResponse wlResponse) {
                Timber.d("Loading favourites succeeded");

                loading = false;
                String jsonText = wlResponse.getResponseText();

                try {
                    final JSONArray jsonArray = new JSONArray(jsonText);
                    ArrayList<Station> stations = new ArrayList<>();
                    stations.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Station station = gson.fromJson(jo.toString(), Station.class);

                        stations.add(station);
                    }
                    FAVOURITES.clear();
                    FAVOURITES.addAll(stations);
                    isLoaded.postValue(true);
                    result.postValue(Resource.success(true));
                } catch (JSONException e) {
                    e.printStackTrace();
                    result.postValue(Resource.error(e.getMessage(), false));
                }
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                loading = false;
                Timber.d("Loading favourites failed");
                Timber.d("mfp_error: " + wlFailResponse.getErrorMsg());
                result.postValue(Resource.error(wlFailResponse.getErrorMsg(), false));
            }
        });

        return result;
    }

    @Override
    public LiveData<Boolean> isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean isFavourite(Station station) {
        return FAVOURITES.contains(station);
    }

    @Override
    public ArrayList<Station> getFavouriteList() {
        return FAVOURITES;
    }

    @Override
    public LiveData<Resource<Boolean>> addFavourite(Station station) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        try {
            WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            JSONObject body = new JSONObject();
            body.put("encryptedEntityId", station.getId());
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    FAVOURITES.add(station);
                    isLoaded.postValue(true);
                    result.postValue(Resource.success(true));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("mfp_error:" + wlFailResponse.getErrorMsg());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg(), false));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Timber.e(e.getMessage());
            result.postValue(Resource.error(e.getMessage(), false));
        }

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> removeFavourite(Station station) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.DELETE, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
        request.setQueryParameter("encryptedEntityId", String.valueOf(station.getId()));
        request.send(new WLResponseListener() {
            @Override
            public void onSuccess(WLResponse wlResponse) {
                FAVOURITES.remove(station);
                isLoaded.postValue(true);
                result.postValue(Resource.success(true));
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Timber.d("mfp_error:" + wlFailResponse.getErrorMsg());
                result.postValue(Resource.error(wlFailResponse.getErrorMsg(), false));
            }
        });

        return result;
    }
}
