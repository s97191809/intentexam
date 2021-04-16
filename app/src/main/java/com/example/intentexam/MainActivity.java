package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    fragment1 fragment1;
    fragment2 fragment2;
    fragment3 fragment3;

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
        fragment1 = new fragment1();
        fragment2 = new fragment2();
        fragment3 = new fragment3();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //바텀 네비게이션 버튼에 눌렀을때 화면 바뀌는 기능 추가
                //item.getItemId() 아이템의 아이디를 바로 가져옴
                switch (item.getItemId()) {
                    case R.id.tab1:
                        Toast.makeText(MainActivity.this, "첫 번째 탭", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                        return true; //return true 밑으로는 작동이 되지 않는다.

                    case R.id.tab2:
                        Toast.makeText(MainActivity.this, "두 번째 탭", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                        return true;

                    case R.id.tab3:
                        Toast.makeText(MainActivity.this, "세 번째 탭", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                        return true;
                }
                return false;
            }
        });
            //------------------------------------네비 끝

// 마커 아이콘
}
}

