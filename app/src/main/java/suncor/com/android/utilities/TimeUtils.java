package suncor.com.android.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static String formatTime(Calendar calendar) {
        return new SimpleDateFormat("hh:mm a").format(calendar.getTime());
    }
}
