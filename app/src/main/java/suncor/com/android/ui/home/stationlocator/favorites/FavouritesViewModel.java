package suncor.com.android.ui.home.stationlocator.favorites;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class FavouritesViewModel extends ViewModel {

    private FavouriteRepository favouriteRepository;
    MutableLiveData<ArrayList<Station>> stations = new MutableLiveData<>();

    public FavouritesViewModel(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
        favouriteRepository.loadFavourites().observeForever(booleanResource -> {
            if (booleanResource.status == Resource.Status.SUCCESS) {
                stations.postValue(favouriteRepository.getFavouriteList());
            }
        });

    }
}
