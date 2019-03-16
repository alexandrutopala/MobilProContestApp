package ro.infotop.journeytoself.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
    private DateUtils() {
    }

    private static String dateAndTimeFormat = "dd.MM.yyyy-HH:mm";
    private static String dateFormat = "dd.MM.yyyy";

    public static Date parseStringDateAndTime(String stringDateAndTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateAndTimeFormat);
        try {
            return sdf.parse(stringDateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseDateAndTime(Date dateAndTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateAndTimeFormat);
        return sdf.format(dateAndTime);
    }

    public static String parseDateAndTime(Date dateAndTime, String dateAndTimeFormat) {
        String oldFormat = DateUtils.dateAndTimeFormat;
        DateUtils.dateAndTimeFormat = dateAndTimeFormat;
        String result = parseDateAndTime(dateAndTime);
        DateUtils.dateAndTimeFormat = oldFormat;
        return result;
    }

    public static Date parseStringDateAndTime(String stringDateAndTime, String dateAndTimeFormat) {
        String oldFormat = DateUtils.dateAndTimeFormat;
        DateUtils.dateAndTimeFormat = dateAndTimeFormat;
        Date result = parseStringDateAndTime(stringDateAndTime);
        DateUtils.dateAndTimeFormat = oldFormat;
        return result;
    }

    public static String getDateAndTimeFormat() {
        return dateAndTimeFormat;
    }

    public static void setDateAndTimeFormat(String dateAndTimeFormat) {
        DateUtils.dateAndTimeFormat = dateAndTimeFormat;
    }

    public static String getCurrentDateAsString() {
        Date currDate = Calendar.getInstance().getTime();
        return parseDate(currDate);
    }

    private static String parseDate(Date currDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(currDate);
    }

    public static Date parseStringDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
