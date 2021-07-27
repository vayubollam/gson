package suncor.com.android.utilities;

import android.content.res.Resources;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String ENGLISH_DATE_FORMAT = "MMM dd, yyyy 'at' hh:mma";
    private static final String FRENCH_DATE_FORMAT = "dd MMM yyyy 'à' HH:mm";

    public static String getFormattedDate(String inputDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public static String getFormattedDate(Date inputDate) {
        Locale local = Resources.getSystem().getConfiguration().getLocales().get(0);
        if (local.equals(Locale.CANADA_FRENCH) ||
                local.equals(Locale.FRENCH) || local.equals(Locale.FRANCE)) {
            SimpleDateFormat localDateFormat = new SimpleDateFormat(FRENCH_DATE_FORMAT, local);
            return localDateFormat.format(inputDate);
        } else {
            SimpleDateFormat localDateFormat = new SimpleDateFormat(ENGLISH_DATE_FORMAT, local);
            return localDateFormat.format(inputDate);
        }
    }

    public static long getTodayTimestamp(){
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.HOUR, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        return calender.getTimeInMillis();
    }

    public static int getTodayDate(){
        Calendar calender = Calendar.getInstance();
        return calender.get(Calendar.DATE);
    }

}
