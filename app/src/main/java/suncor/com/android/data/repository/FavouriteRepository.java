package suncor.com.android.data.repository;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public interface FavouriteRepository {
    LiveData<Resource<ArrayList<Station>>> getFavouriteList();
    LiveData<Resource<Boolean>> addFavourite(Station station);
    LiveData<Resource<Boolean>> removeStation(Station station);
}
