package suncor.com.android.data.repository;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.PlaceSuggestion;
import suncor.com.android.model.Resource;

public interface PlaceSuggestionsLiveData {

    LiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestions(String text);

}
