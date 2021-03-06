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

public class walkingActivity extends AppCompatActivity implements SensorEventListener {
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
//    private final String TMAP_API_KEY = "l7xx38321a7abbc144b28f1e1a6165ea3b58";
    private TMapView tmap;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    boolean start = false;
    Button startButton;
    private TextView tv_sensor;
    private TextView dista;
    private TextView kcalView;
    double totalDistance = 0;
    private TextView coinView;
    private int coin;
    long time1;
    int min;
    int steps = 0;
    int totalSteps = 0;
    private int sec;
    double accDistance = 0;
    double kcal;
    double air;
    calTime calTime;
    SharedPreferences sf;
    TMapPoint st_point;
    SensorManager sm;
    Sensor sensor_step_detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);
        setGps();

        if (ContextCompat.checkSelfPermission(walkingActivity.this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {//?????? ?????? ??????
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

        TMapGpsManager tmapgps = new TMapGpsManager(walkingActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();

        tmap.setTrackingMode(true);
        tmap.setSightVisible(true);

        //???????????? ?????? ?????? ???????????? ?????? ?????? ??????
        start = true;
        startButton = findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ?????? ?????????????????? ?????? true??? ????????? ?????? ??????
                Log.d( "???????????????????????????", "tqdasf");
                AlertDialog.Builder builder = new AlertDialog.Builder(walkingActivity.this);
                builder.setTitle("????????? ??????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences wsf =getSharedPreferences("walkinginfo", MODE_PRIVATE);
                        SharedPreferences.Editor wsfwalking = wsf.edit();
                        wsfwalking.putInt("steps", steps);
                        wsfwalking.putInt("totalSteps", totalSteps);
                        wsfwalking.putInt("kcal", (int) kcal);
                        wsfwalking.putInt("coin", coin);
                        wsfwalking.commit();
                        finish();
                    }
                });

                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });

        dista = findViewById(R.id.distance);
        kcalView = findViewById(R.id.kcal);
        Log.d("????????????", String.valueOf(totalDistance));

        coinView = findViewById(R.id.coin);
        coinView.setText("?????? ???\n" + coin);

        calTime = new calTime();
        calTime.start();


        tv_sensor = (TextView) findViewById(R.id.sensor);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_step_detector = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (sensor_step_detector == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final LocationListener mLocationListener = new LocationListener() {


        public void onLocationChanged(Location location) {

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmap.setCenterPoint(longitude, latitude);
                tmap.setLocationPoint(longitude, latitude);
                TMapData tmapdata = new TMapData();
                Intent intent = getIntent();
                if(st_point==null){
                   st_point = new TMapPoint(latitude, longitude);
                }
                TMapPoint endPoint = new TMapPoint(latitude, longitude);

                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, st_point, endPoint, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        polyLine.setLineColor(1);

                        //polyLine.setID("pedLine");
                        tmap.addTMapPolyLine("pedine",polyLine);
                        TMapInfo info = tmap.getDisplayTMapInfo(polyLine.getLinePoint());

                        int zoom = info.getTMapZoomLevel();
                        zoom = 20;
                        tmap.setZoomLevel(zoom);

                    }
                });

            }


        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    public void setGps() {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // ????????? ???????????????(???????????? NETWORK_PROVIDER ??????)
                100, // ??????????????? ?????? ???????????? (miliSecond)
                0, // ??????????????? ?????? ???????????? (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // ????????? ???????????????(???????????? NETWORK_PROVIDER ??????)
                100, // ??????????????? ?????? ???????????? (miliSecond)
                0, // ??????????????? ?????? ???????????? (m)
                mLocationListener);
    }

    private class calTime extends Thread {
        public calTime() {

        }

        @Override
        public void run() {
            min = (int) (time1 / 60 % 60);
            Log.d("?????? : ", String.valueOf(min));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sf = getSharedPreferences("info", MODE_PRIVATE);
        String weight = sf.getString("weight", "");
        String id = sf.getString("inputId", "");

            long end = System.currentTimeMillis();
            min = (int) ((end - time1) / 1000) / 60 % 60;
            sec = (int) ((end - time1) / 1000) % 60;
            // air= 3.5*?????????*??? kcal=air*5/1000
            //min = (int) (time1 / 60 % 60);

            Log.d("?????? : ", String.valueOf(min) + "???");
            Log.d("?????? : ", String.valueOf(sec) + "???");

            air = 3.5 * Integer.parseInt(weight) * min;
            kcal = air * 5 / 1000;//????????? 1000
            kcalView.setText("kcal\n" + (int) kcal);

            // ?????? ????????? ???????????? ????????? ?????? ????????? +1
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                tv_sensor.setText("?????? ???\n" + String.valueOf(totalSteps++));
                steps++;
                kcalView.setText("kcal\n" + (int) kcal);
                Log.d("?????? ??? ?????? ??? ", String.valueOf((int) totalSteps));
                totalDistance = steps * 0.7; //m
                accDistance = totalSteps * 0.7; //m
                Log.d("?????? ?????? ?????? ??? ", String.valueOf(steps));

                if (totalDistance >= 2000) {
                    coin = coin + 10;

                    totalDistance = totalDistance - 2000;
                    steps = 0;
                    dista.setText("????????????\n" + (int) accDistance + " M");


                    Log.d("?????? ??? ", String.valueOf(coin));
                    Log.d("??????????????? ", id);

                    coinView.setText("?????? ???\n" + String.valueOf(coin));
                    mDatabase = FirebaseDatabase.getInstance();
                    mReference = mDatabase.getReference("user"); // ???????????? ????????? child ??????
                    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        String db_coin;
                        String db_id;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                                db_coin = messageData.child("coin").getValue().toString();
                                db_id = messageData.child("userId").getValue().toString();
                                Log.d("????????? ??????", id);
                                if (id.equals(db_id)) {
                                    mReference = mDatabase.getReference().child("user"); // ???????????? ????????? ???????????? ?????? ?????????
                                    mReference.child(db_id).child("coin").setValue(String.valueOf(Integer.parseInt(db_coin) + 10))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    dista.setText("?????? ????????????\n" + (int) accDistance + " M");
                }
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences wsf =getSharedPreferences("walkinginfo", MODE_PRIVATE);
        SharedPreferences.Editor wsfwalking = wsf.edit();
        wsfwalking.putInt("steps", steps);
        wsfwalking.putInt("totalSteps", totalSteps);
        wsfwalking.putString("kcal", String.valueOf(kcal));
        wsfwalking.putInt("coin", coin);
        wsfwalking.commit();
        finish();

    }


}
