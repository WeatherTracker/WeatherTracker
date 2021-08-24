package com.example.weathertracker.event;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Event;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class showMemberAdapter extends RecyclerView.Adapter<showMemberAdapter.viewHolder> {
    private Context mContext;
    private List<String> people;
    private String eventId;
    public showMemberAdapter(Context mContext, List<String> people, String eventId) {
        this.mContext = mContext;
        this.people = people;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public showMemberAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new showMemberAdapter.viewHolder(LayoutInflater.from(mContext).inflate(R.layout.member_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull showMemberAdapter.viewHolder holder, int position) {
        holder.name.setText(people.get(position));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
//            tvSightCard = itemView.findViewById(R.id.tvSightCard);
            name = itemView.findViewById(R.id.name);
            Button levelup = itemView.findViewById(R.id.levelUp);
            levelup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("eventId" + eventId );
                    System.out.println("aaaaaaaaa");
                    RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                    Call<String> call = retrofitService.levelUp(eventId);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(itemView.getContext(), "GGGGGGG", Toast.LENGTH_SHORT).show();
                                System.out.println("gggggggggggggggggg");
                            }
                            else {
                                String s = response.body();
                                Toast.makeText(itemView.getContext(), s, Toast.LENGTH_SHORT).show();
                                System.out.println("level-up" + s);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            });
        }
    }
}
