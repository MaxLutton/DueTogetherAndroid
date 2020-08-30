package com.example.plannerappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class CalendarActivity extends AppCompatActivity {

    private class TaskDateMapper {
        ArrayList<Task> tasks = new ArrayList<>();
        Integer completed = 0;
        Integer pending = 0;
        public TaskDateMapper(Task t){
            tasks.add(t);
        }
    }

    private CalendarView calendarView;
    String TAG = "Calendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);


        // Highlight days with due dates
        List<EventDay> events = new ArrayList<>();

        // Calendar -> Uncompleted Count, Completed Count
        HashMap<DateTime, TaskDateMapper> dates = new HashMap<>();
        Intent intent = getIntent();
        ArrayList<Task> tasks = intent.getParcelableArrayListExtra("tasks");
        if (tasks != null){
            // Associate dates with tasks
            for (Task task : tasks){
                // Calendar c = task.m_dueDate.toCalendar(Locale.getDefault());
                if (dates.containsKey(task.m_dueDate)){
                    TaskDateMapper tdm = dates.get(task.m_dueDate);
                    tdm.tasks.add(task);
                    if (task.m_completed){
                        tdm.completed += 1;
                    }
                    else {
                        tdm.pending += 1;
                    }
                }
                else {
                    TaskDateMapper tdm = new TaskDateMapper(task);
                    if (task.m_completed){
                        tdm.completed += 1;
                    }
                    else{
                        tdm.pending += 1;
                    }
                    dates.put(task.m_dueDate, tdm);
                }
            }

            for (java.util.Map.Entry<DateTime, TaskDateMapper> dateTaskDateMapperEntry : dates.entrySet()) {
                Integer tasksLeft = dateTaskDateMapperEntry.getValue().pending;
                Integer tasksCompleted = dateTaskDateMapperEntry.getValue().completed;
                if (tasksLeft != 0) {
                    Log.w(TAG, "Adding Red event at " + dateTaskDateMapperEntry.getKey().toString());
                    events.add(new EventDay(dateTaskDateMapperEntry.getKey().toCalendar(Locale.getDefault()), DrawableUtils.getCircleDrawableWithText(this, tasksLeft.toString(), "red")));
                } else {
                    Log.w(TAG, "Adding Green event at " + dateTaskDateMapperEntry.getKey().toString());
                    events.add(new EventDay(dateTaskDateMapperEntry.getKey().toCalendar(Locale.getDefault()), DrawableUtils.getCircleDrawableWithText(this, tasksCompleted.toString(), "green")));
                }
            }

        }
        else {
            Log.w(TAG, "Got not tasks.");
        }
        calendarView.setOnDayClickListener(eventDay -> {
            Toast.makeText(CalendarActivity.this, "You clicked: {}" + eventDay.toString(), Toast.LENGTH_SHORT).show();

            // TODO: Optimize this. We shouldn't need to iterate over a hashmap.
            DateTime day = new DateTime(eventDay.getCalendar());
            for (java.util.Map.Entry<DateTime, TaskDateMapper> dateTaskDateMapperEntry : dates.entrySet()){
                if (dateTaskDateMapperEntry.getKey().getDayOfYear() == day.getDayOfYear()){
                    day = dateTaskDateMapperEntry.getKey();
                    break;
                }
            }


            Log.w(TAG, "Date is " + day.toString());
            if (dates.containsKey(day)){
                Log.w(TAG, "This day has tasks!");
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.fragmentPlaceholder, TaskFragment.newInstance(dates.get(day).tasks, 0));
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
            else {
                Log.w(TAG, "this day has no tasks.");
            }

        });

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -2);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 5);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);
        calendarView.setEvents(events);
    }
}