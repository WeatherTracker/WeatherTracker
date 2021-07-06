package com.example.weathertracker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.weathertracker.fragment.CopyrightFragment;
import com.example.weathertracker.fragment.MainFragment;
import com.example.weathertracker.fragment.ProfileFragment;
import com.example.weathertracker.fragment.RecommendFragment;
import com.google.android.material.snackbar.Snackbar;
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem;
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener {

    SNavigationDrawer sNavigationDrawer;
    Class aClass;
    public static Double latitude = 0.0, longitude = 0.0;
    private LocationManager locationManager;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        sNavigationDrawer = findViewById(R.id.nagivation_drawer);
        List<com.shrikanthravi.customnavigationdrawer2.data.MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Main", R.drawable.home));
        menuItems.add(new MenuItem("Profile", R.drawable.profile));
        menuItems.add(new MenuItem("Recommendation", R.drawable.search));
        menuItems.add(new MenuItem("Copyright", R.drawable.giwawa));
        sNavigationDrawer.setMenuItemList(menuItems);
        sNavigationDrawer.setAppbarTitleTV("Weather Tracker");

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
                    case 3:
                        aClass = CopyrightFragment.class;
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

    private void openFragment() {
        try {
            Fragment fragment = (Fragment) aClass.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in
                            , android.R.anim.fade_out)
                    .replace(R.id.frame_layout, fragment).commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        sharedPreferences.edit().putFloat("Longitude", (float) location.getLongitude()).apply();
        sharedPreferences.edit().putFloat("Latitude", (float) location.getLatitude()).apply();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

        } else {
            doubleBackToExitPressedOnce = true;
            Snackbar.make(findViewById(android.R.id.content), "再點擊一次返回鍵以退出", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}