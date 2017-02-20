package animalkeeping.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeHelper {

    public static Date getDateTime(LocalDate ld, String timeStr) {
        String d = ld.toString();
        if (!validateTime(timeStr)) {
            return null;
        }

        String datetimestr = d + " " + timeStr;
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date datetime;

        try {
            datetime = dateTimeFormat.parse(datetimestr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return datetime;
    }

    public static Date localDateToUtilDate(LocalDate d) {
        String dstr = d.toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(dstr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static LocalDate toLocalDate(java.sql.Date date) {
        return ((java.sql.Date) date).toLocalDate();
    }

    public static LocalDate toLocalDate(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return toLocalDate((java.sql.Date) date);
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Boolean validateTime(String time_str) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            timeFormat.parse(time_str);
            return true;
        } catch (Exception e) {
            return  false;
        }
    }
}
