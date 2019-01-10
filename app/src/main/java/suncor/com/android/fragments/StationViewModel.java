package suncor.com.android.fragments;

import java.util.Calendar;

import androidx.databinding.ObservableField;
import suncor.com.android.dataObjects.Hour;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dataObjects.StationMatrix;

public class StationViewModel {

    public ObservableField<Station> station = new ObservableField<>();
    public ObservableField<StationMatrix> distanceDuration = new ObservableField<>();
    public ObservableField<Boolean> isExpanded = new ObservableField<>(false);

    public StationViewModel(Station station) {
        this.station.set(station);
    }

    public StationViewModel(Station station, StationMatrix distanceDuration) {
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

    private Hour getTodaysHours() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return station.get().getHours().get(dayOfWeek - 1);
    }
}
