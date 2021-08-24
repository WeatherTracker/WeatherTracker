package com.example.weathertracker.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weathertracker.R;
import com.example.weathertracker.event.NewEventActivity;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutFragment extends Fragment {
    public LogoutFragment() {
        // Required empty public constructor
    }

    private String userId;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        userId = getContext().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE).getString("userId", "");
        btnLogout = root.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Ack> call = retrofitService.logout(userId);
                call.enqueue(new Callback<Ack>() {
                    @Override
                    public void onResponse(Call<Ack> call, Response<Ack> response) {
                        if (response.isSuccessful()) {
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Ack> call, Throwable t) {
                        Toast.makeText(getActivity(), "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return root;
    }


}