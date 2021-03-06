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

    public static long getFirstDayOfMonth(Long time){
        Calendar c = Calendar.getInstance();   // this takes current date
        c.setTime( new Date(time) );
        //c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTimeInMillis();
    }

    public static long getFirstDayOfYear(Long time){
        Calendar c = Calendar.getInstance();   // this takes current date
        //set given time
        c.setTime( new Date(time) );
        //c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTimeInMillis();
    }

    public static long getFirstDayOfWeek(Long time){

//        int dayOfWeek = 2; // Monday
//        Calendar now = Calendar.getInstance();
//        int weekday = now.get(Calendar.DAY_OF_WEEK);
//
//        // calculate how much to add
//        int days = dayOfWeek - weekday;
//        if (days < 0) days += 7;
//        now.add(Calendar.DAY_OF_YEAR, days);
        Calendar now = Calendar.getInstance();
        now.setTime(  new Date(time) );
        //now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK)-1;// нмер дня недели(для 1-го число) -1(с 0-го)
        dayOfWeek=(dayOfWeek==0) ? 7: dayOfWeek;
        now.add(Calendar.DATE, -dayOfWeek);
        return now.getTimeInMillis();
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
