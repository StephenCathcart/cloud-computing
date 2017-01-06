package uk.co.stephencathcart.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date format utility class for formatting a date to a style like ISO 8601
 * format. Also includes a useful method for checking if the current time is within rush hour.
 *
 * @author Stephen Cathcart
 */
public class DateUtils {
    
    private static final int _7AM = 7;
    private static final int _10AM = 10;
    private static final int _3PM = 15;
    private static final int _6PM = 18;
    
    public static String format(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return formatter.format(date);
    }
    
    public static boolean isRushHour()  {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        return (hour >= _7AM && hour < _10AM) || hour >= _3PM && hour < _6PM;
    }
}
