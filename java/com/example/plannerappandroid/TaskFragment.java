package com.example.plannerappandroid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tasks";
    private static final String ARG_PARAM2 = "index";

    // TODO: Rename and change types of parameters
    private ArrayList<Task> tasks = new ArrayList<>();
    private Integer index = 0;
    String TAG = "TaskFrag";

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(ArrayList<Task> param1, Integer param2) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "Fragment created!");
        if (getArguments() != null) {
            tasks = getArguments().getParcelableArrayList(ARG_PARAM1);
            index = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Button completeButton = getView().findViewById(R.id.completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "Marking this task as completed.");
            }
        });
        Button deleteButton = getView().findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.w(TAG, "Deleting this task.");
            }
        });
        Button backButton = getView().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.w(TAG, "Back button clicked.");
                if (index > 0){
                    index -= 1;
                }
                else {
                    index = tasks.size() - 1;
                }
                displayTask();
            }
        });
        Button nextButton = getView().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.w(TAG, "Next button clicked.");
                if (index < tasks.size() - 1){
                    index += 1;
                }
                else {
                    index = 0;
                }
                displayTask();
            }
        });

        if (tasks.size() == 1){
            nextButton.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.INVISIBLE);
        }

        displayTask();
    }

    private void displayTask(){
        Log.w(TAG, "Displaying Task " + index);
        Task currentTask = tasks.get(index);
        TextView taskName = getView().findViewById(R.id.taskName);
        taskName.setText(currentTask.m_title);
        TextView taskBody = getView().findViewById(R.id.taskBody);
        String body = "Owner: " + currentTask.m_owner + "\n"
                + "Assignee: " + currentTask.m_assignee + "\n"
                + "Due Date: " + currentTask.m_dueDate.toString() + "\n"
                + "Created Date: " + currentTask.m_createdDate.toString() + "\n"
                + "Points: " + currentTask.m_points + "\n";
        if (currentTask.m_completed){
            body += "Completed: Yes!\n"
                    + "Completed Date: " + currentTask.m_completedDate.toString() + "\n";
        }
        else {
            body += "Completed: " + "No";
        }
        taskBody.setText(body);
    }
}