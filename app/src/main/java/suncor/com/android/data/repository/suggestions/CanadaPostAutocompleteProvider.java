package suncor.com.android.data.repository.suggestions;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

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
import suncor.com.android.model.canadapost.CanadaPostDetails;
import suncor.com.android.model.canadapost.CanadaPostSuggestion;
import suncor.com.android.utilities.Timber;

public class CanadaPostAutocompleteProvider {
    private static final String BASE_URL = "http://ws1.postescanada-canadapost.ca/AddressComplete/Interactive/%s/v2.10/json3.ws";
    private static final String API_KEY = "FJ69-PA18-EJ92-NH91";

    private final Gson gson;
    private OkHttpClient client;

    private Call httpCall;

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

        if (httpCall != null) {
            httpCall.cancel();
        }
        httpCall = client.newCall(request);

        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Timber.w("Retrieving suggestions failed", e);
                result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                httpCall = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpCall = null;
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Timber.d("Retrieving suggestions succeeded\n" + jsonResponse);
                    try {
                        CanadaPostSuggestionsResponse addresses = gson.fromJson(jsonResponse, CanadaPostSuggestionsResponse.class);
                        result.postValue(Resource.success(addresses.items));
                    } catch (JsonSyntaxException e) {
                        Timber.w("Deserializing suggestions failed", e);
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }
                } else {
                    Timber.w("Retrieving suggestions failed, response code: ", response.code());
                    result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                }
            }
        });

        return result;
    }

    public LiveData<Resource<CanadaPostDetails>> getPlaceDetails(String placeId) {
        Timber.d("Retrieving details for place with id: " + placeId);
        MutableLiveData<Resource<CanadaPostDetails>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        StringBuilder urlBuilder = new StringBuilder(String.format(BASE_URL, "Retrieve"))
                .append("?")
                .append("Key=")
                .append(API_KEY)
                .append("&")
                .append("Id=")
                .append(placeId);

        Request request = new Request.Builder()
                .get()
                .url(urlBuilder.toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Timber.w("Retrieving place details failed", e);
                result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Timber.d("Retrieving place details succeeded\n" + jsonResponse);
                    try {
                        CanadaPostDetailsResponse addresses = gson.fromJson(jsonResponse, CanadaPostDetailsResponse.class);
                        CanadaPostDetails placeDetails = null;
                        String currentLanguage = Locale.getDefault().getISO3Language();
                        for (CanadaPostDetails item : addresses.items) {
                            if (currentLanguage.equalsIgnoreCase(item.getLanguage())) {
                                placeDetails = item;
                            }
                        }
                        if (placeDetails == null) {
                            placeDetails = addresses.items[0];
                        }
                        if (placeDetails.getError() != null) {
                            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        } else {
                            result.postValue(Resource.success(placeDetails));
                        }
                    } catch (JsonSyntaxException e) {
                        Timber.w("Deserializing place details failed", e);
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }
                } else {
                    Timber.w("Retrieving place details failed, response code: ", response.code());
                    result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                }
            }
        });

        return result;
    }

    private class CanadaPostSuggestionsResponse {
        @SerializedName("Items")
        CanadaPostSuggestion[] items;
    }

    private class CanadaPostDetailsResponse {
        @SerializedName("Items")
        CanadaPostDetails[] items;
    }

}
