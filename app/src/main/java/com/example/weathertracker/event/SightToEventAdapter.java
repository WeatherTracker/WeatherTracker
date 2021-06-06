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

public class SightToEventAdapter extends RecyclerView.Adapter<SightToEventAdapter.viewHolder> {
    private Context mContext;
    private List<Event> mEvents;

    public SightToEventAdapter(Context mContext, List<Event> mEvents) {
        this.mContext = mContext;
        this.mEvents = mEvents;
    }

    @NonNull
    @Override
    public SightToEventAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SightToEventAdapter.viewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SightToEventAdapter.viewHolder holder, int position) {
        holder.tvSightCard.setText(mEvents.get(position).getEventName());
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvSightCard;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvSightCard = itemView.findViewById(R.id.tvSightCard);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    final int position = getAdapterPosition();
                    String json = gson.toJson(mEvents.get(position));
                    System.out.println("json" + json);

                    Intent intent = new Intent(mContext, CheckAndEditActivity.class);
                    intent.putExtra("json", json);
                    intent.putExtra("where","recommend");

                    mContext.startActivity(intent);
                }
            });
        }
    }
}
