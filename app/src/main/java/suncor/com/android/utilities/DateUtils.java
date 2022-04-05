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
    private static final String DATE_TIME_FORMAT = "yy/MM/dd HH:mm:ss";

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

    public static long getDateTimeDifference(String startDate , String endDate){

        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(startDate);
            d2 = format.parse(endDate);
        } catch (ParseException e){
            e.getLocalizedMessage();
            Timber.d("Exception", e.getLocalizedMessage());
        }

        assert d2 != null;
        assert d1 != null;
        long diff = d2.getTime() - d1.getTime();
        Timber.d("Time Difference", String.valueOf(diff));

        return diff;
    }

    public static String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();;
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        return format.format(date);
    }

}
