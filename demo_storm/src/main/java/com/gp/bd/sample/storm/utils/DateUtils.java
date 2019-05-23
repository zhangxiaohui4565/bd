package com.gp.bd.sample.storm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String getDate(String date, String pattern, int step) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            try {
                cal.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cal.add(Calendar.DATE, step);
        return sdf.format(cal.getTime());
    }

    public static String getToday(String pattern) {
        if (null == pattern) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String today = formatter.format(new Date());
        return today;
    }
}
