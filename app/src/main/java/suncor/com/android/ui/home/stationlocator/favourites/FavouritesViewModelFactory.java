package suncor.com.android.ui.home.stationlocator.favourites;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.favourite.FavouriteRepository;

public class FavouritesViewModelFactory implements ViewModelProvider.Factory {

    private final FavouriteRepository favouriteRepository;

    public FavouritesViewModelFactory(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavouritesViewModel.class)) {
            return (T) new FavouritesViewModel(favouriteRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
