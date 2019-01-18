package suncor.com.android.data.repository;

import android.util.Log;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.search.PlaceSuggestion;

public class PlaceSuggestionsProviderImpl implements PlaceSuggestionsProvider {

    private GeoDataClient geoDataClient;
    private MutableLiveData<Resource<ArrayList<PlaceSuggestion>>> predictions = new MutableLiveData<>();

    public PlaceSuggestionsProviderImpl(GeoDataClient geoDataClient) {
        this.geoDataClient = geoDataClient;
    }

    @Override
    public MutableLiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestionsObservable() {
        return predictions;
    }

    @Override
    public void refreshSuggestion(String query) {


        predictions.postValue(Resource.loading(null));
        AutocompleteFilter.Builder builder = new AutocompleteFilter.Builder();
        builder.setCountry("ca");
        builder.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS | AutocompleteFilter.TYPE_FILTER_CITIES);
        AutocompleteFilter mPlaceFilter = builder.build();

        Task<AutocompletePredictionBufferResponse> results =
                geoDataClient.getAutocompletePredictions(query, null,
                        mPlaceFilter);
        results.addOnSuccessListener(autocompletePredictions -> {
            ArrayList<AutocompletePrediction> resultsAuto = DataBufferUtils.freezeAndClose(autocompletePredictions);
            ArrayList<PlaceSuggestion> returnedResult = new ArrayList<>();
            for (AutocompletePrediction place : resultsAuto) {
                PlaceSuggestion placeSuggestion = new PlaceSuggestion();
                placeSuggestion.setPrimaryText(place.getPrimaryText(null).toString());
                placeSuggestion.setSecondaryText(place.getSecondaryText(null).toString());
                placeSuggestion.setPlaceId(place.getPlaceId());
                returnedResult.add(placeSuggestion);
            }
            predictions.postValue(Resource.success(returnedResult));
        });
        results.addOnFailureListener(e -> {
            predictions.postValue(Resource.error(e.getMessage(), null));
            Log.d("suggestions", e.getMessage());
        });
    }
}
