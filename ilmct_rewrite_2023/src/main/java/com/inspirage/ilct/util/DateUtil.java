package com.inspirage.ilct.util;


import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String dateFormate1 = "yyyy-MM-dd hh:mm a";
    public static final String dateFormate2 = "yyyyMMddhhmmss";
    public static final String dateTimeFormatter1 = "yyyy-MM-dd hh:mm a";
    public static final String dateTimeFormatter2 = "yyyyMMddHHmmss";
    public static final String dateFormatWithoutTime = "yyyyMMdd";

    public static String convertDate(String date, String from, String to) {
        SimpleDateFormat sdfSource = new SimpleDateFormat(from);
        SimpleDateFormat sdfDestination = new SimpleDateFormat(to);
        //parse the string into Date object
        try {
            Date d = sdfSource.parse(date);
            return sdfDestination.format(d);
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return "";
    }

    public static String formatDate(Date date, String toFormat) {
        return new SimpleDateFormat(toFormat).format(date);
    }

    public static String formatDate(LocalDateTime localDateTime, String toFormat) {
        return DateTimeFormatter.ofPattern(toFormat).format(localDateTime);
    }

    public static String formatDate(LocalDateTime localDateTime, String toFormat, String withTimeZone) {
        return formatDate(LocalDateTime.ofInstant(localDateTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.of(withTimeZone)), toFormat);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime convertDate(Date date, String fromOffsetOrTimeZone, String toTimeZone) {
        try {
            ZonedDateTime zonedDateTime;
            if (!StringUtils.isEmpty(fromOffsetOrTimeZone)) {
                try {
                    zonedDateTime = date.toInstant().atZone(ZoneId.of(fromOffsetOrTimeZone));
                } catch (Exception e) {
                    try {
                        zonedDateTime = date.toInstant().atZone(ZoneId.ofOffset("UTC", ZoneOffset.of(fromOffsetOrTimeZone)));
                    } catch (Exception e2) {
                        return null;
                    }
                }
            } else
                zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());

            return toTimeZone(zonedDateTime, toTimeZone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime convertDate(String dateString, String fromDateTimeFormatter, String fromOffsetOrTimeZone, String toTimeZone) {
        try {
            ZoneId zoneId;
            if (!StringUtils.isEmpty(fromOffsetOrTimeZone)) {
                try {
                    zoneId = ZoneId.of(fromOffsetOrTimeZone);
                } catch (Exception e) {
                    try {
                        zoneId = ZoneId.ofOffset("UTC", ZoneOffset.of(fromOffsetOrTimeZone));
                    } catch (Exception e2) {
                        return null;
                    }
                }
            } else {
                zoneId = ZoneId.systemDefault();
            }
            return toTimeZone(LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(fromDateTimeFormatter)).atZone(zoneId), toTimeZone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static LocalDateTime toTimeZone(ZonedDateTime zonedDateTime, String toTimeZone) {
        return LocalDateTime.ofInstant(zonedDateTime.toInstant(), (StringUtils.isEmpty(toTimeZone) ? ZoneId.systemDefault() : ZoneId.of(toTimeZone)));
    }

    public static LocalDateTime getStartDate(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);

    return LocalDateTime.ofInstant(calendar.getTime().toInstant(),
                ZoneId.systemDefault());
    }

    public static LocalDateTime addDays(LocalDateTime date, Integer days) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
    	calendar.set(Calendar.DATE, days);
    	 return LocalDateTime.ofInstant(calendar.getTime().toInstant(),
                 ZoneId.systemDefault());
    }

    public static Long differenceBetweenDatesInMinutes(LocalDateTime fromDate, LocalDateTime toDate) {
    return ChronoUnit.MINUTES.between(fromDate, toDate);
    }

    public static LocalDateTime formatDateWithTimezone(LocalDateTime localDateTime, String toTimeZone) {
        return LocalDateTime.ofInstant(localDateTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.of(toTimeZone));
    }

    public static Date formatDateStringWithoutTime(String date, String format) {
    		try {
				Date newDate = new SimpleDateFormat(format).parse(date);
			 	Calendar calendar = Calendar.getInstance();
		    	calendar.setTime(newDate);
		    	calendar.set(Calendar.HOUR, 0);
		    	calendar.set(Calendar.MINUTE, 0);
		    	calendar.set(Calendar.SECOND, 0);
		    	calendar.set(Calendar.MILLISECOND, 0);
		     	calendar.set(Calendar.HOUR_OF_DAY, 0);
		    	return calendar.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		return null;
    }

    // converting event date of format (yyyyMMddHHmmss) with system default timeZone to Util Date tipe
    public static Date formatDateWithSystemTimeZone(String date) {
        LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateTimeFormatter2));
        return Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    // converting string date to LocalDateTime without Zone conversions
    public static LocalDateTime stringToLocalDateTime(String date, String format) {
       return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
    }

    public static Date minusDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -(days));
        return calendar.getTime();
    }
}
