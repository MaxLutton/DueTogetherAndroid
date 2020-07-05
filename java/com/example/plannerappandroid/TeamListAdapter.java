package com.example.plannerappandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.util.List;
public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {

    private List<Team> mData;
    private LayoutInflater mInflater;
    private OnTeamListener mOnTeamListener;

    // data is passed into the constructor
    public TeamListAdapter(Context context, List<Team> data, OnTeamListener onTeamListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mOnTeamListener = onTeamListener;
        for (Team t : data){
            Log.w("TEAM LIST", t.m_name);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.team_list_row, parent, false);
        return new ViewHolder(view, mOnTeamListener);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(mData.get(position).m_name);
        Log.w("Binding to View: ", mData.get(position).m_name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;
        OnTeamListener onTeamListener;

        ViewHolder(View itemView, OnTeamListener onTeamListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.teamName);
            this.onTeamListener = onTeamListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTeamListener.onTeamClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Team getItem(int id) {
        return mData.get(id);
    }


    // parent activity will implement this method to respond to click events
    public interface OnTeamListener {
        void onTeamClick(int position);
    }
}