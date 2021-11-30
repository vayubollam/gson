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
        String json = "[\n" +
                "  {\n" +
                "    \"amenities\": [\n" +
                "      \"electricChargingStation\",\n" +
                "      \"lottery\",\n" +
                "      \"ultra94\",\n" +
                "      \"diesel\",\n" +
                "      \"bankMachine\",\n" +
                "      \"open24Hours\",\n" +
                "      \"convenienceStore\"\n" +
                "    ],\n" +
                "    \"hours\": [\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"address\": {\n" +
                "      \"subdivision\": \"Ontario\",\n" +
                "      \"phone\": \"4163912903\",\n" +
                "      \"countryRegion\": \"Canada\",\n" +
                "      \"postalCode\": \"V8C1V6\",\n" +
                "      \"latitude\": 43.736993,\n" +
                "      \"addressLine\": \"1095 Don Mills Road test 4351\",\n" +
                "      \"crossStreet\": \"LAWRENCE\",\n" +
                "      \"primaryCity\": \"York1\",\n" +
                "      \"longitude\": -79.343357\n" +
                "    },\n" +
                "    \"id\": 182\n" +
                "  },\n" +
                "  {\n" +
                "    \"amenities\": [\n" +
                "      \"lottery\",\n" +
                "      \"ultra94\",\n" +
                "      \"diesel\",\n" +
                "      \"bankMachine\",\n" +
                "      \"open24Hours\",\n" +
                "      \"convenienceStore\"\n" +
                "    ],\n" +
                "    \"hours\": [\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"address\": {\n" +
                "      \"subdivision\": \"Ontario\",\n" +
                "      \"phone\": \"4167404027\",\n" +
                "      \"countryRegion\": \"Canada\",\n" +
                "      \"postalCode\": \"V8C1V6\",\n" +
                "      \"latitude\": 43.757013,\n" +
                "      \"addressLine\": \"3900 Jane Street\",\n" +
                "      \"crossStreet\": \"FINCH\",\n" +
                "      \"primaryCity\": \"North York\",\n" +
                "      \"longitude\": -79.517663\n" +
                "    },\n" +
                "    \"id\": 194\n" +
                "  },\n" +
                "  {\n" +
                "    \"amenities\": [\n" +
                "      \"electricChargingStation\",\n" +
                "      \"lottery\",\n" +
                "      \"ultra94\",\n" +
                "      \"diesel\",\n" +
                "      \"open24Hours\",\n" +
                "      \"convenienceStore\"\n" +
                "    ],\n" +
                "    \"hours\": [\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"2400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"address\": {\n" +
                "      \"subdivision\": \"Ontario\",\n" +
                "      \"phone\": \"4162983974\",\n" +
                "      \"countryRegion\": \"Canada\",\n" +
                "      \"postalCode\": \"V8C1V6\",\n" +
                "      \"latitude\": 43.823675,\n" +
                "      \"addressLine\": \"4575 Steeles Avenue East\",\n" +
                "      \"crossStreet\": \"KENNEDY\",\n" +
                "      \"primaryCity\": \"Scarborough\",\n" +
                "      \"longitude\": -79.307007\n" +
                "    },\n" +
                "    \"id\": 445\n" +
                "  },\n" +
                "  {\n" +
                "    \"amenities\": [\n" +
                "      \"electricChargingStation\",\n" +
                "      \"lottery\",\n" +
                "      \"ultra94\",\n" +
                "      \"diesel\",\n" +
                "      \"open24Hours\",\n" +
                "      \"convenienceStore\"\n" +
                "    ],\n" +
                "    \"hours\": [\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"close\": \"1400\",\n" +
                "        \"open\": \"0000\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"address\": {\n" +
                "      \"subdivision\": \"Ontario\",\n" +
                "      \"phone\": \"4162983974\",\n" +
                "      \"countryRegion\": \"Canada\",\n" +
                "      \"postalCode\": \"V8C1V6\",\n" +
                "      \"latitude\": 43.823675,\n" +
                "      \"addressLine\": \"4575 Steeles Avenue East\",\n" +
                "      \"crossStreet\": \"KENNEDY\",\n" +
                "      \"primaryCity\": \"Scarborough\",\n" +
                "      \"longitude\": -79.307007\n" +
                "    },\n" +
                "    \"id\": 445\n" +
                "  }\n" +
                "]";

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
