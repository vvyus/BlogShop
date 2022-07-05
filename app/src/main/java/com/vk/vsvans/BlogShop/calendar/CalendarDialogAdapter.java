package com.vk.vsvans.BlogShop.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

//import com.google.type.DateTime;
import com.vk.vsvans.BlogShop.AppStart;
import com.vk.vsvans.BlogShop.R;
import com.vk.vsvans.BlogShop.util.UtilsHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
public class CalendarDialogAdapter extends BaseAdapter {
    private List<Date> dateArray = new ArrayList();
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;

    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#808080");
    private static final int DEFAULT_OFF_MONTH_DAY_COLOR = Color.parseColor("#a0a0a0");
    int currentDayColor=Color.parseColor("#1DA6E0");//"#a0a0a0"

    private HashMap<String, Date> selected_date = new HashMap<String, Date>();
    int default_background_color= Color.WHITE;
    int select_background_color=Color.RED; //0xff008577;
    int select_text_color=Color.WHITE;
    int text_color_event=Color.parseColor("#008577");
    int text_color_calendar=DEFAULT_TEXT_COLOR;//Color.BLACK;
    private Animation anim;
    private long start_range_select=0;

    onItemClickListener mItemClickListener;

    HashMap<String,String> month_names=new HashMap<String,String>();
    int select_position;
    HashMap<String, Integer> calendar_events = new HashMap<String, Integer>();
    int mType_event;
    //float dp;
    //After expanding the custom cell, define Wiget here
    private static class ViewHolder {
        public TextView dateText;
        public TextView eventText;
        public CardView cvCalendar;
    }

    private void initMonth() {
        month_names.put("January","Январь");
        month_names.put("February","Февраль");
        month_names.put("March","Март");
        month_names.put("April","Апрель");
        month_names.put("May","Май");
        month_names.put("June","Июнь");
        month_names.put("July","Июль");
        month_names.put("August","Август");
        month_names.put("September","Сентябрь");
        month_names.put("October","Октябрь");
        month_names.put("November","Ноябрь");
        month_names.put("December","Декабрь");
    }

    private void initEvent() {
        //!
        //calendar_events=mainActivity.getCalendarEvents();
    }

    private void initRemindersEvent() {
        //!calendar_events=RemindersActivity.getInstance().getCalendarEvents();
    }

