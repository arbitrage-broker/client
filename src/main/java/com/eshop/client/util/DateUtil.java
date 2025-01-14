package com.eshop.client.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static LocalDate toLocalDate(Date date){
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    public static LocalDateTime toLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    public static LocalDate toLocalDate(Long epochMilli){
        return Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public static LocalDateTime toLocalDateTime(Long epochMilli){
        return Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    public static Long toEpoch(LocalDate date){
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    public static Long toEpoch(LocalDateTime date){
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Date toDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Date toDate(LocalDateTime date){
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String addZero(int value) {
        if (value < 10)
            return "0" + value;
        else return value + "";
    }
    public static Date truncate(Date date) {
        // Convert back to Date with time set to 00:00:00
        return Date.from(toLocalDate(date).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String timeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " mins ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hours ago";
        } else if (seconds < 172800) { // 2 days
            return "yesterday";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
            return dateTime.format(formatter);
        }
    }
}
