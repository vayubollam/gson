package suncor.com.android.data.repository.suggestions;

import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.stationlocator.search.PlaceSuggestion;

public class GooglePlaceSuggestionsProvider implements PlaceSuggestionsProvider {

    private PlacesClient placesClient;
    private AutocompleteSessionToken token;
    private String TAG = "places_api";

    public GooglePlaceSuggestionsProvider(PlacesClient placesClient) {
        this.placesClient = placesClient;
        token = AutocompleteSessionToken.newInstance();
    }

    @Override
    public LiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestions(String query) {
        MutableLiveData<Resource<ArrayList<PlaceSuggestion>>> predictions = new MutableLiveData<>();
        predictions.postValue(Resource.loading(null));
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("ca")
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            ArrayList<PlaceSuggestion> returnedResult = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                PlaceSuggestion placeSuggestion = new PlaceSuggestion();
                placeSuggestion.setPrimaryText(prediction.getPrimaryText(null).toString());
                placeSuggestion.setSecondaryText(prediction.getSecondaryText(null).toString());
                placeSuggestion.setPlaceId(prediction.getPlaceId());
                returnedResult.add(placeSuggestion);
            }
            predictions.postValue(Resource.success(returnedResult));
        }).addOnFailureListener((exception) -> {
            predictions.postValue(Resource.error(exception.getMessage()));
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
        return predictions;
    }

    @Override
    public LiveData<Resource<LatLng>> getCoordinatesOfPlace(PlaceSuggestion suggestion) {
        MutableLiveData<Resource<LatLng>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        String placeId = suggestion.getPlaceId();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).setSessionToken(token)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            result.postValue(Resource.success(place.getLatLng()));
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                result.postValue(Resource.error(apiException.getMessage(), null));
            }
        });

        return result;
    }
}
