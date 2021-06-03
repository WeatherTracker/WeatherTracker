package com.example.weathertracker.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weathertracker.R;
import com.example.weathertracker.account.LoginActivity;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.example.weathertracker.retrofit.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private View view;
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
    private List<List<Boolean>> freeTime = new ArrayList<>();//todo:確認型態
    private List<String> hobbies = new ArrayList<>();
    private ArrayList<List<CheckBox>> checkBoxList = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile, container, false);
        userId = getContext().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE).getString("userId", "");
        idMap.put("戶外活動類", R.array.hobbies_outdoor_events);
        idMap.put("運動類", R.array.hobbies_sports);
        idMap.put("藝文嗜好類", R.array.hobbies_arts);
        idMap.put("益智類", R.array.hobbies_puzzle);
        idMap.put("視聽類", R.array.hobbies_audiovisual);
        idMap.put("休憩社交類", R.array.hobbies_social);
        idMap.put("其它類", R.array.hobbies_others);
        findId();
        setListener();
        initProfile();
        return view;
    }

    private void findId() {
        etUserName = view.findViewById(R.id.etUserName);
        scrollView = view.findViewById(R.id.scrollView);
        chipGroup = view.findViewById(R.id.chipGroup);
        etHobbies = view.findViewById(R.id.etHobbies);
        etHobbyClass = view.findViewById(R.id.etHobbyClass);
        weatherVsReasonable = view.findViewById(R.id.weatherVsReasonable);
        reasonableVsKeepParticipants = view.findViewById(R.id.reasonableVsKeepParticipants);
        keepParticipantsVsWeather = view.findViewById(R.id.keepParticipantsVsWeather);
        fab = view.findViewById(R.id.floatingActionButton);
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_11), view.findViewById(R.id.cb_12), view.findViewById(R.id.cb_13)));
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_21), view.findViewById(R.id.cb_22), view.findViewById(R.id.cb_23)));
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_31), view.findViewById(R.id.cb_32), view.findViewById(R.id.cb_33)));
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_41), view.findViewById(R.id.cb_42), view.findViewById(R.id.cb_43)));
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_51), view.findViewById(R.id.cb_52), view.findViewById(R.id.cb_53)));
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_61), view.findViewById(R.id.cb_62), view.findViewById(R.id.cb_63)));
        checkBoxList.add(Arrays.asList(view.findViewById(R.id.cb_71), view.findViewById(R.id.cb_72), view.findViewById(R.id.cb_73)));
    }

    private void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AHP_count();
                if (AHPPreference != null) {
                    RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                    hobbies = new ArrayList<>(chipSet);
                    List<Float> barValue = new ArrayList<>();
                    barValue.add(weatherVsReasonable.getValue());
                    barValue.add(reasonableVsKeepParticipants.getValue());
                    barValue.add(keepParticipantsVsWeather.getValue());
                    findFreeTime();
                    User u = new User(userId, etUserName.getText().toString(), hobbies, AHPPreference, barValue, freeTime);
                    Call<Ack> call = retrofitService.editProfile(u);
                    call.enqueue(new Callback<Ack>() {
                        @Override
                        public void onResponse(Call<Ack> call, Response<Ack> response) {
                            if (!response.isSuccessful()) {
                                if (response.code() == 401) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            } else {
                                Ack ack = response.body();
                                if (ack.getCode() == 200) {
                                    Toast.makeText(getActivity(), ack.getMsg(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Ack> call, Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("排程偏好存在矛盾，請重新設定")
                            .show();
                }
            }
        });

        hobbyClassAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.hobby_classes, R.layout.list_item);
        etHobbyClass.setAdapter(hobbyClassAdapter);
        etHobbyClass.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    hobbiesAdapter = ArrayAdapter.createFromResource(getActivity(), idMap.get(s.toString()), android.R.layout.simple_spinner_dropdown_item);
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
                        Chip chip = new Chip(getActivity());
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
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
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

    private void initProfile() {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<User> call = retrofitService.getProfile(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } else {
                    User user = response.body();
                    etUserName.setText(user.getUserName());
                    weatherVsReasonable.setValue(user.getBarValue().get(0));
                    reasonableVsKeepParticipants.setValue(user.getBarValue().get(1));
                    keepParticipantsVsWeather.setValue(user.getBarValue().get(2));
                    System.out.println(user.getFreeTime());
                    for (int i = 0; i < 7; i++) {
                        for (int j = 0; j < 3; j++) {
                            checkBoxList.get(i).get(j).setChecked(user.getFreeTime().get(i).get(j));
                        }
                    }
                    for (String s : user.getHobbies()) {
                        chipSet.add(s);
                        Chip chip = new Chip(getActivity());
                        chip.setText(s);
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
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void findFreeTime() {
        freeTime.clear();
        for (List i : checkBoxList) {
            ArrayList<Boolean> innerList = new ArrayList<>();
            for (Object c : i) {
                innerList.add(((CheckBox) c).isChecked());
            }
            freeTime.add(innerList);
        }
    }

    private Boolean AHP_check(double eigenMax) {
        double CI = (eigenMax - 3) / 2;
        return (CI / 0.52) < 0.1;
    }

    private void AHP_count() {
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
            Toast.makeText(getActivity(), AHPPreference.toString(), Toast.LENGTH_SHORT).show();
        } else {
            AHPPreference = null;
        }
    }

}
