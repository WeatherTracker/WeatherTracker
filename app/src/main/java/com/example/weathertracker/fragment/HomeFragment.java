package com.example.weathertracker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.MainActivity;
import com.example.weathertracker.R;
import com.example.weathertracker.account.LoginActivity;
import com.example.weathertracker.event.NewEventActivity;
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
import com.google.gson.Gson;

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
public class HomeFragment extends Fragment implements OnNavigationButtonClickedListener {
    private static CustomCalendar customCalendar;
    private Calendar calendar, lastPickedDate;
    //    private Spinner spinner;
    private LineChart lineChart;
    private int pickDate = 0, date = 0, month = 0, year = 0, today = 0, today_month = 0, today_year = 0;
    private String nowTime = null;
    private Boolean isOpen = false;
    private chartList data = null;
    private List<Event> event = null;
    private ArrayAdapter<CharSequence> adapter = null;
    private HashMap<Integer, Object> dateHashMap = new HashMap<>();
    private ArrayList<String> xLabels = new ArrayList<>();
    private AutoCompleteTextView etWeatherElement;
    private View root;
    private String userId;
    private List<String> icon;
    private int iconFlag = 7, monthFlag = 0;
    private TextView tvUserLocation;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        userId = getContext().getSharedPreferences("sharedPreferences", getContext().MODE_PRIVATE).getString("userId", "");
        System.out.println("userId in home: " + userId);
        findId();
        initCalendar();

        try {
            getDropdownList(today, today_month, today_year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setListener();
        String M = null;
        if (today_month < 10) M = "0" + today_month;
        else M = String.valueOf(today_month);
        System.out.println(M);
        getRedPoint(M, today_year);

        return root;
    }

    private void initCalendar() {
        calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_MONTH);
        today_month = calendar.get(Calendar.MONTH) + 1;
        today_year = calendar.get(Calendar.YEAR);
        nowTime = getNowTime();
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

        Property redPointProperty = new Property();
        redPointProperty.layoutResource = R.layout.redpoint_view;
        redPointProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("redPoint", redPointProperty);

        Property disableProperty = new Property();
        disableProperty.layoutResource = R.layout.disable_view;
        disableProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("disabled", disableProperty);

        customCalendar.setMapDescToProp(descHashMap);
        customCalendar.setDate(calendar, dateHashMap);

        getWeatherIcon(today_month, today_year);
    }

