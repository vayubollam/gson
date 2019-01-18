package suncor.com.android.data.repository;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.search.PlaceSuggestion;

public interface PlaceSuggestionsProvider {

    LiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestions(String input);
}
