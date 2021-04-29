package com.example.weathertracker.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.chartList;
import com.google.gson.Gson;

import java.util.ArrayList;

public class favoriteAdapter extends RecyclerView.Adapter<favoriteAdapter.LinearViewHolder> {

    private Context mContext;
    private ArrayList<String> favorDate,favorDateData;
    private  chartList data = null;


    public favoriteAdapter(Context context, ArrayList<String> favorDate,ArrayList<String> favorDateData){
        this.mContext=context;
        this.favorDate =favorDate;
        this.favorDateData=favorDateData;
    }

    @Override
    public favoriteAdapter.LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.favoriteday_item,parent,false));

    }

    @Override
    public void onBindViewHolder(favoriteAdapter.LinearViewHolder  holder, final int position) {
        String dataAQI="";
        holder.date.setText("日期"+favorDate.get(position).toString());

        Gson gson = new Gson();
        chartList dateData = gson.fromJson(favorDateData.get(position), chartList.class);
        holder.windSpeed.setText(dateData.getWindSpeed().get(0).getTime());
        System.out.println("aadada"+data);

        //System.out.println(data[0].getWindSpeed().get(0).getTime());
//        holder.AQI.setText(data.getWindSpeed().get(0).getTime());
    }

    @Override
    public int getItemCount() {
        return favorDate.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView date,AQI,windSpeed;


        public LinearViewHolder(View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.date);
            AQI = itemView.findViewById(R.id.AQI);
            windSpeed = itemView.findViewById(R.id.windSpeed);


        }
    }


}

