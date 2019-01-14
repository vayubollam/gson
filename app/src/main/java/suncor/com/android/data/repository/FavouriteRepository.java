package suncor.com.android.data.repository;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Station;

public interface FavouriteRepository {
    LiveData<Station> getFavouriteList();
}
