package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.opencsv.CSVReader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx5450926a109d4b33a7f3f0b5c89a2f0c");
        linearLayoutTmap.addView(tMapView);

        //InputStreamReader is = new InputStreamReader(getResources().openRawResource(R.raw.data));
        //BufferedReader reader = new BufferedReader(is);
        //CSVReader read = new CSVReader(reader);
        //String[] record = null;

       //------------------------------------네비시작
        mBottomNV = findViewById(R.id.nav_view);
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() { //NavigationItemSelecte
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                BottomNavigate(menuItem.getItemId());


                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.navigation_1);
    }
    private void BottomNavigate(int id) {  //BottomNavigation 페이지 변경
        String tag = String.valueOf(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (id == R.id.navigation_1) {
                fragment = new FragmentPage1();

            } else if (id == R.id.navigation_2){

                fragment = new FragmentPage2();
            }
            else if (id == R.id.navigation_3){

                fragment = new FragmentPage3();
            }else {
                fragment = new FragmentPage4();
            }

            fragmentTransaction.add(R.id.container, fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();


    }


            //------------------------------------네비 끝

// 마커 아이콘
}


