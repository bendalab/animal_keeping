package animalkeeping.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
        return date.toLocalDate();
    }

    public static LocalDate toLocalDate(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return toLocalDate((java.sql.Date) date);
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(java.util.Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
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

    public static Age age(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            Integer years, months, days;
            years = Period.between(birthDate, currentDate).getYears();
            LocalDate ref = LocalDate.of(birthDate.getYear() + years, birthDate.getMonth(), birthDate.getDayOfMonth());
            months = Period.between(ref, currentDate).getMonths();
            ref = ref.plusMonths(months);
            days = Period.between(ref, currentDate).getDays();
            return new Age(years, months, days);
        } else {
            return null;
        }
    }

    public static class Age {
        Integer years, months, days;

        Age(Integer years, Integer months, Integer days) {
            this.years = years;
            this.months = months;
            this.days = days;
        }

        public Integer getYears() {
            return years;
        }

        public Integer getMonths() {
            return months;
        }

        public Integer getDays() {
            return days;
        }
    }

}
