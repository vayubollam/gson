package suncor.com.android.data.repository.favourite;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public interface FavouriteRepository {
    LiveData<Resource<Boolean>> loadFavourites();

    /**
     * holds loading status, and can be also used to observe lists changes
     * @return LiveData<Boolean> to observe loading status
     */
    LiveData<Boolean> isLoaded();

    boolean isFavourite(Station station);

    ArrayList<Station> getFavouriteList();

    LiveData<Resource<Boolean>> addFavourite(Station station);

    LiveData<Resource<Boolean>> removeFavourite(Station station);
}
