package suncor.com.android.ui.home.stationlocator;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.FavouriteRepository;

public class StationViewModelFactory implements ViewModelProvider.Factory {

    private final FavouriteRepository favouriteRepository;

    public StationViewModelFactory(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StationsViewModel.class)) {
            return (T) new StationsViewModel(favouriteRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