    //todo:
    private void getWeatherIcon(int flag, int year) {
        if (year == today_year) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
            float Longitude = sharedPreferences.getFloat("Longitude", 0);
            float Latitude = sharedPreferences.getFloat("Latitude", 0);
            RetrofitService retrofitService = RetrofitManager.getInstance().getService();
            Call<List<String>> call = retrofitService.getWeatherIcon(Latitude, Longitude);
            call.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getActivity(), "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    } else {
                        int iconTime = 7, iconDay = today - 1;
                        View[] month_days = customCalendar.getAllViews();
                        if ((month_days.length - today) < 8) {
                            iconTime = (month_days.length - today) + 1;
                            iconFlag = 7 - iconTime - 1;
                        }
                        icon = response.body();
                        if (flag == today_month) {
                            System.out.println("this month");
                            for (int i = 0; i < iconTime; i++) {
                                String uri = icon.get(i); //圖片路徑和名稱

                                int imageResource = getContext().getResources().getIdentifier(uri, "drawable", getContext().getPackageName());
                                //System.out.println("image+" + imageResource + "+" + uri);
                                View temp_view = month_days[iconDay];
//                            temp_view.findViewById(R.id.icon).setBackgroundResource(imageResource);
                                temp_view.findViewById(R.id.text_view).setBackgroundResource(imageResource);
                                temp_view.findViewById(R.id.cycle).bringToFront();
                                iconDay++;
                            }
                        } else if (flag == (today_month + 1)) {
                            int j = 7 - iconFlag;
                            System.out.println("Next month" + flag);
                            for (int i = 0; i < j; i++) {
                                String uri = icon.get(i); //圖片路徑和名稱
                                int imageResource = getContext().getResources().getIdentifier(uri, "drawable", getContext().getPackageName());
                                //System.out.println("image+" + imageResource + "+" + uri);
                                View temp_view = month_days[i];
                                temp_view.findViewById(R.id.icon).setBackgroundResource(imageResource);
                                j++;
                                iconDay++;
                            }
                            iconFlag = 7;

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Toast.makeText(getActivity(), "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setListener() {
        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {

                View[] month_days = customCalendar.getAllViews();
                date = selectedDate.get(Calendar.DATE);
                month = (selectedDate.get(Calendar.MONTH) + 1);
                year = selectedDate.get(Calendar.YEAR);
                System.out.println("my test " + date + " " + month + " " + year);
                if (pickDate != date) {
                    View temp_view = month_days[date - 1];
                    //todo:
//                    getRedPoint("04", selectedDate.getWeekYear());
                    temp_view.setBackgroundResource(R.drawable.date_pick);
                    if (pickDate != 0) {
                        temp_view = month_days[pickDate - 1];//有時候會out of index (30 of 30)
                        temp_view.setBackgroundResource(R.drawable.date_picknull);
                        //todo:
                        try {
                            String pickDay = year + "-" + month + "-" + date;
                            if (today_year == year && today_month == month && today == date)
                                getData(nowTime);
                            else getData(pickDay);
                            getDropdownList(date, month, year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    pickDate = date;
                }
                if (lastPickedDate != null) {
                    if (lastPickedDate.get(Calendar.DATE) == selectedDate.get(Calendar.DATE) &&
                            lastPickedDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                            lastPickedDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && !isOpen) {
                        isOpen = true;
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        View layoutView = getLayoutInflater().inflate(R.layout.pop_up_layout, null);
                        TextView tvDate = layoutView.findViewById(R.id.date);

                        ImageButton btnNewEvent = layoutView.findViewById(R.id.btnNewEvent);
                        btnNewEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date foo = lastPickedDate.getTime();
                                intent.putExtra("pickedDay", dateFormatter.format(foo));
                                startActivity(intent);
                            }
                        });
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
                        scaleAnimation.setDuration(500);
                        BounceInterpolator bounceInterpolator = new BounceInterpolator();
                        scaleAnimation.setInterpolator(bounceInterpolator);

                        String X = null, Y = null;
                        if (month < 10) X = "0" + (month);
                        else X = String.valueOf(month);
                        if (date < 10) Y = "0" + date;
                        else Y = String.valueOf(date);
                        String sharedPreferencesPickDay = String.valueOf(year) + "-" + X + "-" + Y;
                        RecyclerView rv_day = layoutView.findViewById(R.id.rv_day);
                        RecyclerView rv_day2 = layoutView.findViewById(R.id.rv_day2);
                        getCalenderDay(year, X, Y, rv_day, rv_day2);
                        tvDate.setText(sharedPreferencesPickDay);
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("favorite", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        String S = sharedPreferences.getString(sharedPreferencesPickDay, "0");

                        ToggleButton buttonFavorite = layoutView.findViewById(R.id.button_favorite);
                        if (S != "0") {
                            System.out.println("shared " + S);
                            buttonFavorite.setChecked(true);
                            buttonFavorite.startAnimation(scaleAnimation);
                        } else {
                            buttonFavorite.setChecked(false);
                        }
                        buttonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (!isChecked) {
                                    System.out.println("yes");
                                    String I = null, J = null;
                                    if (month < 10) I = "0" + (month);
                                    else I = String.valueOf(month);
                                    if (date < 10) J = "0" + date;
                                    else J = String.valueOf(date);
                                    String deleteDay = String.valueOf(year) + "-" + I + "-" + J;
                                    sharedPreferences.edit().remove(deleteDay).apply();
                                    buttonView.setChecked(false);
                                    Reload();
                                } else {
                                    System.out.println("no");
                                    buttonView.startAnimation(scaleAnimation);
                                    String I = null, J = null;
                                    if (month < 10) I = "0" + (month);
                                    else I = String.valueOf(month);
                                    if (date < 10) J = "0" + date;
                                    else J = String.valueOf(date);
                                    String favorateDay = String.valueOf(year) + "-" + I + "-" + J;
                                    System.out.println("favoer" + favorateDay);

                                    Gson gson = new Gson();
                                    String json = gson.toJson(data);
                                    //System.out.println(json);
                                    editor.putString(favorateDay, json);
                                    editor.apply();
                                    Reload();
                                }
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
                } else {
                    lastPickedDate = Calendar.getInstance();
                }
                lastPickedDate.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
                lastPickedDate.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
                lastPickedDate.set(Calendar.DATE, selectedDate.get(Calendar.DATE));
            }
        });
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        etWeatherElement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    makeChart(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findId() {
        customCalendar = root.findViewById(R.id.custom_calender);
        etWeatherElement = root.findViewById(R.id.etWeatherElement);
        lineChart = root.findViewById(R.id.lineChart);
        etWeatherElement = root.findViewById(R.id.etWeatherElement);
        tvUserLocation = root.findViewById(R.id.tvUserLocation);
    }

    private void Reload() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getDropdownList(int i, int j, int h) throws ParseException {
        String I = null, J = null;
        if (i < 10) I = "0" + (i);
        else I = String.valueOf(i);
        if (j < 10) J = "0" + j;
        else J = String.valueOf(j);
        //System.out.println("pickday " + h + j + i + "today" + today_year + today_month + today);
        String pickDay = h + "-" + J + "-" + I;
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = format.parse(h + "-" + j + "-" + i);
        Date endDate = format.parse(today_year + "-" + today_month + "-" + today);
        long day = (beginDate.getTime() - endDate.getTime()) / (24 * 60 * 60 * 1000);
        if (day >= 0 && day <= 3) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_2, android.R.layout.simple_spinner_item);
        }
        if (day > 2 && day <= 7) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_7, android.R.layout.simple_spinner_item);
        }
        if (day < 0) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_history, android.R.layout.simple_spinner_item);
        }
        etWeatherElement.setText("溫度", false);
        etWeatherElement.setAdapter(adapter);
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

