package com.example.weathertracker.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.weathertracker.R;
import com.example.weathertracker.event.NewEventActivity;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.example.weathertracker.retrofit.chartList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnNavigationButtonClickedListener {
    private CustomCalendar customCalendar;
    private Calendar calendar, lastPickedDate;
    private Spinner spinner;
    private LineChart lineChart;
    private long startClickTime = 0;
    private int pickDate = 0, date = 0, month = 0, year = 0, today = 0, today_month = 0, today_year = 0;
    private String nowTime = null;
    private Boolean isOpen = false;

    ArrayAdapter<CharSequence> adapter = null;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        customCalendar = root.findViewById(R.id.custom_calender);
        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_MONTH);
        today_month = calendar.get(Calendar.MONTH) + 1;
        today_year = calendar.get(Calendar.YEAR);

        nowTime = getNowTime();
        System.out.println(nowTime);//yyyy/MM/dd hh:mm:ss
        getData(nowTime);

        HashMap<Object, Property> descHashMap = new HashMap<>();
        Property defaultProperty = new Property();
        defaultProperty.layoutResource = R.layout.default_view;
        defaultProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("default", defaultProperty);
        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("current", currentProperty);
        Property absentProperty = new Property();
        absentProperty.layoutResource = R.layout.absent_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("absent", absentProperty);

        //todo:unavailable???
        Property disableProperty = new Property();
        disableProperty.layoutResource = R.layout.disable_view;
        disableProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("disabled", disableProperty);

        Property unavailableProperty = new Property();
        absentProperty.layoutResource = R.layout.disable_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("unavailable", unavailableProperty);

        customCalendar.setMapDescToProp(descHashMap);

        HashMap<Integer, Object> dateHashMap = new HashMap<>();
        calendar = Calendar.getInstance();

        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH), "current");
        date = today;
        month = today_month + 1;
