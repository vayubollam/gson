package suncor.com.android.ui.home.stationlocator.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.PlaceSuggestionsProvider;

public class SearchViewModelFactory implements ViewModelProvider.Factory {

    private final PlaceSuggestionsProvider placeSuggestionsProvider;

    public SearchViewModelFactory(PlaceSuggestionsProvider placeSuggestionsProvider) {
        this.placeSuggestionsProvider = placeSuggestionsProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(placeSuggestionsProvider);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
