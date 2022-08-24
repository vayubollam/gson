package suncor.com.android.ui.main.stationlocator;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;


import suncor.com.android.BR;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.station.Hour;
import suncor.com.android.model.station.Station;

public class StationItem extends BaseObservable {

    public FavouriteRepository favouriteRepository;

    private Station station;
    private DirectionsResult distanceDuration;
    public boolean isFavourite;

    public StationItem(FavouriteRepository favouriteRepository, Station station, boolean isFavourite) {
        this.favouriteRepository = favouriteRepository;
        this.station = station;
        this.isFavourite = isFavourite;
    }

    public StationItem(FavouriteRepository favouriteRepository, Station station, DirectionsResult distanceDuration) {
        this.favouriteRepository = favouriteRepository;
        this.station = station;
        this.distanceDuration = distanceDuration;
    }

    public StationItem(Station station) {
        this.station = station;
    }


    @Bindable
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
        notifyPropertyChanged(BR.station);
    }

    @Bindable
    public DirectionsResult getDistanceDuration() {
        return distanceDuration;
    }

    public void setDistanceDuration(DirectionsResult distanceDuration) {
        this.distanceDuration = distanceDuration;
        notifyPropertyChanged(BR.distanceDuration);
    }

    @Bindable
    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        notifyPropertyChanged(BR.favourite);
    }

    public LiveData<Resource<Boolean>> toggleFavourite() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        try {
            if (isFavourite) {
                return Transformations.map(favouriteRepository.removeFavourite(station), (r) -> {
                    if (r.status == Resource.Status.SUCCESS) {
                        setFavourite(false);
                    }
                    return r;
                });
            } else {
                return Transformations.map(favouriteRepository.addFavourite(station), (r) -> {
                    if (r.status == Resource.Status.SUCCESS) {
                        setFavourite(true);
                    }
                    return r;
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.postValue(Resource.error(ex.getMessage(), false));
            return result;
        }
    }

    public String getCarWashType() {
        return station.getCarWashType();
    }

    public boolean isOpen() {
        Hour workHour = getTodaysHours();
        return workHour.isInRange(Calendar.getInstance());
    }

    public String getOpenHour() {
        return getTodaysHours().formatOpenHour();
    }

    public String getCloseHour() {
        return getTodaysHours().formatCloseHour();
    }

    public boolean isOpen24Hrs() {
        Hour workHours = getTodaysHours();
        return workHours.getOpen().equals("0000") && workHours.getClose().equals("2400");
    }
    public boolean isClosed(int i) {
        Hour workHours = station.getHours().get(i);
        return workHours.getOpen().equals("0000") && workHours.getClose().equals("0000");
    }

    public String getDayName(int i) {
        String[] weekDayNames = new DateFormatSymbols().getWeekdays();
        int index = i + 1;
        return weekDayNames[index];
    }

    public String getWorkHours(int i) {
        ArrayList<Hour> workHours = station.getHours();
        StringBuilder buffer = new StringBuilder();
        buffer.append(workHours.get(i).formatOpenHour())
                .append(" - ")
                .append(workHours.get(i).formatCloseHour());
        return buffer.toString();
    }

    private Hour getTodaysHours() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return station.getHours().get(dayOfWeek - 1);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof StationItem)) {
            return false;
        }
        return ((StationItem) obj).station.equals(this.station);
    }
}