//        pickDate=today;
        spinner = root.findViewById(R.id.spinners_weatherDetail);
        try {
            getDropdownList(today, today_month, today_year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spinner.setOnItemSelectedListener(this);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                View[] month_days = customCalendar.getAllViews();
                date = selectedDate.get(Calendar.DATE);
                month = (selectedDate.get(Calendar.MONTH) + 1);
                year = selectedDate.get(Calendar.YEAR);
                if (pickDate != date) {
                    View temp_view = month_days[date - 1];
                    temp_view.setBackgroundResource(R.drawable.date_pick);
                    if (pickDate != 0) {
                        temp_view = month_days[pickDate - 1];
                        temp_view.setBackgroundResource(R.drawable.date_picknull);
                        //todo:
                        try {
                            getDropdownList(date, month, year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pickDate = date;
                }
                //dropdownlist
                if (lastPickedDate != null) {
                    if (lastPickedDate.get(Calendar.DATE) == selectedDate.get(Calendar.DATE) &&
                            lastPickedDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                            lastPickedDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && !isOpen) {
                        isOpen = true;
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        View layoutView = getLayoutInflater().inflate(R.layout.pop_up_layout, null);
                        ImageButton btnNewEvent  = layoutView.findViewById(R.id.btnNewEvent);
                        btnNewEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                                startActivity(intent);
                            }
                        });
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
                        scaleAnimation.setDuration(500);
                        BounceInterpolator bounceInterpolator = new BounceInterpolator();
                        scaleAnimation.setInterpolator(bounceInterpolator);
                        ToggleButton buttonFavorite = layoutView.findViewById(R.id.button_favorite);
                        buttonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                buttonView.startAnimation(scaleAnimation);
                            }
                        });
                        dialogBuilder.setView(layoutView);
                        AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                isOpen = false;
                            }
                        });
                        alertDialog.show();
                    }
                    lastPickedDate.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
                    lastPickedDate.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
                    lastPickedDate.set(Calendar.DATE, selectedDate.get(Calendar.DATE));
                } else {
                    lastPickedDate = Calendar.getInstance();
                    lastPickedDate.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
                    lastPickedDate.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
                    lastPickedDate.set(Calendar.DATE, selectedDate.get(Calendar.DATE));
                }
            }
        });
        customCalendar.setDate(calendar, dateHashMap);
        lineChart = root.findViewById(R.id.lineChart);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, (OnNavigationButtonClickedListener) this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, (OnNavigationButtonClickedListener) this);
        customCalendar.setDate(calendar, dateHashMap);
        return root;
    }

    private void getDropdownList(int i, int j, int h) throws ParseException {
        String I = null, J = null;
        if (i < 10) I = "0" + i;
        else I = String.valueOf(i);
        if (j < 10) J = "0" + j;
        else J = String.valueOf(j);
        System.out.println("pickday " + h + j + i + "today" + today_year + today_month + today);
        String pickDay = h + "/" + J + "/" + I + " 00:00:00";
        System.out.println(pickDay);
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = format.parse(h + "-" + j + "-" + i);
        Date endDate = format.parse(today_year + "-" + today_month + "-" + today);
        long day = (beginDate.getTime() - endDate.getTime()) / (24 * 60 * 60 * 1000);
        System.out.println("相隔的天數=" + day);
        getData(pickDay);
        if (day >= 0 && day <= 3) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_2, android.R.layout.simple_spinner_item);
        }
        if (day > 2 && day <= 7) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_7, android.R.layout.simple_spinner_item);
        }
        if (day < 0) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_history, android.R.layout.simple_spinner_item);
        }
        spinner.setAdapter(adapter);
    }


    private List<Entry> lineChartDataSet() {
        ArrayList<Entry> dataSet = new ArrayList<Entry>();

        dataSet.add(new Entry(0, 40));
        dataSet.add(new Entry(1, 10));
        dataSet.add(new Entry(2, 15));
        dataSet.add(new Entry(3, 12));
        dataSet.add(new Entry(4, 20));
        dataSet.add(new Entry(5, 50));
        dataSet.add(new Entry(6, 23));
        dataSet.add(new Entry(7, 34));
        dataSet.add(new Entry(8, 12));
        return dataSet;
    }

    @Override//drodownlistSelect
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
        makeChart(date, month, text);

    }

    @Override//預設
    public void onNothingSelected(AdapterView<?> adapterView) {
        makeChart(today, today_month, "溫度");
    }


    //折線圖
    public void makeChart(int date, int month, String s) {
        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(), s);
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();

        lineChart.setNoDataText("Data not Available");

        //you can modify your line chart graph according to your requirement there are lots of method available in this library

        //now customize line chart
        Description description = lineChart.getDescription();
        description.setText(String.valueOf(month * 100 + date));//顯示文字名稱
        description.setTextSize(14);//字體大小
        description.setTextColor(Color.BLUE);//字體顏色
        description.setPosition(900, 100);

        //設定沒資料時顯示的內容
        lineChart.setNoDataText("暫時沒有數據");
        lineChart.setNoDataTextColor(Color.BLUE);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setCircleHoleRadius(2);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setValueTextColor(Color.BLACK);
    }

    //換月
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];
        switch (newMonth.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                arr[0] = new HashMap<>();
                break;
            case Calendar.FEBRUARY:
                arr[0] = new HashMap<>(); //This is the map linking a date to its description
//                for(int i=1;i<=31;i++){
//                    if(a[2][i]==1)arr[0].put(i,"absent");
//                }
                for (int i = 201; i <= 231; i++) {
                    String j = String.valueOf(i);
                }

                arr[1] = null; //Optional: This is the map linking a date to its tag.
                break;
            case Calendar.MARCH:
                arr[0] = new HashMap<>();
                break;
            case Calendar.APRIL:
                arr[0] = new HashMap<>();
                break;
            case Calendar.MAY:
                arr[0] = new HashMap<>();
                break;
            case Calendar.JUNE:

                arr[0] = new HashMap<>();
                arr[0].put(5, "presnet");
                arr[0].put(10, "presnet");
                arr[0].put(19, "presnet");
                break;

            case Calendar.JULY:
                arr[0] = new HashMap<>();
                break;
            case Calendar.AUGUST:
                arr[0] = new HashMap<>();
                break;
            case Calendar.SEPTEMBER:
                arr[0] = new HashMap<>();
                break;
            case Calendar.OCTOBER:
                arr[0] = new HashMap<>();
                break;
            case Calendar.NOVEMBER:
                arr[0] = new HashMap<>();
                break;
            case Calendar.DECEMBER:
                arr[0] = new HashMap<>();
                break;

        }
        return arr;
    }

    public String getNowTime() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = new Date();
        String strDate = sdFormat.format(date);
        return strDate;
    }

    private void getData(String pickDay) {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<chartList> call = retrofitService.getChart(120.716073, 22.074033, "2021-04-03%2023:59:59.628556");
        call.enqueue(new Callback<chartList>() {
            @Override
            public void onResponse(Call<chartList> call, Response<chartList> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "server沒啦", Toast.LENGTH_SHORT).show();
                } else {
                    chartList data = response.body();
                }
            }

            @Override
            public void onFailure(Call<chartList> call, Throwable t) {

            }
        });

    }
}