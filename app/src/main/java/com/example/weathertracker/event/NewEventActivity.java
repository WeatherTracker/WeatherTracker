package com.example.weathertracker.event;

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
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
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

public class NewEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageButton btnClose, btnDone, btnAddPlace, btnRemovePlace;
    private TextView tvPlaceDescribe, tvDate, tvTime;
    private EditText etEventName, etHostRemark;
    private SupportMapFragment mapFragment;
    private double latitude, longitude;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private GoogleMap mMap;
    private SimpleDateFormat dateFormatter, timeFormatter;
    private HashMap<String, Integer> idMap = new HashMap<>();
    private AutoCompleteTextView etHobbies, etHobbyClass;
    private ArrayAdapter<CharSequence> hobbyClassAdapter, hobbiesAdapter;
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

        idMap.put("戶外活動類", R.array.hobbies_outdoor_events);
        idMap.put("運動類", R.array.hobbies_sports);
        idMap.put("藝文嗜好類", R.array.hobbies_arts);
        idMap.put("益智類", R.array.hobbies_puzzle);
        idMap.put("視聽類", R.array.hobbies_audiovisual);
        idMap.put("休憩社交類", R.array.hobbies_social);
        idMap.put("其它類", R.array.hobbies_others);

        findId();
        setListener();

        tvDate.setText(dateFormatter.format(now.getTime()));
        tvTime.setText(timeFormatter.format(now.getTime()));
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
    }

    private void findId() {
        btnClose = findViewById(R.id.btnClose);
        btnDone = findViewById(R.id.btnDone);
        btnAddPlace = findViewById(R.id.btnAddPlace);
        btnRemovePlace = findViewById(R.id.btnRemovePlace);
        etEventName = findViewById(R.id.etEventName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etHostRemark = findViewById(R.id.etHostRemark);
        tvPlaceDescribe = findViewById(R.id.tvPlaceDescribe);
        etHobbies = findViewById(R.id.etHobbies);
        etHobbyClass = findViewById(R.id.etHobbyClass);
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
        btnRemovePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.GONE);
                btnAddPlace.setVisibility(View.VISIBLE);
                btnRemovePlace.setVisibility(View.INVISIBLE);
                tvPlaceDescribe.setText("");
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                //todo:
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s = year + "-" + (month + 1) + "-" + dayOfMonth;
                tvDate.setText(s);
            }
        }, YEAR, MONTH, DAY);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = hourOfDay + ":" + String.format("%02d", minute);
                tvTime.setText(s);
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
            btnAddPlace.setVisibility(View.INVISIBLE);
            btnRemovePlace.setVisibility(View.VISIBLE);
            Place place = Autocomplete.getPlaceFromIntent(data);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            tvPlaceDescribe.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(NewEventActivity.this, "fuck", Toast.LENGTH_SHORT).show();
        }
    }
}