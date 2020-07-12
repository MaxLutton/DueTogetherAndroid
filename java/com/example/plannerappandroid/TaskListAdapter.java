package com.example.plannerappandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<Task> mData;
    private LayoutInflater mInflater;
    private OnTaskListener mOnTaskListener;

    // data is passed into the constructor
    TaskListAdapter(Context context, List<Task> data, OnTaskListener onTaskListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mOnTaskListener = onTaskListener;
        for (Task t : data){
            Log.w("UPCOMING TASK LIST", t.m_title);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.task_list_row, parent, false);
        return new ViewHolder(view, mOnTaskListener);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(mData.get(position).m_title);
        Log.w("Binding to View: ", mData.get(position).m_title);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        OnTaskListener onTaskListener;

        ViewHolder(View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.taskName);
            this.onTaskListener = onTaskListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTaskListener.onTaskClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Task getItem(int id) {
        return mData.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface OnTaskListener {
        void onTaskClick(int position);
    }
}