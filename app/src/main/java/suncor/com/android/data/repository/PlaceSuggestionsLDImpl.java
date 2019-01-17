package suncor.com.android.data.repository;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.PlaceSuggestion;
import suncor.com.android.model.Resource;

public class PlaceSuggestionsLDImpl implements PlaceSuggestionsLiveData {

    private GeoDataClient geoDataClient;


    public PlaceSuggestionsLDImpl(GeoDataClient geoDataClient) {
        this.geoDataClient = geoDataClient;
    }

    @Override
    public LiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestions(String query) {

        MutableLiveData<Resource<ArrayList<PlaceSuggestion>>> predictions=new MutableLiveData<>();
        predictions.postValue(Resource.loading(null));
        AutocompleteFilter.Builder builder=new AutocompleteFilter.Builder();
        builder.setCountry("ca");
        builder.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS | AutocompleteFilter.TYPE_FILTER_CITIES );
        AutocompleteFilter  mPlaceFilter=builder.build();

        Task<AutocompletePredictionBufferResponse> results =
                geoDataClient.getAutocompletePredictions(query,null,
                        mPlaceFilter);
        results.addOnSuccessListener(autocompletePredictions -> {
            ArrayList<AutocompletePrediction> resultsAuto = DataBufferUtils.freezeAndClose(autocompletePredictions);
            ArrayList<PlaceSuggestion>returnedResult=new ArrayList<>();
            for (AutocompletePrediction place: resultsAuto) {
                PlaceSuggestion placeSuggestion=new PlaceSuggestion(place.getPrimaryText(null).toString(),place.getSecondaryText(null).toString(),place.getPlaceId());
                 returnedResult.add(placeSuggestion);
            }
            predictions.postValue(Resource.success(returnedResult));
        });
        return predictions;
    }

}
