package com.example.weathertracker.event;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Event;
import com.google.gson.Gson;

import java.util.List;

public class showMemberAdapter extends RecyclerView.Adapter<showMemberAdapter.viewHolder> {
    private Context mContext;

    public showMemberAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public showMemberAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new showMemberAdapter.viewHolder(LayoutInflater.from(mContext).inflate(R.layout.member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull showMemberAdapter.viewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return  10;
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvSightCard;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
//            tvSightCard = itemView.findViewById(R.id.tvSightCard);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
