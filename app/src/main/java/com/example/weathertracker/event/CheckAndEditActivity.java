package com.example.weathertracker.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.MainActivity;
import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.Event;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.example.weathertracker.retrofit.Sight;
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
import com.google.android.gms.maps.model.LatLng;
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
    private ImageButton btnEdit, btnBack, btnDone, btnAddPlace, btnSchedule, btnTags, btnDelete, btnParticipateEvent, btnOutEvent, btnLink, btnCalender;
    private TextView tvPlaceDescribe, tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private EditText etEventName, etHostRemark, etServerRemark;
    private SupportMapFragment mapFragment;
    private Double latitude, longitude;
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
    private List<Sight> sights;
    private LinearLayout timeLayout;
    private ArrayList<String> xLabels = new ArrayList<>();
    private LineChart lineChart;
    private chartList data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_and_edit);

        Intent intent = this.getIntent();
        String json = intent.getStringExtra("json");
        String where = intent.getStringExtra("where");

        System.out.println("Event:" + json);
        if (json == null) {
            Uri uri = getIntent().getData();
            String query = uri.getQuery(); //type=url&from=web
            System.out.println("eventId = " + query);
            where = "recommend";
        }

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
        callChart();
        if (where.equals("recommend")) {
            btnOutEvent.setVisibility(View.INVISIBLE);
            btnParticipateEvent.setVisibility(View.VISIBLE);
        } else {
            if (!event.isAuth()) {
                btnOutEvent.setVisibility(View.VISIBLE);
            } else {
                btnOutEvent.setVisibility(View.INVISIBLE);
            }
            btnLink.setVisibility(View.VISIBLE);
            btnParticipateEvent.setVisibility(View.INVISIBLE);
        }
    }

    private void getEventToSight() {
        Call<List<Sight>> call = retrofitService.getRecommendSights(event.getLongitude(), event.getLatitude());
        call.enqueue(new Callback<List<Sight>>() {
            @Override
            public void onResponse(Call<List<Sight>> call, Response<List<Sight>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                } else {
                    sights = response.body();
                    System.out.println(sights);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CheckAndEditActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    eventToSight.setLayoutManager(linearLayoutManager);
                    eventToSight.setAdapter(new EventToSightAdapter(CheckAndEditActivity.this, sights));

                }
            }

            @Override
            public void onFailure(Call<List<Sight>> call, Throwable t) {
                Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findId() {
        btnDone = findViewById(R.id.btnDone);
        btnEdit = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnAddPlace = findViewById(R.id.btnAddPlace);
        btnTags = findViewById(R.id.btnTags);
        btnDelete = findViewById(R.id.btnDelete);
        btnSchedule = findViewById(R.id.btnSchedule);
        btnParticipateEvent = findViewById(R.id.participateEvent);
        btnOutEvent = findViewById(R.id.outEvent);
        btnLink = findViewById(R.id.eventLink);
        etEventName = findViewById(R.id.etEventName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndTime = findViewById(R.id.tvEndTime);
        etHostRemark = findViewById(R.id.etHostRemark);
        etServerRemark = findViewById(R.id.serverRemark);
        tvPlaceDescribe = findViewById(R.id.tvPlaceDescribe);
        etHobbies = findViewById(R.id.etHobbies);
        etHobbyClass = findViewById(R.id.etHobbyClass);
        isOutDoor = findViewById(R.id.isOutDoor);
        isPublic = findViewById(R.id.isPublic);
        isAllDay = findViewById(R.id.isAllDay);
        hobbyClass = findViewById(R.id.hobbyClass);
        hobbies = findViewById(R.id.hobbies);
        eventToSight = findViewById(R.id.eventToSight);
        lineChart = findViewById(R.id.checkEventChart);
        timeLayout = findViewById(R.id.timeLayout);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        btnCalender = findViewById(R.id.goCalender);
    }

    private void setListener() {
        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCalender();
            }
        });
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("WeatherTracker", "http://weather_tracker/link?" + event.getEventId());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(CheckAndEditActivity.this, "分享連結已複製至剪貼簿", Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnParticipateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Ack> call = retrofitService.inOrOutEvent(event.getEventId(), userId, true);
                call.enqueue(new Callback<Ack>() {
                    @Override
                    public void onResponse(Call<Ack> call, Response<Ack> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                        } else {
                            Ack ack = response.body();
                            Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CheckAndEditActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Ack> call, Throwable t) {
                        Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnOutEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Ack> call = retrofitService.inOrOutEvent(event.getEventId(), userId, false);
                call.enqueue(new Callback<Ack>() {
                    @Override
                    public void onResponse(Call<Ack> call, Response<Ack> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                        } else {
                            Ack ack = response.body();
                            Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();//成功新增事件
                            Intent intent = new Intent(CheckAndEditActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Ack> call, Throwable t) {
                        Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(CheckAndEditActivity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("確定修改?");
                sweetAlertDialog.setConfirmButton("確定", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
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
                                        Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Ack ack = response.body();
                                        if (ack.getCode() == 200) {
                                            Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CheckAndEditActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(CheckAndEditActivity.this, "錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Ack> call, Throwable t) {
                                    Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                sweetAlertDialog.setCancelButton("取消", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        initField();
                        callChart();
                        setNonEditable();
                        mMap.clear();
                        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        sweetAlertDialog.cancel();
                    }
                });
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();

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
                TextView tvRecommendResult = dialogView.findViewById(R.id.recommendResult);
                Button btnAddList = dialogView.findViewById(R.id.btnAddList);
                Button btnStartSchedule = dialogView.findViewById(R.id.startSchedule);
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
                        String str = tvScheduleStartTime.getText().toString() + " " + tvScheduleEndTime.getText().toString();
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
                btnStartSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> whiteList = new ArrayList<>(whiteSet);
                        List<String> blackList = new ArrayList<>(blackSet);
                        Call<List<String>> call = retrofitService.getRecommendTime(userId, event.getEventId(), whiteList, blackList);
                        call.enqueue(new Callback<List<String>>() {
                            @Override
                            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                                } else {
                                    List<String> recommendTime = response.body();
                                    StringBuilder foo = new StringBuilder();
                                    foo.append("結果\n");
                                    for (int i = 0; i < recommendTime.size(); i += 2) {
                                        foo.append(recommendTime.get(i)).append(" ~ ").append(recommendTime.get(i + 1)).append("\n");
                                    }
                                    tvRecommendResult.setText(foo.toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<String>> call, Throwable t) {
                                Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });
        btnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(event.getDynamicTags());
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CheckAndEditActivity.this);
                View layoutView = getLayoutInflater().inflate(R.layout.tag_popup_layout, null);
                TextView tv = layoutView.findViewById(R.id.dTags);
                StringBuilder s = new StringBuilder();
                for (String tag : event.getDynamicTags()) {
                    s.append(tag).append("\n");
                }
                tv.setText(s);
                dialogBuilder.setView(layoutView);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
//                PopupWindow popupWindow = new PopupWindow(CheckAndEditActivity.this);
//                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                popupWindow.setContentView(LayoutInflater.from(CheckAndEditActivity.this).inflate(R.layout.tag_popup_layout, null));
//                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//                popupWindow.setOutsideTouchable(false);
//                popupWindow.setFocusable(true);
//                popupWindow.showAsDropDown(btnTags, -100, 0);
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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog pDialog = new SweetAlertDialog(CheckAndEditActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog.setTitleText("確定刪除?");
                pDialog.setContentText("此操作無法恢復");
                pDialog.setConfirmButton("確定", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Call<Ack> call = retrofitService.deleteEvent(event.getEventId());
                        call.enqueue(new Callback<Ack>() {
                            @Override
                            public void onResponse(Call<Ack> call, Response<Ack> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                                } else {
                                    Ack ack = response.body();
                                    if (ack.getCode() == 200) {
                                        Toast.makeText(CheckAndEditActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CheckAndEditActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(CheckAndEditActivity.this, "錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Ack> call, Throwable t) {
                                Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
                pDialog.setCancelButton("取消", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                });
                pDialog.setCancelable(false);
                pDialog.show();
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
        if (latitude == 0.0 && longitude == 0.0) {
            new SweetAlertDialog(CheckAndEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("請選擇地點")
                    .show();
            return false;
        }
        return true;
    }

    private void initField() {
        setNonEditable();
        mapFragment.getView().setVisibility(View.VISIBLE);
        etEventName.setText(event.getEventName());
        tvStartDate.setText(event.strSplit(event.getStartTime())[0]);
        tvStartTime.setText(event.strSplit(event.getStartTime())[1]);
        tvEndDate.setText(event.strSplit(event.getEndTime())[0]);
        tvEndTime.setText(event.strSplit(event.getEndTime())[1]);
        isOutDoor.setChecked(event.isOutDoor());
        isPublic.setChecked(event.isPublic());
        latitude = event.getLatitude();
        longitude = event.getLongitude();
        etHostRemark.setText(event.getHostRemark());
        etServerRemark.setText("系統備註:\n" + event.getSuggestions().getAll() + "\n" + event.getSuggestions().getHost() + "\n" + event.getSuggestions().getParticipant());
        etHobbyClass.setText(event.getStaticHobbyClass(), false);
        etHobbies.setText(event.getStaticHobbyTag(), false);
        try {
            if (event.isAuth()) {
                btnOutEvent.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            System.out.println("from recommend");
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
        isAllDay.setEnabled(true);
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
        etServerRemark.setEnabled(false);
        isPublic.setEnabled(false);
        isOutDoor.setEnabled(false);
        tvStartDate.setEnabled(false);
        tvStartTime.setEnabled(false);
        tvEndDate.setEnabled(false);
        tvEndTime.setEnabled(false);
        isAllDay.setEnabled(false);
        btnLink.setVisibility(View.INVISIBLE);
        btnDone.setVisibility(View.INVISIBLE);
        btnAddPlace.setVisibility(View.INVISIBLE);
        btnSchedule.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);
        btnOutEvent.setVisibility(View.INVISIBLE);

        etEventName.setTextColor(getResources().getColor(R.color.white));
        etHobbyClass.setTextColor(getResources().getColor(R.color.white));
        etHobbies.setTextColor(getResources().getColor(R.color.white));
        etHostRemark.setTextColor(getResources().getColor(R.color.white));
        etServerRemark.setTextColor(getResources().getColor(R.color.white));
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
        mMap.clear();
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            callChart();
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            tvPlaceDescribe.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Toast.makeText(CheckAndEditActivity.this, "fuck", Toast.LENGTH_SHORT).show();
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String strNow = sdf.format(now);
        Call<chartList> call = retrofitService.getChart(latitude.floatValue(), longitude.floatValue(), strNow.compareTo(tvStartDate.getText().toString()) > 0 ? strNow : tvStartDate.getText().toString());
        call.enqueue(new Callback<chartList>() {
            @Override
            public void onResponse(Call<chartList> call, Response<chartList> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 503) {
                        Toast.makeText(CheckAndEditActivity.this, "抱歉，非7日內資料暫時不可用，日後即將更新", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckAndEditActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    data = response.body();
                    makeChart("溫度");
                }
            }

            @Override
            public void onFailure(Call<chartList> call, Throwable t) {
//                Toast.makeText(CheckAndEditActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //todo:
    private void gotoCalender() {

        //strartend time
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        //beginTime
//        String[] stringArr= event.getStartTime().split("-");
        beginTime.set(2012, 9, 14, 24, 00);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        //endTime
        endTime.set(2012, 9, 14, 24, 00);
        endMillis = endTime.getTimeInMillis();

        //判斷有無空直
//        if(!title.getText().toString().isEmpty() && !location.getText().toString().isEmpty()
//                && !description.getText().toString().isEmpty()){


        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        //title
        intent.putExtra(CalendarContract.Events.TITLE, event.getEventName());
        //location
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLatitude());
        //description
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getHostRemark() + "\n" + event.getSuggestions().getAll());

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());

        intent.putExtra(CalendarContract.Events.ALL_DAY, false);

        //intent.putExtra(Intent.EXTRA_EMAIL, "rockandjeter@gmail.com,jacky410456@gmail.com");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(CheckAndEditActivity.this, "There is no app can support this action",
                    Toast.LENGTH_SHORT).show();
        }

//        }else {
//            Toast.makeText(CheckAndEditActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
//        }
    }


}