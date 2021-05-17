package com.example.weathertracker.account;

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
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private String selected, userId;
    private ChipGroup chipGroup;
    private ArrayAdapter<CharSequence> hobbyClassAdapter, hobbiesAdapter;
    private Set<String> chipSet = new HashSet<>();
    private AutoCompleteTextView etHobbies, etHobbyClass;
    private HashMap<String, Integer> idMap = new HashMap<>();
    private Slider weatherVsReasonable, reasonableVsKeepParticipants, keepParticipantsVsWeather;
    private FloatingActionButton fab;
    private TextInputEditText etUserName;
    private List<Double> AHPPreference;
    private List<Integer> freeTime = new ArrayList<>();//todo:確認型態
    private List<String> hobbies = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userId = getSharedPreferences("sharedPreferences", MODE_PRIVATE).getString("userId", "");
        idMap.put("戶外活動類", R.array.hobbies_outdoor_events);
        idMap.put("運動類", R.array.hobbies_sports);
        idMap.put("藝文嗜好類", R.array.hobbies_arts);
        idMap.put("益智類", R.array.hobbies_puzzle);
        idMap.put("視聽類", R.array.hobbies_audiovisual);
        idMap.put("休憩社交類", R.array.hobbies_social);
        idMap.put("其它類", R.array.hobbies_others);
        findId();
        setListener();
    }


    private void findId() {
        etUserName = findViewById(R.id.etUserName);
        scrollView = findViewById(R.id.scrollView);
        chipGroup = findViewById(R.id.chipGroup);
        etHobbies = findViewById(R.id.etHobbies);
        etHobbyClass = findViewById(R.id.etHobbyClass);
        weatherVsReasonable = findViewById(R.id.weatherVsReasonable);
        reasonableVsKeepParticipants = findViewById(R.id.reasonableVsKeepParticipants);
        keepParticipantsVsWeather = findViewById(R.id.keepParticipantsVsWeather);
        fab = findViewById(R.id.floatingActionButton);
    }

    private void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AHP_count() != null) {
                    RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                    hobbies = new ArrayList<>(chipSet);
                    Call<Ack> call = retrofitService.editProfile(userId, etUserName.getText().toString(), AHPPreference, freeTime, hobbies);
                    call.enqueue(new Callback<Ack>() {
                        @Override
                        public void onResponse(Call<Ack> call, Response<Ack> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
                            } else {
                                Ack ack = response.body();
                                if (ack.getCode() == 200) {
                                    Toast.makeText(ProfileActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();//去信箱收信
                                } else {
                                    Toast.makeText(ProfileActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Ack> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("排程偏好存在矛盾，請重新設定")
                            .show();
                }
            }
        });

        hobbyClassAdapter = ArrayAdapter.createFromResource(this, R.array.hobby_classes, R.layout.list_item);
        etHobbyClass.setAdapter(hobbyClassAdapter);
        etHobbyClass.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    hobbiesAdapter = ArrayAdapter.createFromResource(ProfileActivity.this, idMap.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
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
                    selected = s.toString();
                    if (!chipSet.contains(selected)) {
                        chipSet.add(selected);
                        Chip chip = new Chip(ProfileActivity.this);
                        chip.setText(selected);
                        chip.setChipBackgroundColorResource(R.color.black);
                        chip.setCloseIconVisible(true);
                        chip.setTextColor(getResources().getColor(R.color.white));
                        chip.setTextAppearance(R.style.ChipTextAppearance);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chipSet.remove(((Chip) v).getText());
                                chipGroup.removeView(v);
                            }
                        });
                        chipGroup.addView(chip);
                    } else {
                        new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
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

    private Boolean AHP_check(double eigenMax) {
        double CI = (eigenMax - 3) / 2;
        return (CI / 0.52 )< 0.1;
    }

    private List<Double> AHP_count() {
        float value_1 = weatherVsReasonable.getValue();
        float value_2 = reasonableVsKeepParticipants.getValue();
        float value_3 = keepParticipantsVsWeather.getValue();
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
            AHPPreference = new ArrayList<>();
            double[] vector = decomposition.getEigenvector(index).toArray();
            double sum = 0;
            for (double i : vector) {
                sum += Math.abs(i);
            }
            for (double i : vector) {
                AHPPreference.add(Math.abs(i) / sum);
            }
            Toast.makeText(ProfileActivity.this, AHPPreference.toString(), Toast.LENGTH_SHORT).show();
            return AHPPreference;
        } else {
            return null;
        }
    }
}