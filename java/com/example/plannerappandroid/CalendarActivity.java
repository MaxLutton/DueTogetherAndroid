package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    String TAG = "Calendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Toast.makeText(CalendarActivity.this, "You clicked: {}" + eventDay.toString(), Toast.LENGTH_SHORT).show();
                // TODO: Launch Task View fragment

            }
        });

        // Highlight days with due dates
        List<EventDay> events = new ArrayList<>();

        // Calendar -> Uncompleted Count, Completed Count
        HashMap<Calendar, Pair<Integer, Integer>> dates = new HashMap<>();
        Intent intent = getIntent();
        ArrayList<Task> tasks = intent.getParcelableArrayListExtra("tasks");
        if (tasks != null){
            // Associate dates with tasks
            for (Task task : tasks){
                Calendar c = task.m_dueDate.toCalendar(Locale.getDefault());
                if (dates.containsKey(c)){
                    if (task.m_completed){
                        dates.replace(c, new Pair(dates.get(c).first, dates.get(c).second + 1));
                    }
                    else {
                        dates.replace(c, new Pair(dates.get(c).first + 1, dates.get(c).second));
                    }
                }
                else {
                    if (task.m_completed){
                        dates.put(c, new Pair(0,1));
                    }
                    else{
                        dates.put(c, new Pair(1,0));
                    }
                }
            }

            for (java.util.Map.Entry<Calendar, Pair<Integer, Integer>> calendarPairEntry : dates.entrySet()) {
                Integer tasksLeft = calendarPairEntry.getValue().first;
                Integer tasksCompleted = calendarPairEntry.getValue().second;
                if (tasksLeft != 0) {
                    Log.w(TAG, "Adding Red event at " + calendarPairEntry.getKey().toString());
                    events.add(new EventDay(calendarPairEntry.getKey(), DrawableUtils.getCircleDrawableWithText(this, tasksLeft.toString(), "red")));
                } else {
                    Log.w(TAG, "Adding Green event at " + calendarPairEntry.getKey().toString());
                    events.add(new EventDay(calendarPairEntry.getKey(), DrawableUtils.getCircleDrawableWithText(this, tasksCompleted.toString(), "green")));
                }
            }

        }
        else {
            Log.w(TAG, "Got not tasks.");
        }



        /*
        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, DrawableUtils.getCircleDrawableWithText(this, "M", "red")));

        Calendar calendar3 = Calendar.getInstance();
        calendar3.add(Calendar.DAY_OF_MONTH, 7);
        events.add(new EventDay(calendar3, R.drawable.sample_three_icons));

        Calendar calendar4 = Calendar.getInstance();
        calendar4.add(Calendar.DAY_OF_MONTH, 13);
        events.add(new EventDay(calendar4, DrawableUtils.getThreeDots(this)));
        */
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -2);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 5);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);
        calendarView.setEvents(events);
    }
}