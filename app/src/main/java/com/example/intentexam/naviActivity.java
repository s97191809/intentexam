package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class naviActivity extends AppCompatActivity {//공원, 상점 경로 안내 클래스
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    //private final String TMAP_API_KEY = "l7xx38321a7abbc144b28f1e1a6165ea3b58";

    private TMapView tmap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    boolean start = false;
    Button startButton;
    long time1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        setGps();

        if (ContextCompat.checkSelfPermission(naviActivity.this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {//센서 권한 요청
            //ask for permission

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }

        }
        time1 = System.currentTimeMillis();

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.tmap);


        tmap = new TMapView(this);

        tmap.setSKTMapApiKey(TMAP_API_KEY);
        linearLayoutTmap.addView(tmap);
        tmap.setCompassMode(true);
        tmap.setIconVisibility(true);

        TMapGpsManager tmapgps = new TMapGpsManager(naviActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();

        tmap.setTrackingMode(true);
        tmap.setSightVisible(true);

        
        TMapData tmapdata = new TMapData();
        Intent intent = getIntent();

        String naviName = intent.getStringExtra("naviName");
        Double edlt = intent.getDoubleExtra("eplt", 0);
        Double edln = intent.getDoubleExtra("epln", 0);

        TMapPoint endPoint = new TMapPoint(edlt, edln);
        location location = new location(this);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        TMapPoint st_point = new TMapPoint(lat, lon);
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, st_point, endPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {

                polyLine.setID("pedLine");
                tmap.addTMapPath(polyLine);
                TMapInfo info = tmap.getDisplayTMapInfo(polyLine.getLinePoint());

                int zoom = info.getTMapZoomLevel();
                if (zoom > 20) {
                    zoom = 20;
                }
                tmap.setZoomLevel(zoom);

            }
        });
        startButton = findViewById(R.id.startbutton);//시작버튼
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 안내 시작하겠다고 하면 true로 바꿔서 기록 시작

                AlertDialog.Builder builder = new AlertDialog.Builder(naviActivity.this);

                builder.setTitle("안내를 시작하시겠습니까?").setMessage("도착 장소 : " + naviName);
                builder.setPositiveButton("출발", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        tmap.setZoomLevel(20);
                        start = true;
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });




    }

    private final LocationListener mLocationListener = new LocationListener() {


        public void onLocationChanged(Location location) {

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmap.setCenterPoint(longitude, latitude);
                tmap.setLocationPoint(longitude, latitude);

            }


        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    public void setGps() {// gps 권한 요청
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                100, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                100, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }




}
