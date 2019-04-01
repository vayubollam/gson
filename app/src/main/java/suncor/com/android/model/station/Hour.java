package suncor.com.android.model.station;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import suncor.com.android.utilities.TimeUtils;

public class Hour {
    private String close;
    private String open;

    public Hour(String close, String open) {
        this.close = close;
        this.open = open;
    }

    private static Calendar parseDate(String hour) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        try {
            calendar.setTime(sdf.parse(hour));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String formatOpenHour() {
        Calendar calendar = parseDate(open);
        return TimeUtils.formatTime(calendar);
    }

    public String formatCloseHour() {
        Calendar calendar = parseDate(close);
        return TimeUtils.formatTime(calendar);
    }

    public boolean isInRange(Calendar calendar) {
        Calendar openHour = parseDate(open);
        Calendar closeHour = parseDate(close);
        calendar.set(openHour.get(Calendar.YEAR), openHour.get(Calendar.MONTH), openHour.get(Calendar.DAY_OF_MONTH));
        return calendar.before(closeHour) && calendar.after(openHour);
    }

}
