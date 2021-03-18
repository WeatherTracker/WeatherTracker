package com.example.weathertracker.Account;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Register extends AppCompatActivity {
    private String selected;
    private ChipGroup chipGroup;
    private Spinner hobbies, hobby_class;
    private ArrayList<Integer> arrayList = new ArrayList(Arrays.asList(R.array.hobbies_empty,R.array.hobbies_outdoor_events, R.array.hobbies_sports, R.array.hobbies_arts, R.array.hobbies_puzzle, R.array.hobbies_audiovisual, R.array.hobbies_social, R.array.hobbies_others));
    private ArrayAdapter<CharSequence> hobby_class_adapter, hobbies_adapter;
    private Set<String> chip_set = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        chipGroup = findViewById(R.id.chip_group);
        hobby_class = findViewById(R.id.hobby_class);
        hobbies = findViewById(R.id.hobbies);
        hobby_class_adapter = ArrayAdapter.createFromResource(this, R.array.hobby_class, R.layout.list_item);
        hobby_class.setAdapter(hobby_class_adapter);
        hobby_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hobbies_adapter = ArrayAdapter.createFromResource(Register.this, arrayList.get(position), android.R.layout.simple_spinner_item);
                hobbies.setAdapter(hobbies_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hobbies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    selected = hobbies.getSelectedItem().toString();
                    if (!chip_set.contains(selected)) {
                        chip_set.add(selected);
                        Chip chip = new Chip(Register.this);
                        chip.setText(selected);
                        chip.setChipBackgroundColorResource(R.color.black);
                        chip.setCloseIconVisible(true);
                        chip.setTextColor(getResources().getColor(R.color.white));
                        chip.setTextAppearance(R.style.ChipTextAppearance);
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}