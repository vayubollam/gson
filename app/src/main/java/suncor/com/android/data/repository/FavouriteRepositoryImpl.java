package suncor.com.android.data.repository;

import android.content.Context;
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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class FavouriteRepositoryImpl implements FavouriteRepository {

    private final ArrayList<Station> FAVOURITES = new ArrayList<>();
    private URI adapterURI;

    private MutableLiveData<Boolean> isLoaded = new MutableLiveData<>();
    private boolean loading;

    public FavouriteRepositoryImpl(Context context) {
        try {
            adapterURI = new URI("/adapters/suncor/v1/favourite-stations");
            isLoaded.setValue(false);
            SessionManager.getInstance().getLoginState().observeForever((state) -> {
                if (state == SessionManager.LoginState.LOGGED_IN && !isLoaded.getValue()) {
                    if (!loading) {
                        Log.d(FavouriteRepositoryImpl.this.getClass().getSimpleName(), "Loading favourites on login");
                        loadFavourites();
                    }
                }
                if (state == SessionManager.LoginState.LOGGED_OUT) {
                    Log.d(FavouriteRepositoryImpl.this.getClass().getSimpleName(), "Clearing favourites due to logging out");
                    FAVOURITES.clear();
                    isLoaded.postValue(false);
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LiveData<Resource<Boolean>> loadFavourites() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        loading = true;
        WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.GET);
        Log.d(this.getClass().getSimpleName(), "Loading favourites");
        request.send(new WLResponseListener() {
            @Override
            public void onSuccess(WLResponse wlResponse) {
                Log.d(FavouriteRepositoryImpl.this.getClass().getSimpleName(), "Loading favourites succeeded");

                loading = false;
                String jsonText = wlResponse.getResponseText();

                try {
                    final JSONArray jsonArray = new JSONArray(jsonText);
                    Gson gson = new Gson();
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
                Log.d(FavouriteRepositoryImpl.this.getClass().getSimpleName(), "Loading favourites failed");
                Log.d(FavouriteRepositoryImpl.this.getClass().getSimpleName(), "mfp_error: " + wlFailResponse.getErrorMsg());
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
            WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.POST);
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
                    Log.d(FavouriteRepositoryImpl.class.getSimpleName(), "mfp_error:" + wlFailResponse.getErrorMsg());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg(), false));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(FavouriteRepositoryImpl.class.getSimpleName(), e.getMessage());
            result.postValue(Resource.error(e.getMessage(), false));
        }

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> removeFavourite(Station station) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.DELETE);
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
                Log.d(FavouriteRepositoryImpl.class.getSimpleName(), "mfp_error:" + wlFailResponse.getErrorMsg());
                result.postValue(Resource.error(wlFailResponse.getErrorMsg(), false));
            }
        });

        return result;
    }
}
