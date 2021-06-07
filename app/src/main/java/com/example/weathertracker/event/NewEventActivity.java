package com.example.weathertracker.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.weathertracker.MainActivity;
import com.example.weathertracker.R;
import com.example.weathertracker.account.LoginActivity;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.Event;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.example.weathertracker.retrofit.chartList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageButton btnBack, btnDone, btnAddPlace;
    private TextView tvPlaceDescribe, tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private EditText etEventName, etHostRemark;
    private SupportMapFragment mapFragment;
    private Double latitude = 0.0, longitude = 0.0;
    private DatePickerDialog datePickerDialog, datePickerDialog2;
    private TimePickerDialog timePickerDialog, timePickerDialog2;
    private GoogleMap mMap;
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private AutoCompleteTextView etHobbies, etHobbyClass;
    private ArrayAdapter<CharSequence> hobbyClassAdapter, hobbiesAdapter;
    private String userId, staticHobbyTag, staticHobbyClass;
    private int year, month, day;
    private ToggleButton isOutDoor, isPublic;
    private SwitchCompat isAllDay;
    private LinearLayout timeLayout;
    private ArrayList<String> xLabels = new ArrayList<>();
    private LineChart lineChart;
    private chartList data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        userId = getSharedPreferences("sharedPreferences", MODE_PRIVATE).getString("userId", "");
        staticHobbyTag = "";
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH);
        day = now.get(Calendar.DAY_OF_MONTH);

        idMap.put("戶外活動類", R.array.hobbies_outdoor_events);
        idMap.put("運動類", R.array.hobbies_sports);
        idMap.put("藝文嗜好類", R.array.hobbies_arts);
        idMap.put("益智類", R.array.hobbies_puzzle);
        idMap.put("視聽類", R.array.hobbies_audiovisual);
        idMap.put("休憩社交類", R.array.hobbies_social);
        idMap.put("其它類", R.array.hobbies_others);

        findId();
        setListener();
        String pickedDay = getIntent().getStringExtra("pickedDay");
        tvStartDate.setText(pickedDay);
        tvStartTime.setText(timeFormatter.format(now.getTime()));
        tvEndDate.setText(pickedDay);
        tvEndTime.setText(timeFormatter.format(now.getTime()));

        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
    }

    private void findId() {
        btnBack = findViewById(R.id.btnBack);
        btnDone = findViewById(R.id.btnDone);
        btnAddPlace = findViewById(R.id.btnAddPlace);
        etEventName = findViewById(R.id.etEventName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndTime = findViewById(R.id.tvEndTime);
        etHostRemark = findViewById(R.id.etHostRemark);
        tvPlaceDescribe = findViewById(R.id.tvPlaceDescribe);
        etHobbies = findViewById(R.id.etHobbies);
        etHobbyClass = findViewById(R.id.etHobbyClass);
        isOutDoor = findViewById(R.id.isOutDoor);
        isPublic = findViewById(R.id.isPublic);
        isAllDay = findViewById(R.id.isAllDay);
        lineChart = findViewById(R.id.newEventChart);
        timeLayout = findViewById(R.id.timeLayout);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
    }

    private void setListener() {
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(NewEventActivity.this);
                startActivityForResult(intent, 200);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event e = null;
                String endDateString;
                if (isAllDay.isChecked()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = sdf.parse(tvEndDate.getText().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        c.add(Calendar.DATE, 1);
                        date = c.getTime();
                        endDateString = sdf.format(date);
                        e = new Event(etEventName.getText().toString(), etHostRemark.getText().toString(), tvStartDate.getText().toString() + " 00:00", endDateString + " 00:00", etHobbyClass.getText().toString(), etHobbies.getText().toString(), latitude, longitude, Arrays.asList(userId), isPublic.isChecked(), isOutDoor.isChecked());
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                } else {
                    e = new Event(etEventName.getText().toString(), etHostRemark.getText().toString(), tvStartDate.getText().toString() + " " + tvStartTime.getText().toString(), tvEndDate.getText().toString() + " " + tvEndTime.getText().toString(), etHobbyClass.getText().toString(), etHobbies.getText().toString(), latitude, longitude, Arrays.asList(userId), isPublic.isChecked(), isOutDoor.isChecked());
                }
                if (checkValid(e)) {
                    RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                    Call<Ack> call = retrofitService.newEvent(e);
                    call.enqueue(new Callback<Ack>() {
                        @Override
                        public void onResponse(Call<Ack> call, Response<Ack> response) {
                            if (!response.isSuccessful()) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(NewEventActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            } else {
                                Ack ack = response.body();
                                if (ack.getCode() == 200) {
                                    Toast.makeText(NewEventActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();//成功新增事件
                                    Intent intent = new Intent(NewEventActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(NewEventActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Ack> call, Throwable t) {
                            Toast.makeText(NewEventActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
//                System.out.println(e.toString());
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
            }
        });
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog2.show();
            }
        });
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.show();
            }
        });
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                tvStartDate.setText(s);
                if (latitude != 0.0 && longitude != 0.0) {
                    callChart();
                }
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                tvStartTime.setText(s);
            }
        }, 12, 0, false);
        datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                tvEndDate.setText(s);
            }
        }, year, month, day);
        datePickerDialog2.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                tvEndTime.setText(s);
            }
        }, 12, 0, false);
        hobbyClassAdapter = ArrayAdapter.createFromResource(this, R.array.hobby_classes, R.layout.list_item);
        etHobbyClass.setAdapter(hobbyClassAdapter);
        etHobbyClass.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    hobbiesAdapter = ArrayAdapter.createFromResource(NewEventActivity.this, idMap.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
                    staticHobbyClass = s.toString();
                    etHobbies.setText("");
                    etHobbies.setAdapter(hobbiesAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etHobbies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().equals("")) {
                    staticHobbyTag = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        isAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timeLayout.setVisibility(View.INVISIBLE);
                } else {
                    timeLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean checkValid(Event e) {
        if (etEventName.getText().toString().equals("") || staticHobbyTag.equals("")) {
            new SweetAlertDialog(NewEventActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("活動名稱與類別不得為空")
                    .show();
            return false;
        }
        if (!Event.isTimeValid(e.getStartTime(), e.getEndTime())) {
            new SweetAlertDialog(NewEventActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("時間錯誤")
                    .show();
            return false;
        }
        if (latitude == 0.0 && longitude == 0.0) {
            new SweetAlertDialog(NewEventActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("請選擇地點")
                    .show();
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            Place place = Autocomplete.getPlaceFromIntent(data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            callChart();
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            tvPlaceDescribe.setText(place.getName() + "\n" + place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(NewEventActivity.this, "" + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    private String xLabelFormatter(String x) {
        try {
            SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = std.parse(x);
            SimpleDateFormat dts = new SimpleDateFormat("MM/dd HH:mm");
            return dts.format(date);
        } catch (Exception e) {
            System.out.println("error format");
            return "";
        }
    }

    private ArrayList<Entry> lineChartDataSet(String s) {
        ArrayList<Entry> dataSet = new ArrayList<>();
        String x;
        Double y;
        if (s.equals("溫度")) {
            xLabels.clear();
            for (int i = 0; i < data.getTemperature().size(); i++) {
                x = data.getTemperature().get(i).getTime();
                y = data.getTemperature().get(i).getValue();
                xLabels.add(xLabelFormatter(x));
                dataSet.add(new Entry(i, y.floatValue()));
            }
        } else if (s.equals("AQI")) {
            xLabels.clear();
            for (int i = 0; i < data.getAQI().size(); i++) {
                x = data.getAQI().get(i).getTime();
                y = data.getAQI().get(i).getValue();
                xLabels.add(xLabelFormatter(x));
                dataSet.add(new Entry(i, y.floatValue()));
            }
        } else if (s.equals("紫外線")) {
            xLabels.clear();
            for (int i = 0; i < data.getUV().size(); i++) {
                x = data.getUV().get(i).getTime();
                y = data.getUV().get(i).getValue();
                xLabels.add(xLabelFormatter(x));
                dataSet.add(new Entry(i, y.floatValue()));
            }
        } else if (s.equals("風速")) {
            xLabels.clear();
            for (int i = 0; i < data.getWindSpeed().size(); i++) {
                x = data.getWindSpeed().get(i).getTime();
                y = data.getWindSpeed().get(i).getValue();
                xLabels.add(xLabelFormatter(x));
                dataSet.add(new Entry(i, y.floatValue()));
            }
        } else if (s.equals("濕度")) {
            xLabels.clear();
            for (int i = 0; i < data.getHumidity().size(); i++) {
                x = data.getHumidity().get(i).getTime();
                y = data.getHumidity().get(i).getValue();
                xLabels.add(xLabelFormatter(x));
                dataSet.add(new Entry(i, y.floatValue()));
            }
        } else if (s.equals("降雨機率")) {
            xLabels.clear();
            for (int i = 0; i < data.getPOP().size(); i++) {
                x = data.getPOP().get(i).getTime();
                y = data.getPOP().get(i).getValue();
                xLabels.add(xLabelFormatter(x));
                dataSet.add(new Entry(i, y.floatValue()));
            }
        }
        return dataSet;
    }

    //折線圖
    public void makeChart(String s) {
        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(s), s);
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                try {
                    String i = xLabels.get((int) value);
                    return i;
                } catch (Exception e) {
                    return "N/A";
                }
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5, true);
        xAxis.setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        Description description = lineChart.getDescription();
        description.setText("");
        lineChart.setVisibleXRange(0, 4);
        lineChart.setScaleEnabled(false);

        lineChart.invalidate();
//
//        lineChart.setNoDataText("Data not Available");
//
//        //you can modify your line chart graph according to your requirement there are lots of method available in this library
//
//        //now customize line chart
//        Description description = lineChart.getDescription();
//        description.setText(String.valueOf(month * 100 + date));//顯示文字名稱
//        description.setTextSize(14);//字體大小
//        description.setTextColor(Color.BLUE);//字體顏色
//        description.setPosition(900, 100);
//
//        //設定沒資料時顯示的內容
//        lineChart.setNoDataText("暫時沒有數據");
//        lineChart.setNoDataTextColor(Color.BLUE);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setCircleHoleRadius(2);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setValueTextColor(Color.BLACK);
    }

    private void callChart() {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<chartList> call = retrofitService.getChart(latitude.floatValue(), longitude.floatValue(), tvStartDate.getText().toString());
        call.enqueue(new Callback<chartList>() {
            @Override
            public void onResponse(Call<chartList> call, Response<chartList> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(NewEventActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                } else {
                    data = response.body();
                    makeChart("溫度");
                }
            }

            @Override
            public void onFailure(Call<chartList> call, Throwable t) {

            }
        });
    }
}