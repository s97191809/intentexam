package com.example.intentexam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapView;

public class naviActivity extends AppCompatActivity {
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    private TMapView tmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.tmap);

        tmap = new TMapView(this);

        tmap.setSKTMapApiKey(TMAP_API_KEY);
        linearLayoutTmap.addView(tmap);

        tmap.setIconVisibility(true);
        tmap.setTrackingMode(true);
        tmap.setSightVisible(true);
        TMapGpsManager gps = new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();



        // 폴리라인을 계쏙 그리면서 이동.
    }
}