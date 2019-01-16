package suncor.com.android.ui.home.stationlocator.search;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.databinding.ObservableField;
import suncor.com.android.model.Hour;
import suncor.com.android.model.Station;

public class StationNearbyItem {

    public ObservableField<Station> station = new ObservableField<>();
    public ObservableField<Double> distance = new ObservableField<>();

    public StationNearbyItem(Station station) {
        this.station.set(station);
    }

    public StationNearbyItem(Station station, Double distance) {
        this.station.set(station);
        this.distance.set(distance);
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
