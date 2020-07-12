package com.example.plannerappandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.util.List;
public class TeamMemberListAdapter extends RecyclerView.Adapter<TeamMemberListAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private OnTeamMemberListener mOnTeamMemberListener;
    private final String TAG = "TeamMemberListAdapter";

    // data is passed into the constructor
    public TeamMemberListAdapter(Context context, List<String> data, OnTeamMemberListener onTeamMemberListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mOnTeamMemberListener = onTeamMemberListener;
        for (String t : data){
            Log.w(this.TAG, t);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.team_member_list_row, parent, false);
        return new ViewHolder(view, mOnTeamMemberListener);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(mData.get(position));
        Log.w(this.TAG, mData.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;
        OnTeamMemberListener onTeamMemberListener;

        ViewHolder(View itemView, OnTeamMemberListener onTeamMemberListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.memberName);
            this.onTeamMemberListener = onTeamMemberListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTeamMemberListener.onTeamMemberClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }


    // parent activity will implement this method to respond to click events
    public interface OnTeamMemberListener {
        void onTeamMemberClick(int position);
    }
}