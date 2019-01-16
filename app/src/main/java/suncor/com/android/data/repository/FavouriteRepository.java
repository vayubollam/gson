package suncor.com.android.data.repository;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public interface FavouriteRepository {
    LiveData<Resource<Boolean>> loadFavourites();

    boolean isLoaded();

    boolean isFavourite(Station station);

    ArrayList<Station> getFavouriteList();

    LiveData<Resource<Boolean>> addFavourite(Station station);

    LiveData<Resource<Boolean>> removeStation(Station station);
}
