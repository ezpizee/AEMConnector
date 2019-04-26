package com.ezpizee.aem.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateFormatUtil {

    private static final String DEFAULT_SIMPLEDATE_TIMEZONE = "UTC";
    private static final SimpleDateFormat MICROTIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, y");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("MMM d, y h:mm aaa");
    private static final SimpleDateFormat YYYYMMDD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat MMDD_FORMAT = new SimpleDateFormat("MM/dd");
    private static final SimpleDateFormat MMMMYYYY_FORMAT = new SimpleDateFormat("MMMM yyyy");
    private static final DateFormatSymbols DFS = new DateFormatSymbols();
    private static final String[] MONTHS = DFS.getMonths();

    private DateFormatUtil() {}

    public static String microTimestamp() {
        return DateFormatUtil.getTimeStr(null);
    }

    public static String displayFormat(Date date) {
        return DATE_FORMAT.format((new Date(DateFormatUtil.getStandardTimezone(date.getTime())*1000)).getTime());
    }

    public static String datetime(Date date) {
        return date != null ? DATETIME_FORMAT.format((new Date(DateFormatUtil.getStandardTimezone(date.getTime())*1000)).getTime()) : "";
    }

    public static String datetime(Calendar calendar) {
        return calendar != null ? DATETIME_FORMAT.format(calendar.getTime()) : "";
    }

    public static String getTimeStr(Date date) {
        return MICROTIMESTAMP_FORMAT.format(DateFormatUtil.getStandardTimezone(date==null? (new Date()).getTime() : date.getTime()));
    }

    public static String getTimeStr(int unixTimestamp) {
        return MICROTIMESTAMP_FORMAT.format((new Date(DateFormatUtil.getStandardTimezone((long)unixTimestamp*1000))).getTime());
    }

    public static Calendar getCalendar(int unixTimestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(DateFormatUtil.getStandardTimezone((long)unixTimestamp*1000)));
        return cal;
    }

    public static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(DateFormatUtil.getStandardTimezone((new Date()).getTime())));
        return cal;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(DateFormatUtil.getStandardTimezone(date.getTime())));
        return cal;
    }

    public static Calendar getCalendar(String dateString) {
        try {
            return getCalendar(MICROTIMESTAMP_FORMAT.parse(dateString));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static long strToTimestamp(String dateString) {
        if (StringUtils.isNotEmpty(dateString)) {
            Calendar c = getCalendar(dateString);
            if (c != null) {
                return c.getTimeInMillis();
            }
        }
        return -1;
    }

    public static String yyyymmddFormat(Date date) {
        return date != null ? YYYYMMDD_FORMAT.format(new Date(DateFormatUtil.getStandardTimezone(date.getTime()))) : "";
    }

    private static long getStandardTimezone(long time) {
        long newTime;
        try {
            MICROTIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone(DEFAULT_SIMPLEDATE_TIMEZONE));
            newTime = MICROTIMESTAMP_FORMAT.parse(MICROTIMESTAMP_FORMAT.format(time)).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(DateFormatUtil.class.getName()).log(Level.SEVERE, null, ex);
            newTime = 0;
        }
        return newTime;
    }

    public static String getMonthForInt(int num) {
        String month = null;
        if (num >= 0 && num <= 11 ) {
            month = MONTHS[num];
        }
        return month;
    }

}