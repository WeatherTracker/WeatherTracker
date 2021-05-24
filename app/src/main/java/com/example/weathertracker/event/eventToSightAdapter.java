package com.example.weathertracker.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Sight;

import java.util.ArrayList;

public class eventToSightAdapter extends RecyclerView.Adapter<eventToSightAdapter.viewHolder> {
    private Context mContext;
    private ArrayList<Sight> mSights;
    public eventToSightAdapter(Context mContext,ArrayList<Sight> sights){
        this.mContext = mContext;
        this.mSights = sights;
    }

    @NonNull
    @Override
    public eventToSightAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_item,parent));
    }

    @Override
    public void onBindViewHolder(@NonNull eventToSightAdapter.viewHolder holder, int position) {
        holder.tv.setText(mSights.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mSights.size();
    }
    class viewHolder extends RecyclerView.ViewHolder{

        private TextView tv;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvSightCard);
        }
    }
}
