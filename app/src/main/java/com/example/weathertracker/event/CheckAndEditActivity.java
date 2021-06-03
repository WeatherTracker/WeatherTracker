package com.example.weathertracker.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.Event;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.example.weathertracker.retrofit.Sight;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckAndEditActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RetrofitService retrofitService;
    private Event event;
    private ImageButton btnEdit, btnBack, btnDone, btnAddPlace, btnRemovePlace, btnSchedule, btnTags, btnDelete;
    private TextView tvPlaceDescribe, tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private EditText etEventName, etHostRemark;
    private SupportMapFragment mapFragment;
    private double latitude, longitude;
    private DatePickerDialog datePickerDialog, datePickerDialog2;
    private TimePickerDialog timePickerDialog, timePickerDialog2;
    private GoogleMap mMap;
    private SimpleDateFormat dateFormatter, timeFormatter;
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private AutoCompleteTextView etHobbies, etHobbyClass;
    private ArrayAdapter<CharSequence> hobbyClassAdapter, hobbiesAdapter;
    private String staticHobbyTag, staticHobbyClass, userId;
    private int year, month, day;
    private ToggleButton isOutDoor, isPublic;
    private TextInputLayout hobbyClass, hobbies;
    private SwitchCompat isAllDay;
    private RecyclerView eventToSight;
    private ArrayList<Sight> sights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_and_edit);

        Intent intent = this.getIntent();
        String json = intent.getStringExtra("json");
        System.out.println("Event:" + json);

        Gson gson = new Gson();
        event = gson.fromJson(json, Event.class);
        userId = getSharedPreferences("sharedPreferences", MODE_PRIVATE).getString("userId", "");
        retrofitService = RetrofitManager.getInstance().getService();

        System.out.println(event.getHosts().get(0));
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
        getEventToSight();

        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
        initField();
    }

    private void getEventToSight() {
        Call<List<Sight>> call = retrofitService.getRecommendSights(event.getLongitude(), event.getLatitude());
        call.enqueue(new Callback<List<Sight>>() {
            @Override
            public void onResponse(Call<List<Sight>> call, Response<List<Sight>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CheckAndEditActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
                } else {
                    eventToSight.setLayoutManager(new LinearLayoutManager(CheckAndEditActivity.this));
                    eventToSight.setAdapter(new eventToSightAdapter(CheckAndEditActivity.this, sights));
                }
            }

            @Override
            public void onFailure(Call<List<Sight>> call, Throwable t) {
                Toast.makeText(CheckAndEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findId() {
        btnDone = findViewById(R.id.btnDone);
        btnEdit = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnAddPlace = findViewById(R.id.btnAddPlace);
        btnRemovePlace = findViewById(R.id.btnRemovePlace);
        btnTags = findViewById(R.id.btnTags);
        btnDelete = findViewById(R.id.btnDeletee);
        btnSchedule = findViewById(R.id.btnSchedule);
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
        hobbyClass = findViewById(R.id.hobbyClass);
        hobbies = findViewById(R.id.hobbies);
        eventToSight = findViewById(R.id.eventToSight);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
    }

    private void setListener() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditable();
                btnDone.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNonEditable();
                btnDone.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
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
                        e = new Event(etEventName.getText().toString(), event.getEventId(), etHostRemark.getText().toString(), tvStartDate.getText().toString() + " 00:00", endDateString + " 00:00", etHobbyClass.getText().toString(), etHobbies.getText().toString(), latitude, longitude, isPublic.isChecked(), isOutDoor.isChecked());
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                } else {
                    e = new Event(etEventName.getText().toString(), event.getEventId(), etHostRemark.getText().toString(), tvStartDate.getText().toString() + " " + tvStartTime.getText().toString(), tvEndDate.getText().toString() + " " + tvEndTime.getText().toString(), etHobbyClass.getText().toString(), etHobbies.getText().toString(), latitude, longitude, isPublic.isChecked(), isOutDoor.isChecked());
                }
                if (checkValid(e)) {
                    Call<Ack> call = retrofitService.editEvent(e);
                    call.enqueue(new Callback<Ack>() {
                        @Override
                        public void onResponse(Call<Ack> call, Response<Ack> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(CheckAndEditActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
                            } else {
                                Ack ack = response.body();
                                if (ack.getCode() == 200) {
                                    Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CheckAndEditActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Ack> call, Throwable t) {
                            Toast.makeText(CheckAndEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> blackSet = new HashSet<>();
                Set<String> whiteSet = new HashSet<>();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CheckAndEditActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.schedule, null);
                dialogBuilder.setView(dialogView);
                TextView tvScheduleStartTime = dialogView.findViewById(R.id.tvScheduleStartTime);
                TextView tvScheduleEndTime = dialogView.findViewById(R.id.tvScheduleEndTime);
                Button btnAddList = dialogView.findViewById(R.id.btnAddList);
                ToggleButton blackWhiteToggle = dialogView.findViewById(R.id.blackWhiteToggle);
                ChipGroup whiteChipGroup = dialogView.findViewById(R.id.whiteChipGroup);
                ChipGroup blackChipGroup = dialogView.findViewById(R.id.blackChipGroup);
                TimePickerDialog timePickerDialog, timePickerDialog2;
                tvScheduleStartTime.setText(event.getStartTime().split(" ")[1]);
                tvScheduleEndTime.setText(event.getEndTime().split(" ")[1]);
                timePickerDialog = new TimePickerDialog(CheckAndEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String s = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        tvScheduleStartTime.setText(s);
                    }
                }, 12, 0, false);
                timePickerDialog2 = new TimePickerDialog(CheckAndEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String s = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        tvScheduleEndTime.setText(s);
                    }
                }, 12, 0, false);
                tvScheduleStartTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog.show();
                    }
                });
                tvScheduleEndTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog2.show();
                    }
                });
                btnAddList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = tvScheduleStartTime.getText().toString() + " 至 " + tvScheduleEndTime.getText().toString();
                        if (blackWhiteToggle.isChecked() && !whiteSet.contains(str)) {//白名單
                            whiteSet.add(str);
                            Chip chip = new Chip(CheckAndEditActivity.this);
                            chip.setText(str);
                            chip.setChipBackgroundColorResource(R.color.black);
                            chip.setCloseIconVisible(true);
                            chip.setTextColor(getResources().getColor(R.color.white));
                            chip.setTextAppearance(R.style.ChipTextAppearance);
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    whiteSet.remove(((Chip) v).getText());
                                    whiteChipGroup.removeView(v);
                                }
                            });
                            whiteChipGroup.addView(chip);
                        }
                        if (!blackWhiteToggle.isChecked() && !blackSet.contains(str)) {//黑名單
                            blackSet.add(str);
                            Chip chip = new Chip(CheckAndEditActivity.this);
                            chip.setText(str);
                            chip.setChipBackgroundColorResource(R.color.black);
                            chip.setCloseIconVisible(true);
                            chip.setTextColor(getResources().getColor(R.color.white));
                            chip.setTextAppearance(R.style.ChipTextAppearance);
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    blackSet.remove(((Chip) v).getText());
                                    blackChipGroup.removeView(v);
                                }
                            });
                            blackChipGroup.addView(chip);
                        }
                    }
                });
                dialogBuilder.setPositiveButton("開始排程", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> whiteList = new ArrayList<>(whiteSet);
                        ArrayList<String> blackList = new ArrayList<>(blackSet);
                        Call<List<String>> call = retrofitService.getRecommendTime(userId, event.getEventId(), whiteList, blackList);
                        call.enqueue(new Callback<List<String>>() {
                            @Override
                            public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<String>> call, Throwable t) {

                            }
                        });
                    }
                });
                dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
        btnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupWindow = new PopupWindow(CheckAndEditActivity.this);
                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setContentView(LayoutInflater.from(CheckAndEditActivity.this).inflate(R.layout.card_item, null));
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(btnTags, -100, 0);
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
                btnRemovePlace.setVisibility(View.INVISIBLE);
                tvPlaceDescribe.setText("");
                //todo:
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Ack> call = retrofitService.deleteEvent(event.getEventId());
                call.enqueue(new Callback<Ack>() {
                    @Override
                    public void onResponse(Call<Ack> call, Response<Ack> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(CheckAndEditActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
                        } else {
                            Ack ack = response.body();
                            if (ack.getCode() == 200) {
                                Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CheckAndEditActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Ack> call, Throwable t) {
                        Toast.makeText(CheckAndEditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                String s = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                tvEndDate.setText(s);
            }
        }, year, month, day);
        datePickerDialog2.getDatePicker().setMinDate(System.currentTimeMillis());
        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String s = hourOfDay + ":" + String.format("%02d", minute);
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

    private boolean checkValid(Event e) {
        if (etEventName.getText().toString().equals("") || staticHobbyTag.equals("")) {
            new SweetAlertDialog(CheckAndEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("活動名稱與類別不得為空")
                    .show();
            return false;
        }
        if (!Event.isTimeValid(e.getStartTime(), e.getEndTime())) {
            new SweetAlertDialog(CheckAndEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("時間錯誤")
                    .show();
            return false;
        }
        return true;
    }

    private void initField() {
        setNonEditable();
        etEventName.setText(event.getEventName());
        tvStartDate.setText(event.strSplit(event.getStartTime())[0]);
        tvStartTime.setText(event.strSplit(event.getStartTime())[1]);
        tvEndDate.setText(event.strSplit(event.getEndTime())[0]);
        tvEndTime.setText(event.strSplit(event.getEndTime())[1]);
        isOutDoor.setChecked(event.isOutDoor());
        isPublic.setChecked(event.isPublic());
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 15));
        etHostRemark.setText(event.getHostRemark());
        etHobbyClass.setText(event.getStaticHobbyClass(), false);
        etHobbies.setText(event.getStaticHobbyTag(), false);
        if (event.isAuth()) {
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
        tvEndDate.setEnabled(true);
        tvEndTime.setEnabled(true);
        btnAddPlace.setVisibility(View.VISIBLE);
        btnSchedule.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);

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
        tvEndDate.setEnabled(false);
        tvEndTime.setEnabled(false);
        btnAddPlace.setVisibility(View.INVISIBLE);
        btnRemovePlace.setVisibility(View.INVISIBLE);
        btnSchedule.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);

        etEventName.setTextColor(getResources().getColor(R.color.white));
        etHobbyClass.setTextColor(getResources().getColor(R.color.white));
        etHobbies.setTextColor(getResources().getColor(R.color.white));
        etHostRemark.setTextColor(getResources().getColor(R.color.white));
        isPublic.setTextColor(getResources().getColor(R.color.white));
        isOutDoor.setTextColor(getResources().getColor(R.color.white));
        tvStartDate.setTextColor(getResources().getColor(R.color.white));
        tvStartTime.setTextColor(getResources().getColor(R.color.white));
        tvEndDate.setTextColor(getResources().getColor(R.color.white));
        tvEndTime.setTextColor(getResources().getColor(R.color.white));
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
            Toast.makeText(CheckAndEditActivity.this, "fuck", Toast.LENGTH_SHORT).show();
        }
    }
}