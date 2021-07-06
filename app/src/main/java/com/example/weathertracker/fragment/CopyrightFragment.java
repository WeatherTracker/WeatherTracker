package com.example.weathertracker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weathertracker.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CopyrightFragment extends Fragment {
    public CopyrightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_copyright, container, false);

        TextView link1,link2,link3,link4,link5;

        link1 = root.findViewById(R.id.link1);
        link2 = root.findViewById(R.id.link2);
        link3 = root.findViewById(R.id.link3);
        link4 = root.findViewById(R.id.link4);
        link5 = root.findViewById(R.id.link5);

        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://gis.taiwan.net.tw/XMLReleaseALL_public/scenic_spot_C_f.json");
            }
        });
        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://data.epa.gov.tw/api/v1");
            }
        });
        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://alerts.ncdr.nat.gov.tw/api_swagger/index.html");
            }
        });
        link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://e-service.cwb.gov.tw/HistoryDataQuery/index.jsp");
            }
        });
        link5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://opendata.cwb.gov.tw/dist/opendata-swagger.html");
            }
        });



        return root;
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

}