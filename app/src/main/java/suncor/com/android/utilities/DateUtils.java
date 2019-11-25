package suncor.com.android.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

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
        SimpleDateFormat localDateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm");
        return localDateFormat.format(inputDate);
    }
}
