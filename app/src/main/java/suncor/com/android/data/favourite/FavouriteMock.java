package suncor.com.android.data.favourite;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;

import static suncor.com.android.utilities.CommonUtils.getMockResponse;

public class FavouriteMock implements FavouriteRepository {

    private final static ArrayList<Station> FAVOURITES = new ArrayList<>();

    private static MutableLiveData<Boolean> isLoaded = new MutableLiveData<>();

    static {
        isLoaded.setValue(false);
    }

    @Override
    public void observeSessionChanges() {
        //do nothing
    }

    @Override

    synchronized public LiveData<Resource<Boolean>> loadFavourites() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    FAVOURITES.clear();
                    FAVOURITES.addAll(mockFavourites());
                    result.postValue(Resource.success(true));
                    isLoaded.postValue(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

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
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    FAVOURITES.add(station);
                    result.postValue(Resource.success(true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        return result;
    }

    @Override
    public LiveData<Resource<Boolean>> removeFavourite(Station station) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    FAVOURITES.remove(station);
                    result.postValue(Resource.success(true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        return result;
    }

    private ArrayList<Station> mockFavourites() {
        String json = getMockResponse(null, "favouritesApiResponse.json");

        try {
            JSONArray jsonArray = new JSONArray(json);
            ArrayList<Station> stations = new ArrayList<>();
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                Station station = gson.fromJson(jo.toString(), Station.class);
                stations.add(station);
            }
            return stations;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
