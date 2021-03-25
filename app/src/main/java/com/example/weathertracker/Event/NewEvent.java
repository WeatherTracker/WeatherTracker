package com.example.weathertracker.Event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.example.weathertracker.Retrofit.RetrofitManager;
import com.example.weathertracker.Retrofit.RetrofitService;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewEvent extends AppCompatActivity implements OnMapReadyCallback {

    private ImageButton btn_close, btn_done, btn_addPlace, btn_removePlace;
    private TextView tv_place_describe, tv_date, tv_time;
    private EditText et_event_name, et_host_remark;
    private SupportMapFragment mapFragment;
    private double latitude, longitude;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private GoogleMap mMap;
    private SimpleDateFormat dateFormatter, timeFormatter;
    private HashMap<String, Integer> id_map = new HashMap<>();
    private AutoCompleteTextView et_hobbies, et_hobby_class;
    private ArrayAdapter<CharSequence> hobby_class_adapter, hobbies_adapter;
    private String staticTag;
    private int YEAR, MONTH, DAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Calendar now = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        timeFormatter = new SimpleDateFormat("HH:mm");
        YEAR = now.get(Calendar.YEAR);
        MONTH = now.get(Calendar.MONTH);
        DAY = now.get(Calendar.DAY_OF_MONTH);

        id_map.put("戶外活動類", R.array.hobbies_outdoor_events);
        id_map.put("運動類", R.array.hobbies_sports);
        id_map.put("藝文嗜好類", R.array.hobbies_arts);
        id_map.put("益智類", R.array.hobbies_puzzle);
        id_map.put("視聽類", R.array.hobbies_audiovisual);
        id_map.put("休憩社交類", R.array.hobbies_social);
        id_map.put("其它類", R.array.hobbies_others);

        find_id();
        set_listener();

        tv_date.setText(dateFormatter.format(now.getTime()));
        tv_time.setText(timeFormatter.format(now.getTime()));
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
    }

    private void find_id() {
        btn_close = findViewById(R.id.btn_close);
        btn_done = findViewById(R.id.btn_done);
        btn_addPlace = findViewById(R.id.btn_addPlace);
        btn_removePlace = findViewById(R.id.btn_removePlace);
        et_event_name = findViewById(R.id.et_event_name);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        et_host_remark = findViewById(R.id.et_host_remark);
        tv_place_describe = findViewById(R.id.tv_place_describe);
        et_hobbies = findViewById(R.id.et_hobbies);
        et_hobby_class = findViewById(R.id.et_hobby_class);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
    }

    private void set_listener() {
        btn_addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(NewEvent.this);
                startActivityForResult(intent, 200);
            }
        });
        btn_removePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.GONE);
                btn_addPlace.setVisibility(View.VISIBLE);
                btn_removePlace.setVisibility(View.INVISIBLE);
                tv_place_describe.setText("");
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                //todo:
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
            }
        });
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s = year + "-" + (month + 1) + "-" + dayOfMonth;
                tv_date.setText(s);
            }
        }, YEAR, MONTH, DAY);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = hourOfDay + ":" + String.format("%02d", minute);
                tv_time.setText(s);
            }
        }, 12, 0, false);
        hobby_class_adapter = ArrayAdapter.createFromResource(this, R.array.hobby_classes, R.layout.list_item);
        et_hobby_class.setAdapter(hobby_class_adapter);
        et_hobby_class.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    hobbies_adapter = ArrayAdapter.createFromResource(NewEvent.this, id_map.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
                    et_hobbies.setText("");
                    et_hobbies.setAdapter(hobbies_adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_hobbies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().equals("")) {
                    staticTag = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
            btn_addPlace.setVisibility(View.INVISIBLE);
            btn_removePlace.setVisibility(View.VISIBLE);
            Place place = Autocomplete.getPlaceFromIntent(data);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            tv_place_describe.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(NewEvent.this, "fuck", Toast.LENGTH_SHORT).show();
        }
    }
}