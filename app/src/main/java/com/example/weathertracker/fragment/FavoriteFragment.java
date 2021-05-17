package com.example.weathertracker.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private int favoriteSize;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        RecyclerView rv_hor = root.findViewById(R.id.rv_hor);
        SharedPreferences preferences = getContext().getSharedPreferences("favorite", Context.MODE_PRIVATE);
        Map<String, ?> allPreferences = preferences.getAll();
        ArrayList<String> favorDate = new ArrayList<String>();
        ArrayList<String> favorDateData = new ArrayList<String>();
        favoriteSize=0;
        for (Object key : allPreferences.keySet()) {
            //System.out.println(key + " : " + allPreferences.get(key));
            favorDate.add((String) key);
            favorDateData.add((String)allPreferences.get(key));
            favoriteSize++;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_hor.setLayoutManager(linearLayoutManager);
        rv_hor.setAdapter(new favoriteAdapter(getActivity(),favorDate,favorDateData));
        //System.out.println(favoriteSize);
        return root;
    }


}