//    @Override//drodownlistSelect
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String text = adapterView.getItemAtPosition(i).toString();
//        //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
//        makeChart(date, month, text);
//
//    }
//
//    @Override//預設
//    public void onNothingSelected(AdapterView<?> adapterView) {
//        makeChart(today, today_month, "溫度");
//    }

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

    //換月
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar Mcalendar) {
        Map<Integer, Object>[] arr = new Map[2];
        pickDate = 0;
        int month = 0;
        month = Mcalendar.get(Calendar.MONTH);
        int year = Mcalendar.get(Calendar.YEAR);
        System.out.println("month +" + month + whichButton);
        switch (month) {
            case Calendar.JANUARY:
                arr[0] = new HashMap<>();
                getRedPoint("01", Mcalendar.getWeekYear());
                getWeatherIcon(1, year);
//                if (today_month == 1) {
//                    arr[0].put(today, "current");
//                }
                break;
            case Calendar.FEBRUARY:
                arr[0] = new HashMap<>();
                getRedPoint("02", Mcalendar.getWeekYear());
                getWeatherIcon(2, year);
                break;
            case Calendar.MARCH:
                arr[0] = new HashMap<>();
                getRedPoint("03", Mcalendar.getWeekYear());
                getWeatherIcon(3, year);
                break;
            case Calendar.APRIL:
                arr[0] = new HashMap<>();
                getRedPoint("04", Mcalendar.getWeekYear());
                getWeatherIcon(4, year);
                break;
            case Calendar.MAY:
                getRedPoint("05", Mcalendar.getWeekYear());
                arr[0] = new HashMap<>();
                getWeatherIcon(5, year);
                break;
            case Calendar.JUNE:
                arr[0] = new HashMap<>();
                getRedPoint("06", Mcalendar.getWeekYear());
                getWeatherIcon(6, year);
                System.out.println("88888");
                break;
            case Calendar.JULY:
                arr[0] = new HashMap<>();
                getRedPoint("07", Mcalendar.getWeekYear());
                getWeatherIcon(7, year);
            case Calendar.AUGUST:
                arr[0] = new HashMap<>();
                getRedPoint("08", Mcalendar.getWeekYear());
                getWeatherIcon(8, year);
                break;
            case Calendar.SEPTEMBER:
                arr[0] = new HashMap<>();
                getRedPoint("09", Mcalendar.getWeekYear());
                getWeatherIcon(9, year);
                break;
            case Calendar.OCTOBER:
                arr[0] = new HashMap<>();
                getRedPoint("10", Mcalendar.getWeekYear());
                getWeatherIcon(10, year);
                break;
            case Calendar.NOVEMBER:
                arr[0] = new HashMap<>();
                getRedPoint("11", Mcalendar.getWeekYear());
                getWeatherIcon(11, year);
                break;
            case Calendar.DECEMBER:
                arr[0] = new HashMap<>();
                getRedPoint("12", Mcalendar.getWeekYear());
                getWeatherIcon(12, year);
                break;

        }

        return arr;
    }

    public String getNowTime() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return sdFormat.format(date);
    }

    private void getData(String pickDay) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        float Longitude = sharedPreferences.getFloat("Longitude", 0);
        float Latitude = sharedPreferences.getFloat("Latitude", 0);

        System.out.println("Longitude" + Longitude + "Latitude" + Latitude);

        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<chartList> call = retrofitService.getChart(Latitude, Longitude, pickDay);
        call.enqueue(new Callback<chartList>() {
            @Override
            public void onResponse(Call<chartList> call, Response<chartList> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 503) {
                        Toast.makeText(getActivity(), "抱歉，非7日內資料暫時不可用，日後即將更新", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    data = response.body();
                    makeChart(etWeatherElement.getEditableText().toString());
                    String s3;
                    if (data.getSiteName() == null) {
                        s3 = "";
                    } else {
                        s3 = data.getSiteName();
                    }
                    tvUserLocation.setText("目前位置: " + data.getCity() + data.getArea() + " 測站: " + s3);
//                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            String text = parent.getItemAtPosition(position).toString();
//                            //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
//                            makeChart(date, month, text);
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
                }
            }

            @Override
            public void onFailure(Call<chartList> call, Throwable t) {
//                Toast.makeText(getActivity(), "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCalenderDay(int year, String month, String day, RecyclerView rv_day, RecyclerView rv_day2) {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<List<Event>> call = retrofitService.getCalendarDay(userId, year + "-" + month + "-" + day);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        Toast.makeText(getActivity(), "未經授權，請重新登入", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    event = response.body();

                    if (event != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_day.setLayoutManager(linearLayoutManager);
                        rv_day.setAdapter(new calenderDayHostAdapter(getContext(), event));
//                        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
//                        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
//                        rv_day2.setLayoutManager(linearLayoutManager2);
//                        rv_day2.setAdapter(new calenderDayNoHostAdapter(getContext(), event));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getActivity(), "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getRedPoint(String month, int year) {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<List<String>> call = retrofitService.getCalendarMonth(userId, year + "-" + month);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        Toast.makeText(getActivity(), "未經授權，請重新登入", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    List<String> monthEvent = response.body();
                    View[] mday = customCalendar.getAllViews();
                    for (int i = 0; i < monthEvent.size(); i++) {
                        int da = Integer.parseInt(monthEvent.get(i).substring(8, 10));
                        View view = mday[da - 1];
                        ImageView mcycle = view.findViewById(R.id.cycle);
                        mcycle.setVisibility(View.VISIBLE);
                        //System.out.println(da);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getActivity(), "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });

    }


}