    public CalendarDialogAdapter(Context context, int type_event,Date startTime, HashMap<String, Integer> calendar_events){
        mContext = context;
        //float dp = mContext.getResources().getDisplayMetrics().density;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager(startTime);
        //mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
        select_position=-1;
        // currentDayColor=//context.getResources().getColor(R.color.teal_200);
        mType_event =type_event;
        initMonth();
        if(type_event==1) {
            initEvent();
            this.calendar_events=calendar_events;
        }else if(type_event==2){
            initRemindersEvent();
        }
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Date getItem(int position) {
        return dateArray.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
 //       if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_item, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);
            holder.eventText = convertView.findViewById(R.id.eventText);
            holder.cvCalendar=convertView.findViewById(R.id.cvCalendar);
            convertView.setLayoutParams( new AbsListView.LayoutParams(90,    100));

            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder)convertView.getTag();
//        }

        //Specify cell size
//        float dp = mContext.getResources().getDisplayMetrics().density;
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp,
//                (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
//        convertView.setLayoutParams(params);

        holder.cvCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onItemClickListner.onClick(position);
                Date value=dateArray.get(position);
                String key=UtilsHelper.getDate(value.getTime());
                if(start_range_select>0) {
                    long end_range_select = value.getTime();
                    if (start_range_select != end_range_select) {
                        Date end = start_range_select > end_range_select ? new Date(start_range_select) : new Date(end_range_select);
                        Date current = start_range_select > end_range_select ? new Date(end_range_select) : new Date(start_range_select);
                        while (current.getTime() <= end.getTime()) {
                            String key_ = UtilsHelper.getDate(current.getTime());
                            if (selected_date.get(key_) == null) selected_date.put(key_, current);
                            mItemClickListener.onClick(current);
                            current = UtilsHelper.addDays(current, 1);
                        }
                    }
                    start_range_select = 0;
                    v.clearAnimation();
                    Toast.makeText(mContext, "Закончено выделение диапазона!", Toast.LENGTH_LONG);
//                }else if(start_range_select==0){
//                    start_range_select=value.getTime();
//                    Toast.makeText(mContext, "Начато выделение диапазона!", Toast.LENGTH_LONG);
                }else{
                    if(selected_date.get(key)==null){
                        selected_date.put(key,value);
                    }else selected_date.remove(key);
                }
                notifyDataSetChanged();

                mItemClickListener.onClick(value);

                notifyDataSetChanged();
            }
        });
        //for rangecells select
        holder.cvCalendar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Date value=dateArray.get(position);
                if(start_range_select==0){
                    start_range_select=value.getTime();
                    v.startAnimation(anim);
                    Toast.makeText(mContext, "Начато выделение диапазона!", Toast.LENGTH_LONG);
                } else start_range_select=0;
                return true;
            }
        });
        //show the dates day
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));

        // set background color
        Date value=dateArray.get(position);
        String key=UtilsHelper.getDate(value.getTime());
        if(selected_date.get(key)!=null){
            //convertView.setBackgroundColor(Color.WHITE);
            holder.cvCalendar.setBackgroundColor(select_background_color); //

        } else if (mDateManager.isToday(dateArray.get(position))){
            holder.cvCalendar.setBackgroundColor(currentDayColor);//convertView.setBackgroundColor(currentDayColor);
        }else if (mDateManager.isCurrentMonth(dateArray.get(position))){
            holder.cvCalendar.setBackgroundColor(Color.WHITE);//convertView.setBackgroundColor(Color.WHITE);//current month
        }else {
            holder.cvCalendar.setBackgroundColor(Color.LTGRAY);//convertView.setBackgroundColor(Color.LTGRAY);
        }

        //set text color Sunday to red, Saturday to blue
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            // 1 and 7 is weekend
            case 1:
            case 7:
                if(selected_date.get(key)!=null)
                    colorId =select_text_color;
                else
                    colorId =Color.RED;
                break;

            default:
                if(selected_date.get(key)!=null) colorId =select_text_color;
                else colorId =text_color_calendar;
                break;
        }
        // show events
        holder.dateText.setTextColor(colorId);
        Integer event_int=getEvent(position);
        if(event_int!=null && mType_event!=0) {
            holder.eventText.setText(event_int.toString());
            if(selected_date.get(key)!=null)
                holder.eventText.setTextColor(select_text_color);
            else
                holder.eventText.setTextColor(text_color_event);

        }else {
            holder.eventText.setText("");
        }

//        if(start_range_select==0) holder.dateText.clearAnimation();
//        else if(new Date(start_range_select)==dateArray.get(position))holder.dateText.startAnimation(anim);
//        notifyDataSetChanged();
        return convertView;
    }

    //Get display month
    public String getTitle(){
        String current_month=mDateManager.getCurrentMonth();
        String current_month_ru=month_names.get(current_month);
        return  current_month_ru==null?current_month:current_month_ru+ ", " + mDateManager.getCurrentYear();
    }

    //Next month display
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //Previous month display
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    public Integer getEvent( int position) {
        Date date=dateArray.get(position);
        long time=date.getTime();
        String event_key = UtilsHelper.getDate(time);
        Integer event_int = calendar_events.get(event_key);
        if (event_int != null && event_int != 0) {
            return event_int;
        } else return null;
    }

    public void setSelectedDate(long date) {
        Date value=new Date(date);
        String key= UtilsHelper.getDate(date);
        if(selected_date.get(key)==null){
            selected_date.put(key,value);
        }//else selected_date.remove(key);
        notifyDataSetChanged();
// no send date to dialog listener
        //mItemClickListener.onClick(value);
    }

    public void clearSelectedDate() {
        for (Date value : selected_date.values()) {
            mItemClickListener.onClick(value);
        }
    }

    public ArrayList<String> getSelectedDate(){
        //String[] keys = (String[])selected_date.keySet().toArray();
        ArrayList<String> keys = new ArrayList<String>(selected_date.keySet());
        //keys = new ArrayList<String>(selected_date.keySet());
        //Date date = (Date)selected_date.get(date_str);
       return keys;
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListner) {
        this.mItemClickListener = onItemClickListner;
    }

    public interface onItemClickListener {
        void onClick(Date date);// hear you can pass any type of data as a argument;
    }
}