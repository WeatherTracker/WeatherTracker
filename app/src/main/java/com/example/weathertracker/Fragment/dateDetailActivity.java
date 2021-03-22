package com.example.weathertracker.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;

public class dateDetailActivity extends AppCompatActivity {

    private TextView date_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_detail);
        date_tv = findViewById(R.id.date_tv);
        Intent intent = this.getIntent();
        int date = intent.getIntExtra("DATE",0);
        int month = intent.getIntExtra("MONTH",0);
        System.out.println(month + "/" +  date);

        String data = String.valueOf(month)+String.valueOf(date);
        date_tv.setText(data);
    }
}