package suncor.com.android.ui.home.stationlocator.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.data.repository.suggestions.PlaceSuggestionsProvider;

public class SearchViewModelFactory implements ViewModelProvider.Factory {

    private final PlaceSuggestionsProvider placeSuggestionsProvider;
    private final StationsProvider stationsProvider;

    public SearchViewModelFactory(StationsProvider stationsProvider, PlaceSuggestionsProvider placeSuggestionsProvider) {
        this.stationsProvider = stationsProvider;
        this.placeSuggestionsProvider = placeSuggestionsProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(stationsProvider, placeSuggestionsProvider);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
