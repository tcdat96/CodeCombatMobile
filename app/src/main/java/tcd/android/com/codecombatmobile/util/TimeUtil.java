package tcd.android.com.codecombatmobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
    private static String[] mMonthNames = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static long convertCcTimeToLong(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        try {
            Date date = formatter.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getDayMonthString(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }

    public static int getDayOfYear(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(millis);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static String getMonthName(int month) {
        return mMonthNames[month];
    }
}
