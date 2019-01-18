package suncor.com.android.data.repository;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.search.PlaceSuggestion;

public interface PlaceSuggestionsProvider {

    MutableLiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestionsObservable();

    void refreshSuggestion(String text);
}
