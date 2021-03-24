package com.example.weathertracker.Account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.example.weathertracker.Retrofit.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;


import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Profile extends AppCompatActivity {
    private ScrollView scrollView;
    private String selected;
    private ChipGroup chipGroup;
    private ArrayAdapter<CharSequence> hobby_class_adapter, hobbies_adapter;
    private Set<String> chip_set = new HashSet<>();
    private AutoCompleteTextView et_hobbies, et_hobby_class;
    private HashMap<String, Integer> id_map = new HashMap<>();
    private Slider weather_VS_reasonable, reasonable_VS_keepParticipants, keepParticipants_VS_weather;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        id_map.put("戶外活動類", R.array.hobbies_outdoor_events);
        id_map.put("運動類", R.array.hobbies_sports);
        id_map.put("藝文嗜好類", R.array.hobbies_arts);
        id_map.put("益智類", R.array.hobbies_puzzle);
        id_map.put("視聽類", R.array.hobbies_audiovisual);
        id_map.put("休憩社交類", R.array.hobbies_social);
        id_map.put("其它類", R.array.hobbies_others);
        find_id();
        set_listener();
    }


    private void find_id() {
        scrollView = findViewById(R.id.scroll_view);
        chipGroup = findViewById(R.id.chip_group);
        et_hobbies = findViewById(R.id.et_hobbies);
        et_hobby_class = findViewById(R.id.et_hobby_class);
        weather_VS_reasonable = findViewById(R.id.weather_VS_reasonable);
        reasonable_VS_keepParticipants = findViewById(R.id.reasonable_VS_keepParticipants);
        keepParticipants_VS_weather = findViewById(R.id.keepParticipants_VS_weather);
        fab = findViewById(R.id.floating_action_button);
    }

    private void set_listener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AHP_count() != null) {
//                    CALL API
                } else {
                    new SweetAlertDialog(Profile.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("排程偏好存在矛盾，請重新設定")
                            .show();
                }
            }
        });

        hobby_class_adapter = ArrayAdapter.createFromResource(this, R.array.hobby_classes, R.layout.list_item);
        et_hobby_class.setAdapter(hobby_class_adapter);
        et_hobby_class.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    hobbies_adapter = ArrayAdapter.createFromResource(Profile.this, id_map.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
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
                    selected = s.toString();
                    if (!chip_set.contains(selected)) {
                        chip_set.add(selected);
                        Chip chip = new Chip(Profile.this);
                        chip.setText(selected);
                        chip.setChipBackgroundColorResource(R.color.black);
                        chip.setCloseIconVisible(true);
                        chip.setTextColor(getResources().getColor(R.color.white));
                        chip.setTextAppearance(R.style.ChipTextAppearance);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chip_set.remove(((Chip) v).getText());
                                chipGroup.removeView(v);
                            }
                        });
                        chipGroup.addView(chip);
                    } else {
                        new SweetAlertDialog(Profile.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("看起來你很喜歡這個興趣呢!喜歡到選了兩次")
                                .show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY - scrollY < 0) {
                    fab.hide();
                }
                if (oldScrollY - scrollY > 0) {
                    fab.show();
                }
            }
        });
    }

    private Boolean AHP_check(double eigen_max) {
        double CI = (eigen_max - 3) / 2;
        return CI / 0.52 < 0.1;
    }

    private ArrayList<Double> AHP_count() {
        float value_1 = weather_VS_reasonable.getValue();
        float value_2 = reasonable_VS_keepParticipants.getValue();
        float value_3 = keepParticipants_VS_weather.getValue();
        int dimension = 3;
        double[][] testArray = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        if (value_1 == 0) {
            testArray[0][1] = 1;
            testArray[1][0] = 1;
        } else if (value_1 > 0) {
            testArray[0][1] = 1 / value_1;
            testArray[1][0] = value_1;
        } else {
            testArray[0][1] = -value_1;
            testArray[1][0] = 1 / -value_1;
        }
        if (value_2 == 0) {
            testArray[1][2] = 1;
            testArray[2][1] = 1;
        } else if (value_2 > 0) {
            testArray[1][2] = 1 / value_2;
            testArray[2][1] = value_2;
        } else {
            testArray[1][2] = -value_2;
            testArray[2][1] = 1 / -value_2;
        }
        if (value_3 == 0) {
            testArray[0][2] = 1;
            testArray[2][0] = 1;
        } else if (value_3 > 0) {
            testArray[0][2] = value_3;
            testArray[2][0] = 1 / value_3;
        } else {
            testArray[0][2] = 1 / -value_3;
            testArray[2][0] = -value_3;
        }
        for (double[] i : testArray) {
            for (double j : i) {
                System.out.println(j);
            }
        }
        RealMatrix testMatrix = MatrixUtils.createRealMatrix(testArray);
        EigenDecomposition decomposition = new EigenDecomposition(testMatrix);
        double max_eigen = Double.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < dimension; i++) {
            double real = decomposition.getRealEigenvalue(i);
            double image = decomposition.getImagEigenvalue(i);
            if (max_eigen < real + image) {
                max_eigen = real + image;
                index = i;
            }
        }
        if (AHP_check(max_eigen)) {
            double[] vector = decomposition.getEigenvector(index).toArray();
            double sum = 0;
            for (double i : vector) {
                sum += Math.abs(i);
            }
            ArrayList<Double> arrayList = new ArrayList<>();
            for (double i : vector) {
                arrayList.add(Math.abs(i) / sum);
            }
            Toast.makeText(Profile.this, arrayList.toString(), Toast.LENGTH_SHORT).show();
            return arrayList;
        } else {
            return null;
        }
    }
}