package com.vk.vsvans.BlogShop.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.vk.vsvans.BlogShop.R;

public class CalendarAlertDialog extends AlertDialog {
    private long mInitialDate;
    private Long select_date;
    private CalendarDialogAdapter mCalendarAdapter;
    private Context mContext;
    private TextView titleText;
    private AppCompatButton positiveButton,negativeButton,prevButton,nextButton;
    private GridView calendarGridView;


    private CalendarDialogAdapter.onItemClickListener mAdapterListener;
    private onClickListener mButtonOkListner;
    private onClickListener mButtonCancelListner;
    private int mTypeEvent;

    float initialX,initialY;
    boolean directionLeft;
    protected CalendarAlertDialog(Context context,
                                  CalendarDialogAdapter.onItemClickListener listener,
                                  int type_event
                                  ) {
        super(context);
        // define listener for adapter
        mAdapterListener = listener;
        mContext=context;
        mTypeEvent =type_event;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Pick a Date");

        setContentView(R.layout.dialog_calendar);

        titleText = findViewById(R.id.titleText);

        mCalendarAdapter=new CalendarDialogAdapter(mContext, mTypeEvent);
        // set listener for adapter-> mListener.onClick->in adapter
        mCalendarAdapter.setOnItemClickListener(mAdapterListener);

        setCancelable(true);

        negativeButton = findViewById(R.id.negativeButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear all selected_date
//                clearSelectedDate();
//                dismiss();
                mButtonCancelListner.onClick();
            }
        });

        positiveButton = findViewById(R.id.positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String[] dates=
                mButtonOkListner.onClick();
                //dismiss();
            }
        });
       // positiveButton.setOnClickListener(mButtonPositiveListener);

        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.prevMonth();
                titleText.setText(mCalendarAdapter.getTitle());
                //setEnabledButton();
            }
        });

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.nextMonth();
                titleText.setText(mCalendarAdapter.getTitle());
               // setEnabledButton();
                }
        });

        calendarGridView =(GridView) findViewById(R.id.calendarGridView);
        calendarGridView.setAdapter(mCalendarAdapter);

       // setSelectedDate(UtilsHelper.getCurrentDate());

        titleText.setText(mCalendarAdapter.getTitle());


        calendarGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String TAG="Test";

                int action = event.getAction();
                if(action==MotionEvent.ACTION_DOWN){
                    initialX =  event.getX();
                    Log.d(TAG, "Down "+initialX);
                }
                else if(action==MotionEvent.ACTION_MOVE){
                    if(initialX<event.getX()) directionLeft=true;
                    else directionLeft=false;
                    initialX = event.getX();
                    Log.d(TAG, "Move "+initialX);
                }
                else if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_CANCEL){
                    //initialX = layoutParams.x;
                    float finalX = event.getX();
                    Log.d(TAG, "Up "+initialX+" "+finalX);
                    if (directionLeft) {
                        prevButton.callOnClick();
                    }else{
                        nextButton.callOnClick();
                    }
                }

                return true;
            }
        });
    }



    private void clearSelectedDate() {
        mCalendarAdapter.clearSelectedDate();
    }
    public void setSelectedDate(long date) {
        mCalendarAdapter.setSelectedDate(date);
    }

    public void setOnClickOkListener(onClickListener onClickListner) {
        this.mButtonOkListner = onClickListner;
    }

    public void setOnClickCancelListener(onClickListener onClickListner) {
        this.mButtonCancelListner = onClickListner;
    }


    public interface onClickListener {
        void onClick();// hear you can pass any type of data as a argument;
    }

}
