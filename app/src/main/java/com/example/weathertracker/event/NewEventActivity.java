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
import com.example.weathertracker.account.SignUpActivity;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.Event;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageButton btnBack, btnDone, btnAddPlace, btnRemovePlace;
    private TextView tvPlaceDescribe, tvDate, tvTime;
    private EditText etEventName, etHostRemark;
    private SupportMapFragment mapFragment;
    private double latitude, longitude;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private GoogleMap mMap;
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private AutoCompleteTextView etHobbies, etHobbyClass;
    private ArrayAdapter<CharSequence> hobbyClassAdapter, hobbiesAdapter;
    private String userId, staticHobbyTag, staticHobbyClass;
    private int year, month, day;
    private ToggleButton isOutDoor, isPublic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        userId = getSharedPreferences("sharedPreferences", MODE_PRIVATE).getString("userId", "");
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

        //todo:改成點擊時間
        tvDate.setText(dateFormatter.format(now.getTime()));
        tvTime.setText(timeFormatter.format(now.getTime()));

        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
    }

    private void findId() {
        btnBack = findViewById(R.id.btnBack);
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
        isOutDoor = findViewById(R.id.isOutDoor);
        isPublic = findViewById(R.id.isPublic);
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
                btnRemovePlace.setVisibility(View.GONE);
                tvPlaceDescribe.setText("");
                //todo:
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event e = new Event(etEventName.getText().toString(), etHostRemark.getText().toString(), tvDate.getText().toString() + " " + tvTime.getText().toString(), etHobbyClass.getText().toString(), etHobbies.getText().toString(), latitude, longitude, Arrays.asList(userId), isPublic.isChecked(), isOutDoor.isChecked());
                RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                Call<Ack> call = retrofitService.newEvent(userId, e);
                call.enqueue(new Callback<Ack>() {
                    @Override
                    public void onResponse(Call<Ack> call, Response<Ack> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(NewEventActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
                        } else {
                            Ack ack = response.body();
                            if (ack.getCode() == 200) {
                                Toast.makeText(NewEventActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();//去信箱收信
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
                System.out.println(e.toString());
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
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
        }, year, month, day);
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
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            tvPlaceDescribe.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(NewEventActivity.this, "fuck", Toast.LENGTH_SHORT).show();
        }
    }
}