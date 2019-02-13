package suncor.com.android.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {

    public static String formatTime(Calendar calendar) {
        SimpleDateFormat formatter;
        if ("fr".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            formatter = new SimpleDateFormat("HH 'h' mm");
        } else {
            formatter = new SimpleDateFormat("hh:mm a");
        }

        return formatter.format(calendar.getTime()).toLowerCase();
    }
}
