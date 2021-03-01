package com.example.plannerappandroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeamRequestListAdapter extends RecyclerView.Adapter<TeamRequestListAdapter.ViewHolder> {
    private List<TeamRequest> mRequestList;
    private LayoutInflater mLayoutInflater;
    private OnRequestListener mOnRequestListener;
    public final String TAG = "TeamRequestListener";

    TeamRequestListAdapter(Context context, List<TeamRequest> data, OnRequestListener onRequestListener) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRequestList = data;
        this.mOnRequestListener = onRequestListener;
        for (TeamRequest r : data){
            Log.w(TAG, r.mFromUserName);
        }
    }

    @NonNull
    @Override
    public TeamRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.team_request_list_row, parent, false);
        return new ViewHolder(view, mOnRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamRequestListAdapter.ViewHolder holder, int position) {
        holder.myTextView.setText(mRequestList.get(position).mFromUserName);
        Log.w(TAG, "Binding to View: " + mRequestList.get(position).mFromUserName);
    }

    @Override
    public int getItemCount() {
        return mRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        TextView myTextView;
        OnRequestListener onRequestListener;
        private String TAG = "TeamRequestViewHolder";

        public ViewHolder(@NonNull View itemView, OnRequestListener onRequestListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.from_user_name);
            this.onRequestListener = onRequestListener;
            itemView.setOnClickListener(this);
            ImageButton acceptBtn = itemView.findViewById(R.id.acceptRequestBtn);
            acceptBtn.setOnClickListener(this);
            ImageButton rejectBtn = itemView.findViewById(R.id.rejectRequestBtn);
            rejectBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.acceptRequestBtn:
                    onRequestListener.onRequestClick(getAdapterPosition(), "accept");
                    break;
                case R.id.rejectRequestBtn:
                    onRequestListener.onRequestClick(getAdapterPosition(), "reject");
                case R.id.from_user_name:
                    onRequestListener.onRequestClick(getAdapterPosition(), "name");
                    break;
            }
        }
    }

    public interface OnRequestListener {
        void onRequestClick(int position, String accept);
    }
}
