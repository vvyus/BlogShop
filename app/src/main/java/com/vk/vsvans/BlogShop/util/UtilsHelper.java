package com.vk.vsvans.BlogShop.util;

import android.graphics.Color;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UtilsHelper {
    final public static String SEP = "♀";
    private static final String DATE_FORMAT_1 = "dd MMMM yyyy";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    public static long getTimestamp(String dateString) {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT_1, Locale.getDefault()).parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDate(long time) {
        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_1, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String getDate(long time,String f) {
        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat(f, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static int getContrastColor(int color) {
        if(color==Color.WHITE){
            return Color.LTGRAY;
        }else return Color.WHITE;
     }

    public static long correct_date_begin(long date_begin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date_begin);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //  Toast.makeText(context, "date : "+ dateInString(date_begin)+" "+dateInString(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();
        date_begin=calendar.getTimeInMillis();
        return date_begin;
    }

    public static long correct_date_end(long date_end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date_end);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        date_end=calendar.getTimeInMillis();
        return date_end;
    }

    public static String dateInString(long date) {
        String _date=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
        return _date;
    }

    public static String dateInStringShort(long date) {
        String _date=new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date);
        return _date;
    }

    public static long getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,0);
        long date = calendar.getTimeInMillis();
        //date=System.currentTimeMillis()
        return date;
    }

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
