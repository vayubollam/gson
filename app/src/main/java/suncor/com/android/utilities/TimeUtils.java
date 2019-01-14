package suncor.com.android.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtils {

    public static String formatTime(Calendar calendar) {
        return new SimpleDateFormat("hh:mm a").format(calendar.getTime());
    }
}
