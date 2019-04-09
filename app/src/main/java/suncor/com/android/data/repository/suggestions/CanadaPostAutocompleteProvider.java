package suncor.com.android.data.repository.suggestions;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.canadapost.CanadaPostSuggestion;
import suncor.com.android.utilities.Timber;

public class CanadaPostAutocompleteProvider {
    private static final String BASE_URL = "http://ws1.postescanada-canadapost.ca/AddressComplete/Interactive/%s/v2.10/json3.ws";
    private static final String API_KEY = "FJ69-PA18-EJ92-NH91";

    private final Gson gson;
    private OkHttpClient client;

    public CanadaPostAutocompleteProvider(Gson gson, OkHttpClient client) {
        this.gson = gson;
        this.client = client;
    }

    public LiveData<Resource<CanadaPostSuggestion[]>> findSuggestions(String query, String lastId) {
        if (lastId != null) {
            Timber.d("Retrieving suggestions for group with ID :" + lastId);
        } else {
            Timber.d("Retrieving suggestions for :" + query);
        }
        MutableLiveData<Resource<CanadaPostSuggestion[]>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        StringBuilder urlBuilder = new StringBuilder(String.format(BASE_URL, "Find"))
                .append("?")
                .append("Key=")
                .append(API_KEY)
                .append("&")
                .append("LanguagePreference")
                .append(Locale.getDefault().getLanguage())
                .append("&")
                .append("SearchTerm=")
                .append(query);
        if (lastId != null) {
            urlBuilder.append("&")
                    .append("LastId=")
                    .append(lastId);
        }

        Request request = new Request.Builder()
                .get()
                .url(urlBuilder.toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        CanadaPostResponse addresses = gson.fromJson(jsonResponse, CanadaPostResponse.class);
                        result.postValue(Resource.success(addresses.items));
                    } catch (JsonSyntaxException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }
                } else {
                    result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                }
            }
        });

        return result;
    }

    private class CanadaPostResponse {
        CanadaPostSuggestion[] items;
    }
}
