package suncor.com.android.utilities;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final String ENGLISH_DATE_FORMAT = "MMM dd, yyyy 'at' hh:mma";
    private static final String FRENCH_DATE_FORMAT = "dd MMM yyyy 'à' HH:mm";
    private static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

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

    public static String getFormattedDate(String inputDate, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
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
        SimpleDateFormat localDateFormat;
        if (local.equals(Locale.CANADA_FRENCH) ||
                local.equals(Locale.FRENCH) || local.equals(Locale.FRANCE)) {
            localDateFormat = new SimpleDateFormat(FRENCH_DATE_FORMAT, local);
        } else {
            localDateFormat = new SimpleDateFormat(ENGLISH_DATE_FORMAT, local);
        }
        return localDateFormat.format(inputDate);
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

    @SuppressLint("SimpleDateFormat")
    public static long getDateTimeDifference(String startDate , String endDate, boolean isDifferenceInDays){

        SimpleDateFormat format;

        if(isDifferenceInDays){
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else{
            format = new SimpleDateFormat(DATE_TIME_FORMAT);
        }


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

        if(!isDifferenceInDays){
            Timber.d("Time Difference Log", String.valueOf(diff));
            return diff;
        }else{
            long difference_In_Days = ((diff
                    / (1000 * 60 * 60 * 24))
                    % 365);

            Timber.d("Date Difference Log :", difference_In_Days);
            return difference_In_Days;
        }
    }

    public static String getCurrentDateInEST() {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat etDf = new SimpleDateFormat(DATE_TIME_FORMAT);
        TimeZone etTimeZone = TimeZone.getTimeZone("America/New_York");
        etDf.setTimeZone(etTimeZone);

        Date currentDate = new Date();
        //In ET Time
        return etDf.format(currentDate.getTime());
    }

        public static long findDateDifference (String startDate, String endDate) throws ParseException {
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
