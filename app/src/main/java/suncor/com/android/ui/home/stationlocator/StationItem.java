package suncor.com.android.ui.home.stationlocator;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Hour;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class StationItem {

    private FavouriteRepository favouriteRepository;

    public ObservableField<Station> station = new ObservableField<>();
    public ObservableField<DirectionsResult> distanceDuration = new ObservableField<>();
    public ObservableBoolean isFavourite;
    private Observable.OnPropertyChangedCallback favouriteToggleCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            new Handler(Looper.getMainLooper()).post(() -> favouriteToggled());
        }
    };

    public StationItem(FavouriteRepository favouriteRepository, Station station, boolean isFavourite) {
        this.favouriteRepository = favouriteRepository;
        this.station.set(station);
        this.isFavourite = new ObservableBoolean(isFavourite);
        this.isFavourite.addOnPropertyChangedCallback(favouriteToggleCallback);
    }

    private void favouriteToggled() {
        if (isFavourite.get()) {
            favouriteRepository.addFavourite(station.get()).observeForever((r) -> {
                if (r.status == Resource.Status.ERROR) {
                    isFavourite.removeOnPropertyChangedCallback(favouriteToggleCallback);
                    isFavourite.set(false);
                    isFavourite.addOnPropertyChangedCallback(favouriteToggleCallback);
                    //TODO handle error
                    Toast.makeText(SuncorApplication.getInstance(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            favouriteRepository.removeStation(station.get()).observeForever((r) -> {
                if (r.status == Resource.Status.ERROR) {
                    isFavourite.removeOnPropertyChangedCallback(favouriteToggleCallback);
                    isFavourite.set(true);
                    isFavourite.addOnPropertyChangedCallback(favouriteToggleCallback);
                    //TODO handle error
                    Toast.makeText(SuncorApplication.getInstance(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public StationItem(Station station, DirectionsResult distanceDuration) {
        this.station.set(station);
        this.distanceDuration.set(distanceDuration);
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
        ArrayList<Hour> workHours = station.get().getHours();
        StringBuilder buffer = new StringBuilder();
        buffer.append(workHours.get(i).formatOpenHour())
                .append(" - ")
                .append(workHours.get(i).formatCloseHour());
        return buffer.toString();
    }

    private Hour getTodaysHours() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return station.get().getHours().get(dayOfWeek - 1);
    }
}
