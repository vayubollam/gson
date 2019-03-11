package suncor.com.android.data.repository.suggestions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.search.PlaceSuggestion;

public interface PlaceSuggestionsProvider {

    LiveData<Resource<ArrayList<PlaceSuggestion>>> getSuggestions(String input);

    LiveData<Resource<LatLng>> getCoordinatesOfPlace(PlaceSuggestion suggestion);
}
