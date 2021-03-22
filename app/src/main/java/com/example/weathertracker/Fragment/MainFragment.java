package com.example.weathertracker.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    CustomCalendar customCalendar;
    private long startClickTime = 0;
    private int pickDate=0;
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        customCalendar = root.findViewById(R.id.custom_calender);




        HashMap<Object, Property> descHahMap = new HashMap<>();
        Property defaultProperty = new Property();
        defaultProperty.layoutResource = R.layout.default_view;
        defaultProperty.dateTextViewResource = R.id.text_view;
        descHahMap.put("default",defaultProperty);
        Property currentPorperty = new Property();
        currentPorperty.layoutResource = R.layout.current_view;
        currentPorperty.dateTextViewResource = R.id.text_view;
        descHahMap.put("current",currentPorperty);
        Property absentPorperty = new Property();
        absentPorperty.layoutResource = R.layout.absent_view;
        absentPorperty.dateTextViewResource = R.id.text_view;
        descHahMap.put("absent",absentPorperty);

        customCalendar.setMapDescToProp(descHahMap);

        HashMap<Integer,Object> dateHashMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH),"current");


        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                View[] month_days = customCalendar.getAllViews();
                int date = 0; int month = 0;
                Intent intent = new Intent(getActivity(), dateDetailActivity.class);
                date =selectedDate.get(Calendar.DATE);
                month =(selectedDate.get(Calendar.MONTH)+1);
                intent.putExtra("DATE",date);
                intent.putExtra("MONTH",month);

                if (SystemClock.uptimeMillis() - startClickTime < 300) {//判断两次点击时间差
                    Toast.makeText(getActivity(), "双击事件", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    startClickTime = SystemClock.uptimeMillis();
                    if(pickDate!=date) {
                        View temp_view = month_days[date-1];
                        temp_view.setBackgroundResource(R.drawable.date_pick);
                        if(pickDate!=0) {
                            temp_view = month_days[pickDate-1];
                            temp_view.setBackgroundResource(R.drawable.date_picknull);
                        }
                        pickDate =date;
                    }
                }
            }
        });

        customCalendar.setDate(calendar, dateHashMap);







        LineChart lineChart = root.findViewById(R.id.lineChart);

        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(),"");
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();

        lineChart.setNoDataText("Data not Available");

        //you can modify your line chart graph according to your requirement there are lots of method available in this library

        //now customize line chart
        Description description = lineChart.getDescription();
        description.setText("美元(USD)");//顯示文字名稱
        description.setTextSize(14);//字體大小
        description.setTextColor(Color.BLUE);//字體顏色
        description.setPosition(900, 80);



        //設定沒資料時顯示的內容
        lineChart.setNoDataText("暫時沒有數據");
        lineChart.setNoDataTextColor(Color.BLUE);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setCircleHoleRadius(2);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setValueTextColor(Color.BLACK);

        return root;
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

}