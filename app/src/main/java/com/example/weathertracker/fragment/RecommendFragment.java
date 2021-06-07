package com.example.weathertracker.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.event.SightToEventAdapter;
import com.example.weathertracker.retrofit.Event;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends Fragment {

    private View root;
    private String userId;
    private SearchView searchView;
    private RecyclerView rvRecommendEvents;
    private RetrofitService retrofitService;

    public RecommendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_recommend, container, false);
        userId = getContext().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE).getString("userId", "");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        float Longitude = sharedPreferences.getFloat("Longitude", 0);
        float Latitude = sharedPreferences.getFloat("Latitude", 0);

        initId();
        setListener();

        retrofitService = RetrofitManager.getInstance().getService();
        Call<List<Event>> call = retrofitService.getRecommendEvents(userId, Longitude, Latitude);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                } else {
                    List<Event> events = response.body();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rvRecommendEvents.setLayoutManager(linearLayoutManager);
                    rvRecommendEvents.setAdapter(new SightToEventAdapter(getContext(), events));
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        });
        return root;
    }

    private void initId() {
        searchView = root.findViewById(R.id.searchView);
        rvRecommendEvents = root.findViewById(R.id.recommendEvents);
    }

    private void setListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                Call<List<Event>> call = retrofitService.searchEvent(query);
                call.enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getContext(), "錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                        }else{
                            List<Event> events = response.body();
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            rvRecommendEvents.setLayoutManager(linearLayoutManager);
                            rvRecommendEvents.setAdapter(new SightToEventAdapter(getContext(), events));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Event>> call, Throwable t) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

}
