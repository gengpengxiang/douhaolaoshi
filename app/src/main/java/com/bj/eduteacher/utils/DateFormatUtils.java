package com.bj.eduteacher.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtils {

    private DateFormatUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 日期格式化
     * 格式：yyyy/MM/dd HH:mm:ss
     */
    public static String formatByPattern(String inputDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = format.parse(inputDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            long inputTime = calendar.getTimeInMillis();
            long currentTime = System.currentTimeMillis();

            long value = currentTime - inputTime;
            if ((value / 1000) < 60) {
                return "刚刚";
            } else if ((value / 1000 / 60) >= 1 && (value / 1000 / 60) < 60) {
                return (value / 1000 / 60) + "分钟前";
            } else if ((value / 1000 / 60 / 60) >= 1 && (value / 1000 / 60 / 60) < 24) {
                return (value / 1000 / 60 / 60) + "小时前";
            } else if ((value / 1000 / 60 / 60 / 24) >= 1 && (value / 1000 / 60 / 60 / 24) < 30) {
                return (value / 1000 / 60 / 60 / 24) + "天前";
            } else if ((value / 1000 / 60 / 60 / 24 / 30) >= 1 && (value / 1000 / 60 / 60 / 24 / 30) < 12) {
                return (value / 1000 / 60 / 60 / 24 / 30) + "个月前";
            } else {
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                return format2.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式化
     * 格式：yyyy/MM/dd HH:mm:ss
     */
    public static String formatByTimeMillis(long millis) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
            Date data = new Date(millis);
            return format.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String FORMAT_PATTERN_EXAMPLE_1 = "yyyy/MM/dd  HH:mm";
    public static final String FORMAT_PATTERN_EXAMPLE_2 = "HH:mm  yyyy/MM/dd";

    public static String formatByTimeMillis(long millis, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date data = new Date(millis);
            return format.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
