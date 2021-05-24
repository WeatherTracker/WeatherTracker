package com.example.weathertracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.weathertracker.fragment.MainFragment;
import com.example.weathertracker.fragment.ProfileFragment;
import com.example.weathertracker.fragment.RecommendFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem;
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    SNavigationDrawer sNavigationDrawer;
    Class aClass;
    public static Double latitude=0.0,longitude=0.0;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocatuon();


        setContentView(R.layout.activity_main);
        sNavigationDrawer = findViewById(R.id.nagivation_drawer);
        List<com.shrikanthravi.customnavigationdrawer2.data.MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Main", R.drawable.giwawa));
        menuItems.add(new MenuItem("Profile", R.drawable.giwawa));
        menuItems.add(new MenuItem("recommend", R.drawable.giwawa));
        sNavigationDrawer.setMenuItemList(menuItems);
        sNavigationDrawer.setAppbarTitleTV("main");

        aClass = MainFragment.class;
        openFragment();
        sNavigationDrawer.setOnMenuItemClickListener(new SNavigationDrawer.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(int position) {
                switch (position) {
                    case 0:
                        aClass = MainFragment.class;
                        break;
                    case 1:
                        aClass = ProfileFragment.class;
                        break;
                    case 2:
                        aClass = RecommendFragment.class;
                        break;
                }
            }
        });
        sNavigationDrawer.setDrawerListener(new SNavigationDrawer.DrawerListener() {
            @Override
            public void onDrawerOpening() {

            }

            @Override
            public void onDrawerClosing() {
                openFragment();
            }

            @Override
            public void onDrawerOpened() {

            }

            @Override
            public void onDrawerClosed() {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    public void getLocatuon() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    System.out.println(longitude+"+"+latitude);
                }
            }
        });
    }

    private void openFragment() {
        try{
            Fragment fragment = (Fragment)aClass.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in
                            ,android.R.anim.fade_out)
                    .replace(R.id.frame_layout,fragment).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }

}