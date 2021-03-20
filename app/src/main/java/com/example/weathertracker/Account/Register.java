package com.example.weathertracker.Account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Register extends AppCompatActivity {
    private String selected;
    private ChipGroup chipGroup;
    private ArrayAdapter<CharSequence> hobby_class_adapter, hobbies_adapter;
    private Set<String> chip_set = new HashSet<>();
    private AutoCompleteTextView et_hobbies, et_hobby_class;
    private HashMap<String, Integer> id_map = new HashMap<>();
    private Slider weather_VS_reasonable, reasonable_VS_keepParticipants, keepParticipants_VS_weather;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        chipGroup = findViewById(R.id.chip_group);
        et_hobbies = findViewById(R.id.et_hobbies);
        et_hobby_class = findViewById(R.id.et_hobby_class);
        weather_VS_reasonable = findViewById(R.id.weather_VS_reasonable);
        reasonable_VS_keepParticipants = findViewById(R.id.reasonable_VS_keepParticipants);
        keepParticipants_VS_weather = findViewById(R.id.keepParticipants_VS_weather);
        btn = findViewById(R.id.button);
    }

    private void set_listener() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                RealMatrix testMatrix = MatrixUtils.createRealMatrix(testArray);
                EigenDecomposition decomposition = new EigenDecomposition(testMatrix);
                double max_index = Double.MIN_VALUE;
                int index = 0;
                for (int i = 0; i < dimension; i++) {
                    double real = decomposition.getRealEigenvalue(i);
                    double image = decomposition.getImagEigenvalue(i);
                    if (max_index < real + image) {
                        max_index = real + image;
                        index = i;
                    }
                }
                double[] vector = decomposition.getEigenvector(index).toArray();
                double sum = 0;
                for (double i : vector) {
                    sum += i;
                }
                for (double i : vector) {
                    Toast.makeText(Register.this, "" + (i / sum), Toast.LENGTH_SHORT).show();
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
                    hobbies_adapter = ArrayAdapter.createFromResource(Register.this, id_map.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
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
                        Chip chip = new Chip(Register.this);
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
                        new SweetAlertDialog(Register.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("看起來你很喜歡這個興趣呢!\n喜歡到選了兩次")
                                .show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}