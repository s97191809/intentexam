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
                case R.id.tab4:
                    Toast.makeText(MainActivity.this, "네 번째 탭", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment4).commit();
                    return true;
            }
            return false;
        }

        //------------------------------------네비끝

        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        //TMapPoint tMapPoint1 = new TMapPoint(37.570841, 126.985302); // SKT타워

// 마커 아이콘

        final ArrayList alTMapPoint = new ArrayList();
        alTMapPoint.add(new TMapPoint(37.576016, 126.976867));//광화문
        alTMapPoint.add(new TMapPoint(37.570432, 126.992169));//종로3가
        alTMapPoint.add(new TMapPoint(37.570194, 126.983045));
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);

        for (int i = 0; i < alTMapPoint.size(); i++) {
            // 마커 아이콘 지정
            markerItem1.setIcon(bitmap);
            // 마커의 좌표 지정
            markerItem1.setTMapPoint((TMapPoint) alTMapPoint.get(i));
            //지도에 마커 추가
            tMapView.addMarkerItem("markerItem", markerItem1);

        }

    }
}