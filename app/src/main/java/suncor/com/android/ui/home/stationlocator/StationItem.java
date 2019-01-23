package suncor.com.android.ui.home.stationlocator;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Hour;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class StationItem extends BaseObservable {

    private FavouriteRepository favouriteRepository;


    private Station station;
    private DirectionsResult distanceDuration;
    private boolean isFavourite;

    private Observable.OnPropertyChangedCallback favouriteToggleCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.favourite)
                new Handler(Looper.getMainLooper()).post(() -> favouriteToggled());
        }
    };

    public StationItem(FavouriteRepository favouriteRepository, Station station, boolean isFavourite) {
        this.favouriteRepository = favouriteRepository;
        this.station = station;
        this.isFavourite = isFavourite;
        addOnPropertyChangedCallback(favouriteToggleCallback);
    }

    private void favouriteToggled() {
        if (isFavourite) {
            favouriteRepository.addFavourite(station).observeForever((r) -> {
                if (r.status == Resource.Status.SUCCESS) {
                    Toast.makeText(SuncorApplication.getInstance(), SuncorApplication.getInstance().getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
                } else if (r.status == Resource.Status.ERROR) {
                    removeOnPropertyChangedCallback(favouriteToggleCallback);
                    isFavourite = false;
                    addOnPropertyChangedCallback(favouriteToggleCallback);
                    //TODO handle error
                    Toast.makeText(SuncorApplication.getInstance(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            favouriteRepository.removeFavourite(station).observeForever((r) -> {
                if (r.status == Resource.Status.ERROR) {
                    removeOnPropertyChangedCallback(favouriteToggleCallback);
                    isFavourite = true;
                    addOnPropertyChangedCallback(favouriteToggleCallback);
                    //TODO handle error
                    Toast.makeText(SuncorApplication.getInstance(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public StationItem(Station station, DirectionsResult distanceDuration) {
        this.station = station;
        this.distanceDuration = distanceDuration;
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
