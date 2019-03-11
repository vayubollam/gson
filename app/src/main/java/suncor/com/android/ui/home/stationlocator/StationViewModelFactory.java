package suncor.com.android.ui.home.stationlocator;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.favourite.FavouriteRepository;
import suncor.com.android.data.repository.stations.StationsProvider;

public class StationViewModelFactory implements ViewModelProvider.Factory {

    private final FavouriteRepository favouriteRepository;
    private final StationsProvider stationsProvider;


    public StationViewModelFactory(StationsProvider stationsProvider, FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
        this.stationsProvider = stationsProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StationsViewModel.class)) {
            return (T) new StationsViewModel(stationsProvider, favouriteRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
