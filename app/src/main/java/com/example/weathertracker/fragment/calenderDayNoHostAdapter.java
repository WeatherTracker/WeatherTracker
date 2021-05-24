package com.example.weathertracker.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Event;

import java.util.List;

public class calenderDayNoHostAdapter extends RecyclerView.Adapter<calenderDayNoHostAdapter.LinearViewHolder> {

    private Context mContext;
    private List<Event> event;
    private String ID;

    public calenderDayNoHostAdapter(Context context, List<Event> event, String ID){
        this.mContext=context;
        this.event=event;
        this.ID=ID;
    }

    @Override
    public calenderDayNoHostAdapter.LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.calenderday_item,parent,false));
    }

    @Override
    public void onBindViewHolder(calenderDayNoHostAdapter.LinearViewHolder  holder, final int position) {
        boolean flag=false;
        for (int i=0;i<event.get(position).getHosts().size();i++){
            System.out.println("ID:" + ID + "host" + event.get(position).getHosts().get(i));
            if(ID.equals(event.get(position).getHosts().get(i))) {
                //todo:
                //flag = true;
            }
        }
        System.out.println(flag);
        if (flag != true){
            holder.tv_1.setText(event.get(position).getEventName());
            holder.calenderday_item.setBackgroundResource(R.drawable.nohost);
            System.out.println(event.get(position).getEventName());
        }
        else {
            holder.calenderday_item.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return event.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_1;
        private LinearLayout calenderday_item;
        public LinearViewHolder(View itemView){
            super(itemView);
            calenderday_item = itemView.findViewById(R.id.calenderday_item);
            tv_1=itemView.findViewById(R.id.tv_1);
        }
    }


}
