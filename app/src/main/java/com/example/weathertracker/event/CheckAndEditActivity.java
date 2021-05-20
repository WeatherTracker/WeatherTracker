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
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Event;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CheckAndEditActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RetrofitService retrofitService;
    private Event event;
    private ImageButton btnEdit, btnBack, btnDone, btnAddPlace, btnRemovePlace;
    private TextView tvPlaceDescribe, tvStartDate, tvStartTime,tvEndDate,tvEndTime;
    private EditText etEventName, etHostRemark;
    private SupportMapFragment mapFragment;
    private double latitude, longitude;
    private DatePickerDialog datePickerDialog,datePickerDialog2;
    private TimePickerDialog timePickerDialog,timePickerDialog2;
    private GoogleMap mMap;
    private SimpleDateFormat dateFormatter, timeFormatter;
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private AutoCompleteTextView etHobbies, etHobbyClass;
    private ArrayAdapter<CharSequence> hobbyClassAdapter, hobbiesAdapter;
    private String staticHobbyTag, staticHobbyClass, userId;
    private int year, month, day;
    private ToggleButton isOutDoor, isPublic;
    private TextInputLayout hobbyClass, hobbies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_and_edit);

        Intent intent = this.getIntent();
        String json = intent.getStringExtra("json");
        System.out.println("Event:"+json);

        Gson gson = new Gson();
        Event event0 = gson.fromJson(json, Event.class);

        userId = getSharedPreferences("sharedPreferences", MODE_PRIVATE).getString("userId", "");
        userId = "uuid";

        //todo:
        event = event0;
        //event = new Event(event0.getEventName(), event0.getHostRemark(), event0.getStartTime(),event0.getEndTime(), event0.getStaticHobbyClass(), event0.getStaticHobbyTag(), event0.getLatitude(), event0.getLongitude(), event0.getHosts(), event0.isPublic(), event0.isOutDoor());

        idMap.put("戶外活動類", R.array.hobbies_outdoor_events);
        idMap.put("運動類", R.array.hobbies_sports);
        idMap.put("藝文嗜好類", R.array.hobbies_arts);
        idMap.put("益智類", R.array.hobbies_puzzle);
        idMap.put("視聽類", R.array.hobbies_audiovisual);
        idMap.put("休憩社交類", R.array.hobbies_social);
        idMap.put("其它類", R.array.hobbies_others);

        findId();
        setListener();

        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
        initField();

    }

    private void findId() {
        btnDone = findViewById(R.id.btnDone);
        btnEdit = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnAddPlace = findViewById(R.id.btnAddPlace);
        btnRemovePlace = findViewById(R.id.btnRemovePlace);
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
        hobbyClass = findViewById(R.id.hobbyClass);
        hobbies = findViewById(R.id.hobbies);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
    }

    private void setListener() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditable();
                btnDone.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNonEditable();
                btnDone.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
//                Call<Ack> call = retrofitService.editEvent(event);
//                call.enqueue(new Callback<Ack>() {
//                    @Override
//                    public void onResponse(Call<Ack> call, Response<Ack> response) {
//                        if (!response.isSuccessful()) {
//                            Toast.makeText(CheckAndEditActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Ack ack = response.body();
//                            if (ack.getCode() == 200) {
//                                Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(CheckAndEditActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Ack> call, Throwable t) {
//                        Toast.makeText(CheckAndEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(CheckAndEditActivity.this);
                startActivityForResult(intent, 200);
            }
        });
        btnRemovePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.GONE);
                btnAddPlace.setVisibility(View.VISIBLE);
                btnRemovePlace.setVisibility(View.GONE);
                tvPlaceDescribe.setText("");
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
                String s = year + "-" + (month + 1) + "-" + dayOfMonth;
                tvStartDate.setText(s);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = hourOfDay + ":" + String.format("%02d", minute);
                tvStartTime.setText(s);
            }
        }, 12, 0, false);

        datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String s = year + "-" + (month + 1) + "-" + dayOfMonth;
                tvStartDate.setText(s);
            }
        }, year, month, day);
        datePickerDialog2.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = hourOfDay + ":" + String.format("%02d", minute);
                tvStartTime.setText(s);
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
                    hobbiesAdapter = ArrayAdapter.createFromResource(CheckAndEditActivity.this, idMap.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
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
    }

    private void initField() {
        setNonEditable();
        etEventName.setText(event.getEventName());
        tvStartDate.setText(event.strSplit()[0]);
        tvStartTime.setText(event.strSplit()[1]);
        isOutDoor.setChecked(event.isOutDoor());
        isPublic.setChecked(event.isPublic());
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 15));
        etHostRemark.setText(event.getHostRemark());
        etHobbyClass.setText(event.getStaticHobbyClass(), false);
        etHobbies.setText(event.getStaticHobbyTag(), false);
        if (event.isHost(userId)) {
            btnEdit.setVisibility(View.VISIBLE);
        }
    }

    private void setEditable() {
        etEventName.setEnabled(true);
        hobbyClass.setEndIconVisible(true);
        hobbies.setEndIconVisible(true);
        etHobbyClass.setEnabled(true);
        etHobbies.setEnabled(true);
        etHostRemark.setEnabled(true);
        isPublic.setEnabled(true);
        isOutDoor.setEnabled(true);
        tvStartDate.setEnabled(true);
        tvStartTime.setEnabled(true);
        btnAddPlace.setVisibility(View.VISIBLE);

    }

    private void setNonEditable() {
        etEventName.setEnabled(false);
        hobbyClass.setEndIconVisible(false);
        hobbies.setEndIconVisible(false);
        etHobbyClass.setEnabled(false);
        etHobbies.setEnabled(false);
        etHostRemark.setEnabled(false);
        isPublic.setEnabled(false);
        isOutDoor.setEnabled(false);
        tvStartDate.setEnabled(false);
        tvStartTime.setEnabled(false);
        btnAddPlace.setVisibility(View.GONE);
        btnRemovePlace.setVisibility(View.GONE);

        etEventName.setTextColor(getResources().getColor(R.color.white));
        etHobbyClass.setTextColor(getResources().getColor(R.color.white));
        etHobbies.setTextColor(getResources().getColor(R.color.white));
        etHostRemark.setTextColor(getResources().getColor(R.color.white));
        isPublic.setTextColor(getResources().getColor(R.color.white));
        isOutDoor.setTextColor(getResources().getColor(R.color.white));
        tvStartDate.setTextColor(getResources().getColor(R.color.white));
        tvStartTime.setTextColor(getResources().getColor(R.color.white));

        //todo:
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
            btnAddPlace.setVisibility(View.GONE);
            btnRemovePlace.setVisibility(View.VISIBLE);
            Place place = Autocomplete.getPlaceFromIntent(data);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            tvPlaceDescribe.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(CheckAndEditActivity.this, "fuck", Toast.LENGTH_SHORT).show();
        }
    }
}