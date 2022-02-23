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
    public static String getTodayFormattedDate(){
        Calendar calender = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calender.getTime());
    }

    public static int getTodayDate(){
        Calendar calender = Calendar.getInstance();
        return calender.get(Calendar.DATE);
    }

    public static long findDateDifference(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = dateFormat.parse(startDate);
        Date dateD = dateFormat.parse(endDate);

        Long difference_In_Time = dateD.getTime() - date.getTime();

        long difference_In_Days = ((difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365);

        Timber.d("Date Difference Log :", difference_In_Days);
        return difference_In_Days;
    }

}
