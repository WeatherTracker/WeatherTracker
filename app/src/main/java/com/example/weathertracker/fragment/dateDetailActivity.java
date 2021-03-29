package com.example.weathertracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;

public class dateDetailActivity extends AppCompatActivity {

    private TextView tvDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_detail);
        tvDate = findViewById(R.id.date_tv);
        Intent intent = this.getIntent();
        int date = intent.getIntExtra("DATE",0);
        int month = intent.getIntExtra("MONTH",0);
        System.out.println(month + "/" +  date);

        String data = String.valueOf(month)+String.valueOf(date);
        tvDate.setText(data);
    }
}