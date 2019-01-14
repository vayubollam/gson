package suncor.com.android.fragments;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import androidx.databinding.ObservableField;
import suncor.com.android.dataObjects.Hour;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dataObjects.StationMatrix;

public class StationViewModel {

    public ObservableField<Station> station = new ObservableField<>();
    public ObservableField<StationMatrix> distanceDuration = new ObservableField<>();
    public ObservableField<Boolean> isExpanded = new ObservableField<>(false);
    private boolean isFavourite = false;

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

    public boolean isFavourite() {
        return isFavourite;
    }
}
