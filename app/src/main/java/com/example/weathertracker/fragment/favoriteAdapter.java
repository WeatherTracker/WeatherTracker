package com.example.weathertracker.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        String dataWeedSpeed="",dataPOP="",dataUV="",dataHumidity="",dataTemperature="";
        holder.date.setText("日期"+favorDate.get(position));
        Gson gson = new Gson();
        chartList dateData = gson.fromJson(favorDateData.get(position), chartList.class);

        //weedspeed
        for(int i=0;i<dateData.getWindSpeed().size();i++){
            if(dateData.getWindSpeed().get(i).getTime().substring(0,10).equals(favorDate.get(position))){
                dataWeedSpeed += dateData.getWindSpeed().get(i).getTime().substring(11,16) +" "+ dateData.getWindSpeed().get(i).getValue()+"級 ";
            }
        }
        holder.windSpeed.setText(dataWeedSpeed);

        //POP
        for(int i=0;i<dateData.getPOP().size();i++){
            if(dateData.getPOP().get(i).getTime().substring(0,10).equals(favorDate.get(position))){
                dataPOP += dateData.getPOP().get(i).getTime().substring(11,16) +" "+ dateData.getPOP().get(i).getValue()+"% ";
            }
        }
        holder.POP.setText(dataPOP);

        //UV
        for(int i=0;i<dateData.getUV().size();i++){
            if(dateData.getUV().get(i).getTime().substring(0,10).equals(favorDate.get(position))){
                dataUV += dateData.getUV().get(i).getTime().substring(11,16) +" "+ dateData.getUV().get(i).getValue()+" ";
            }
        }
        holder.UV.setText(dataUV);

        //humidity
        for(int i=0;i<dateData.getHumidity().size();i++){
            if(dateData.getHumidity().get(i).getTime().substring(0,10).equals(favorDate.get(position))){
                dataHumidity += dateData.getHumidity().get(i).getTime().substring(11,16) +" "+ dateData.getHumidity().get(i).getValue()+"% ";
            }
        }
        holder.humidity.setText(dataHumidity);

        //temperature
        for(int i=0;i<dateData.getTemperature().size();i++){
            if(dateData.getTemperature().get(i).getTime().substring(0,10).equals(favorDate.get(position))){
                dataTemperature += dateData.getTemperature().get(i).getTime().substring(11,16) +" "+ dateData.getTemperature().get(i).getValue()+"度 ";
            }
        }
        holder.temperature.setText(dataTemperature);
    }

    @Override
    public int getItemCount() {
        return favorDate.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView date,AQI,windSpeed,POP,UV,humidity,temperature;
        private ImageView event_delete;


        public LinearViewHolder(View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.date);
            AQI = itemView.findViewById(R.id.AQI);
            windSpeed = itemView.findViewById(R.id.windSpeed);
            POP = itemView.findViewById(R.id.POP);
            UV = itemView.findViewById(R.id.UV);
            humidity = itemView.findViewById(R.id.humidity);
            temperature = itemView.findViewById(R.id.temperature);
            event_delete = itemView.findViewById(R.id.event_delete);
            event_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = mContext.getSharedPreferences("favorite", Context.MODE_PRIVATE);
                    final int position = getAdapterPosition();
                    String deleteDate = favorDate.get(position);
                    System.out.println(deleteDate);
                    favorDate.remove(position);
                    favorDateData.remove(position);
                    preferences.edit().remove(deleteDate).commit();
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            });
        }
    }


}

