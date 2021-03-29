package com.example.weathertracker.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.weathertracker.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener , OnNavigationButtonClickedListener {
    private CustomCalendar customCalendar;
    private Calendar calendar;
    private Spinner spinner;
    private LineChart lineChart;
    private long startClickTime = 0;
    private int pickDate=0, date = 0, month = 0,year = 0,today=0,today_month=0,today_year=0;

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

        HashMap<Object, Property> descHashMap = new HashMap<>();
        Property defaultProperty = new Property();
        defaultProperty.layoutResource = R.layout.default_view;
        defaultProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("default",defaultProperty);
        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.current_view;
        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("current",currentProperty);
        Property absentProperty = new Property();
        absentProperty.layoutResource = R.layout.absent_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("absent",absentProperty);

        //todo:unavailable???
        Property disableProperty = new Property();
        disableProperty.layoutResource = R.layout.disable_view;
        disableProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("disabled", disableProperty);

        Property unavailableProperty = new Property();
        absentProperty.layoutResource = R.layout.disable_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("unavailable",unavailableProperty);

        customCalendar.setMapDescToProp(descHashMap);

        HashMap<Integer,Object> dateHashMap = new HashMap<>();
        calendar = Calendar.getInstance();

        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH),"current");
        today=calendar.get(Calendar.DAY_OF_MONTH);
        today_month=calendar.get(Calendar.MONTH)+1;
        today_year=calendar.get(Calendar.YEAR);
        date=today;
        month=today_month+1;
//        pickDate=today;
        spinner =root.findViewById(R.id.spinners_weatherDetail);
        try {
            getDropdownList(today,today_month,today_year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spinner.setOnItemSelectedListener(this);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                View[] month_days = customCalendar.getAllViews();

                Intent intent = new Intent(getActivity(), dateDetailActivity.class);
                date =selectedDate.get(Calendar.DATE);
                month =(selectedDate.get(Calendar.MONTH)+1);
                year =selectedDate.get(Calendar.YEAR);
                intent.putExtra("DATE",date);
                intent.putExtra("MONTH",month);

                if (SystemClock.uptimeMillis() - startClickTime < 300 && pickDate==date) {//判断两次点击时间差
                    startActivity(intent);
                } else {
                    startClickTime = SystemClock.uptimeMillis();
                    if(pickDate!=date) {
                        View temp_view = month_days[date-1];
                        temp_view.setBackgroundResource(R.drawable.date_pick);
                        if(pickDate!=0) {
                            temp_view = month_days[pickDate-1];
                            temp_view.setBackgroundResource(R.drawable.date_picknull);
                            //todo:
                            try {
                                getDropdownList(date,month,year);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        pickDate =date;
                    }
                    //dropdownlist

                }
            }
        });

        customCalendar.setDate(calendar, dateHashMap);

        lineChart = root.findViewById(R.id.lineChart);


        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS,(OnNavigationButtonClickedListener) this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, (OnNavigationButtonClickedListener) this);
        customCalendar.setDate(calendar, dateHashMap);





        return root;
    }




    private void getDropdownList(int i,int j,int h) throws ParseException {
        System.out.println("pickday "+ h+j+i + "today"+today_year+today_month+today);
        //        System.out.println(i+"+"+today+"+"+date);
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date beginDate= format.parse(h+"-"+j+"-"+i);
        Date endDate= format.parse(today_year+"-"+today_month+"-"+today);
        long day=(beginDate.getTime()-endDate.getTime())/(24*60*60*1000);
        System.out.println("相隔的天數="+day);
        if(day>=0&&day<=3) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_2, android.R.layout.simple_spinner_item);
        }
        if(day>2&&day<=7) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_7, android.R.layout.simple_spinner_item);
        }
        if(day<0) {
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_history, android.R.layout.simple_spinner_item);
        }
        spinner.setAdapter(adapter);
    }



    private List<Entry> lineChartDataSet() {
        ArrayList<Entry> dataSet = new ArrayList<Entry>();

        dataSet.add(new Entry(0,40));
        dataSet.add(new Entry(1,10));
        dataSet.add(new Entry(2,15));
        dataSet.add(new Entry(3,12));
        dataSet.add(new Entry(4,20));
        dataSet.add(new Entry(5,50));
        dataSet.add(new Entry(6,23));
        dataSet.add(new Entry(7,34));
        dataSet.add(new Entry(8,12));
        return  dataSet;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
        makeChart(date,month,text);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

        makeChart(today,today_month,"溫度");
    }



    public void makeChart(int date,int month,String s){
        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(),s);
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();

        lineChart.setNoDataText("Data not Available");

        //you can modify your line chart graph according to your requirement there are lots of method available in this library

        //now customize line chart
        Description description = lineChart.getDescription();
        description.setText(String.valueOf(month*100+date));//顯示文字名稱
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


    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];
        switch(newMonth.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                arr[0] = new HashMap<>();
                break;
            case Calendar.FEBRUARY:
                arr[0] = new HashMap<>(); //This is the map linking a date to its description
//                for(int i=1;i<=31;i++){
//                    if(a[2][i]==1)arr[0].put(i,"absent");
//                }
                for(int i=201;i<=231;i++){
                    String j =String.valueOf(i);
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
}