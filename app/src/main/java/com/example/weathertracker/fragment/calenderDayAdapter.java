package com.example.weathertracker.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Event;

import java.util.List;

public class calenderDayAdapter extends RecyclerView.Adapter<calenderDayAdapter.LinearViewHolder> {

    private Context mContext;
    private List<Event> event;


    public calenderDayAdapter(Context context, List<Event> event){
        this.mContext=context;
        this.event=event;
    }

    @Override
    public calenderDayAdapter.LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.calenderday_item,parent,false));
    }

    @Override
    public void onBindViewHolder(calenderDayAdapter.LinearViewHolder  holder, final int position) {
        holder.tv_1.setText(event.get(position).getEventName());
    }

    @Override
    public int getItemCount() {
        return event.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_1;

        public LinearViewHolder(View itemView){
            super(itemView);
            tv_1=itemView.findViewById(R.id.tv_1);
        }
    }


}

