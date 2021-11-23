package com.example.intentexam;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapAddressInfo;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogManager;

import javax.xml.parsers.ParserConfigurationException;

import static android.app.Activity.RESULT_OK;
import static java.lang.Thread.sleep;


public class FragmentPage2 extends Fragment implements SensorEventListener {// 지도 화면 클래스
    Button button3;
    Button button4;
    Button button5;
    Button startbutton;
    Button trailButton;
    Button goodboard;
    TextView tv_sensor;
    TextView dista;
    TextView coinView;
    TextView kcalView;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
        final ArrayList<String> arrParkName = new ArrayList<>();
    final ArrayList<Double> arrParkLat = new ArrayList<>();
    final ArrayList<Double> arrParkLon = new ArrayList<>();
    final ArrayList<String> arrParkAddr = new ArrayList<>();
    final ArrayList<String> arrHospitalName = new ArrayList<>();
    final ArrayList<Double> arrHospitalLat = new ArrayList<>();
    final ArrayList<Double> arrHospitalLon = new ArrayList<>();
    final ArrayList<String> arrHospitalAddr = new ArrayList<>();
    final ArrayList<String> arrShopName = new ArrayList<>();
    final ArrayList<Double> arrShopLat = new ArrayList<>();
    final ArrayList<Double> arrShopLon = new ArrayList<>();
    final ArrayList<String> arrShopAddr = new ArrayList<>();
    final ArrayList<TMapPoint> arrTMapPointPark = new ArrayList<>();
    final ArrayList<TMapPoint> arrTMapPointHospital = new ArrayList<>();
    final ArrayList<TMapPoint> arrTMapPointShop = new ArrayList<>();
    final ArrayList<String> arrAllname = new ArrayList<>();
    final ArrayList<String> arrTempAllname = new ArrayList<>();
    final ArrayList<TMapPoint> distTmapParkPoint = new ArrayList<>();
    final ArrayList<String> distTmapParkName = new ArrayList<>();
    final ArrayList<String> distTmapParkAddr = new ArrayList<>();

    final ArrayList<TMapPoint> distTmapHospitalPoint = new ArrayList<>();
    final ArrayList<String> distTmapHospitalName = new ArrayList<>();
    final ArrayList<String> distTmapHospitalAddr = new ArrayList<>();

    final ArrayList<TMapPoint> distTmapShopPoint = new ArrayList<>();
    final ArrayList<String> distTmapShopName = new ArrayList<>();
    final ArrayList<String> distTmapShopAddr = new ArrayList<>();

    final ArrayList<TMapPoint> boardTopPoint = new ArrayList<>();
    final ArrayList<String> boardTopName = new ArrayList<>();
    final ArrayList<String> boardTopgPoint = new ArrayList<>();
    final ArrayList<String> boardTopAddr = new ArrayList<>();
    final ArrayList<Double> boardToplat = new ArrayList<>();
    final ArrayList<Double> boardToplon = new ArrayList<>();

        final ArrayList<TMapPoint> passList = new ArrayList<>();


    private TMapGpsManager tmapgps = null;
    private TMapPoint point = null;
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    //private final String TMAP_API_KEY = "l7xx3d93d0f710544a94a72def7ba2555d16";
    private TMapView tmap;
    private ballonEvent ballonEventThread;
    private dbLoad dbLoad;
    private removeMarker removeMarker;

    int coin = 0;
    int count = 0;

    double totalDistance = 0;
    double accDistance = 0;
    int steps = 0;
    int totalSteps = 0;
    int min;
    long time1;
    boolean start = false;
    double air;
    double kcal;

    SensorManager sm;
    Sensor sensor_step_detector;
    SharedPreferences sf;
    loadBoard loadBoard;


    calTime calTime;
    static final int REQ_ADD_CONTACT = 1;
    private int sec;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGps();
        time1 = System.currentTimeMillis();
        try {// 산책로 정보 수신
            SharedPreferences prefs = getContext().getSharedPreferences("trailInfo", getContext().MODE_PRIVATE);
            int size = prefs.getInt("Status_size", 0);
            String name = sf.getString("name", "");
            String start = sf.getString("start", "");
            String end = sf.getString("end", "");
            Log.d("이름, 시작, 끝", name + ", " + start + ", " + end);


        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //XML, java 연결
        //XML이 메인에 직접 붙으면 true, 프래그먼트에 붙으면 false

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {//센서 권한 요청
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }


        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_2, container, false);
        LinearLayout linearLayoutTmap = (LinearLayout) v.findViewById(R.id.tmap);
        tmap = new TMapView(getActivity());
        tmap.setSKTMapApiKey(TMAP_API_KEY);
        linearLayoutTmap.addView(tmap);//TMap 지도 화면 설정

        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시
        tmap.setTrackingMode(true);
        tmap.setSightVisible(true);

        dista = v.findViewById(R.id.distance);
        kcalView = v.findViewById(R.id.kcal);
        Log.d("이동거리", String.valueOf(totalDistance));
        location lc = new location(getActivity());
        startbutton = v.findViewById(R.id.startbutton);//산책 시작 버튼
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeMarker.isAlive()) {

                    removeMarker.interrupt();

                }
                removeMarker = new removeMarker();
                removeMarker.start();
                count=0;
                Toast.makeText(getContext(), "산책을 시작합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), walkingActivity.class);
                startActivityForResult(intent, 2);
                start = true;
            }
        });

        coinView = v.findViewById(R.id.coin);
        coinView.setText("코인 수\n" + coin);

        
        calTime = new calTime();//실행 시간 측정
        calTime.start();

        ballonEventThread = new ballonEvent();// 풍선 뷰 이벤트 처리 쓰레드
        ballonEventThread.start();
        mDatabase = FirebaseDatabase.getInstance();
        dbLoad = new dbLoad();//공원, 병원, 상점 데이터베이스 로드
        dbLoad.start();

        loadBoard = new loadBoard();//게시글 정보 로드
        loadBoard.start();

        tv_sensor = (TextView) v.findViewById(R.id.sensor);
        sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);//센서 매니저 호출
        sensor_step_detector = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (sensor_step_detector == null) {//센서가 NULL이면 메시지 출력
            Toast.makeText(getContext(), "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }
        goodboard = v.findViewById(R.id.goodboard);//좋아요 수 20개 이상의 게시물 위치 표시
        goodboard.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (removeMarker.isAlive()) {

                    removeMarker.interrupt();

                }
                removeMarker = new removeMarker();
                removeMarker.start();
                TMapData tMapData = new TMapData();

                makeMarkerBoard(boardTopPoint, boardTopName, boardTopAddr);

            }
        });
        removeMarker = new removeMarker();
        button3 = v.findViewById(R.id.button3);// 공원 위치 표시
        button3.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                if (removeMarker.isAlive()) {

                    removeMarker.interrupt();

                }
                removeMarker = new removeMarker();
                removeMarker.start();
                makeMarkerPark(arrTMapPointPark, arrParkName, arrParkAddr);
            }

        });

        trailButton = v.findViewById(R.id.trailButton);//산책로 리스트 버튼
        trailButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), trailActivity.class);
                startActivityForResult(intent, REQ_ADD_CONTACT);
            }

        });


        button4 = v.findViewById(R.id.button4);// 병원 위치 표시
        button4.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View v) {
                if (removeMarker.isAlive()) {

                    removeMarker.interrupt();

                }
                removeMarker = new removeMarker();
                removeMarker.start();
                makeMarkerHospital(arrTMapPointHospital, arrHospitalName, arrHospitalAddr);

            }


        });

        button5 = v.findViewById(R.id.button5);// 관련 상점 위치 표시
        button5.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View v) {
                if (removeMarker.isAlive()) {

                    removeMarker.interrupt();

                }
                removeMarker = new removeMarker();
                removeMarker.start();
                makeMarkerShop(arrTMapPointShop, arrShopName, arrShopAddr);

            }


        });
        

        Spinner spiner = (Spinner) v.findViewById(R.id.select_distance);//스피너 설정 
        ArrayAdapter sAdapter = ArrayAdapter.createFromResource(getContext(), R.array.question, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(sAdapter);
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectDist = (String) spiner.getSelectedItem();


                if (selectDist.equals("거리 선택")) {
                    // 모든 마커 삭제
                    if (removeMarker.isAlive()) {

                        removeMarker.interrupt();

                    }
                    removeMarker = new removeMarker();
                    removeMarker.start();
                    // 배열 초기화
                    distTmapParkPoint.clear();
                    distTmapParkName.clear();
                    distTmapParkAddr.clear();
                    distTmapHospitalPoint.clear();
                    distTmapHospitalName.clear();
                    distTmapHospitalName.clear();
                    distTmapShopPoint.clear();
                    distTmapShopName.clear();
                    distTmapShopAddr.clear();

                    // 마커와 원 삭제
                    tmap.removeAllMarkerItem();
                    tmap.removeAllTMapCircle();
                } else if (selectDist.equals("1km (약 15분)")) {// 1km 선택시
                    // 모든 마커 삭제
                    if (removeMarker.isAlive()) {

                        removeMarker.interrupt();

                    }
                    removeMarker = new removeMarker();
                    removeMarker.start();
                    
                    distTmapParkPoint.clear();
                    tmap.removeAllMarkerItem();
                    location location = new location(getContext());
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    point = new TMapPoint(lat, lon);
                    TMapCircle tMapCircle = new TMapCircle();//원 그리기
                    tMapCircle.setCenterPoint(point);
                    tMapCircle.setRadius(500);
                    tMapCircle.setCircleWidth(2);
                    tMapCircle.setLineColor(Color.BLUE);
                    tMapCircle.setAreaColor(Color.GRAY);
                    tMapCircle.setAreaAlpha(100);
                    tmap.addTMapCircle("circle1", tMapCircle);
                    Location location2 = new Location("");//현재위치
                    location2.setLatitude(lat);
                    location2.setLongitude(lon);
                    
                    for (int i = 0; i < arrTMapPointPark.size(); i++) {//공원
                        Location parkDestination1Km = new Location("");
                        parkDestination1Km.setLatitude(arrParkLat.get(i));
                        parkDestination1Km.setLongitude(arrParkLon.get(i));

                        double distance = location2.distanceTo(parkDestination1Km);

                        // 범위에 따른 위치 선별
                        if (distance <= 500) {// 단위 (M)
                            TMapPoint dtpoint = new TMapPoint(parkDestination1Km.getLatitude(), parkDestination1Km.getLongitude());
                            String dtname = arrParkName.get(i);
                            String dtaddr = arrParkAddr.get(i);
                            distTmapParkName.add(dtname);
                            distTmapParkAddr.add(dtaddr);
                            distTmapParkPoint.add(dtpoint);
                        }
                    }
                    for (int j = 0; j < arrTMapPointHospital.size(); j++) {//병원
                        Location hospitalDestination1Km = new Location("");
                        hospitalDestination1Km.setLatitude(arrHospitalLat.get(j));
                        hospitalDestination1Km.setLongitude(arrHospitalLon.get(j));

                        double distance = location2.distanceTo(hospitalDestination1Km);
                        // 범위에 따른 위치 선별
                        if (distance <= 500) {
                            TMapPoint dtpoint = new TMapPoint(hospitalDestination1Km.getLatitude(), hospitalDestination1Km.getLongitude());
                            String dtname = arrHospitalName.get(j);
                            String dtaddr = arrHospitalAddr.get(j);
                            distTmapHospitalName.add(dtname);
                            distTmapHospitalAddr.add(dtaddr);
                            distTmapHospitalPoint.add(dtpoint);

                        }
                    }
                    for (int k = 0; k < arrTMapPointShop.size(); k++) {//관련 상점
                        Location shopDestination1Km = new Location("");
                        shopDestination1Km.setLatitude(arrShopLat.get(k));
                        shopDestination1Km.setLongitude(arrShopLon.get(k));

                        double distance = location2.distanceTo(shopDestination1Km);
                        // 범위에 따른 위치 선별
                        if (distance <= 500) {
                            TMapPoint dtpoint = new TMapPoint(shopDestination1Km.getLatitude(), shopDestination1Km.getLongitude());
                            String dtname = arrHospitalName.get(k);
                            String dtaddr = arrHospitalAddr.get(k);
                            distTmapShopPoint.add(dtpoint);
                            distTmapShopName.add(dtname);
                            distTmapShopAddr.add(dtaddr);
                        }
                    }
                    makeMarkerShop(distTmapShopPoint, distTmapShopName, distTmapShopAddr);
                    makeMarkerHospital(distTmapHospitalPoint, distTmapHospitalName, distTmapHospitalAddr);
                    makeMarkerPark(distTmapParkPoint, distTmapParkName, distTmapParkAddr);
                } else if (selectDist.equals("2km (약 30분)")) {// 2km 선택시

                    if (removeMarker.isAlive()) {

                        removeMarker.interrupt();

                    }
                    removeMarker = new removeMarker();
                    removeMarker.start();

                    location location = new location(getContext());
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    point = new TMapPoint(lat, lon);
                    TMapCircle tMapCircle = new TMapCircle();
                    tMapCircle.setCenterPoint(point);
                    tMapCircle.setRadius(1000);
                    tMapCircle.setCircleWidth(2);
                    tMapCircle.setLineColor(Color.BLUE);
                    tMapCircle.setAreaColor(Color.GRAY);
                    tMapCircle.setAreaAlpha(100);
                    tmap.addTMapCircle("circle1", tMapCircle);

                    Location CurrentLocation = new Location("");//현재위치
                    CurrentLocation.setLatitude(lat);
                    CurrentLocation.setLongitude(lon);


                    for (int i = 0; i < arrTMapPointPark.size(); i++) {//공원
                        Location parkLocation = new Location("");
                        parkLocation.setLatitude(arrParkLat.get(i));
                        parkLocation.setLongitude(arrParkLon.get(i));

                        double distance = CurrentLocation.distanceTo(parkLocation);

                        if (distance <= 1000) {
                            TMapPoint dtpoint = new TMapPoint(parkLocation.getLatitude(), parkLocation.getLongitude());
                            String dtname = arrParkName.get(i);
                            String dtaddr = arrParkAddr.get(i);
                            distTmapParkPoint.add(dtpoint);
                            distTmapParkName.add(dtname);
                            distTmapParkAddr.add(dtaddr);
                        }
                    }
                    for (int j = 0; j < arrTMapPointHospital.size(); j++) {//병원
                        Location hostpitalLocation = new Location("");
                        hostpitalLocation.setLatitude(arrHospitalLat.get(j));
                        hostpitalLocation.setLongitude(arrHospitalLon.get(j));

                        double distance = CurrentLocation.distanceTo(hostpitalLocation);

                        if (distance <= 1000) {
                            TMapPoint dtpoint = new TMapPoint(hostpitalLocation.getLatitude(), hostpitalLocation.getLongitude());
                            String dtname = arrHospitalName.get(j);
                            String dtaddr = arrHospitalAddr.get(j);
                            distTmapHospitalName.add(dtname);
                            distTmapHospitalAddr.add(dtaddr);
                            distTmapHospitalPoint.add(dtpoint);
                        }
                    }
                    for (int k = 0; k < arrTMapPointShop.size(); k++) {//관련상점
                        Location shopLocation = new Location("");
                        shopLocation.setLatitude(arrShopLat.get(k));
                        shopLocation.setLongitude(arrShopLon.get(k));

                        double distance = CurrentLocation.distanceTo(shopLocation);

                        if (distance <= 1000) {
                            TMapPoint dtpoint = new TMapPoint(shopLocation.getLatitude(), shopLocation.getLongitude());
                            String dtname = arrShopName.get(k);
                            String dtaddr = arrShopAddr.get(k);
                            distTmapShopPoint.add(dtpoint);
                            distTmapShopName.add(dtname);
                            distTmapShopAddr.add(dtaddr);

                        }
                    }
                    makeMarkerShop(distTmapShopPoint, distTmapShopName, distTmapShopAddr);
                    makeMarkerHospital(distTmapHospitalPoint, distTmapHospitalName, distTmapHospitalAddr);
                    makeMarkerPark(distTmapParkPoint, distTmapParkName, distTmapParkAddr);
                } else if (selectDist.equals("3km (약 45분)")) {// 3km 선택시
                    if (removeMarker.isAlive()) {

                        removeMarker.interrupt();

                    }
                    removeMarker = new removeMarker();
                    removeMarker.start();

                    distTmapParkPoint.clear();
                    distTmapHospitalPoint.clear();
                    distTmapShopPoint.clear();

                    location location = new location(getContext());
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    point = new TMapPoint(lat, lon);
                    TMapCircle tMapCircle = new TMapCircle();
                    tMapCircle.setCenterPoint(point);
                    tMapCircle.setRadius(1500);
                    tMapCircle.setCircleWidth(2);
                    tMapCircle.setLineColor(Color.BLUE);
                    tMapCircle.setAreaColor(Color.GRAY);
                    tMapCircle.setAreaAlpha(100);
                    tmap.addTMapCircle("circle1", tMapCircle);

                    Location location2 = new Location("");//현재위치
                    location2.setLatitude(lat);
                    location2.setLongitude(lon);
                    // 리스트 크기만큼 반복문

                    for (int i = 0; i < arrTMapPointPark.size(); i++) {//공원
                        Location location3 = new Location("");
                        location3.setLatitude(arrParkLat.get(i));
                        location3.setLongitude(arrParkLon.get(i));

                        double distance = location2.distanceTo(location3);

                        //걸러진 어레이 리스트도 필요함
                        if (distance <= 1500) {
                            TMapPoint dtpoint = new TMapPoint(location3.getLatitude(), location3.getLongitude());
                            String dtname = arrParkName.get(i);
                            String dtaddr = arrParkAddr.get(i);
                            distTmapParkName.add(dtname);
                            distTmapParkAddr.add(dtaddr);
                            distTmapParkPoint.add(dtpoint);
                        }

                    }
                    for (int j = 0; j < arrTMapPointHospital.size(); j++) {//병원
                        Location location4 = new Location("");
                        location4.setLatitude(arrHospitalLat.get(j));
                        location4.setLongitude(arrHospitalLon.get(j));

                        double distance = location2.distanceTo(location4);

                        if (distance <= 1500) {
                            TMapPoint dtpoint = new TMapPoint(location4.getLatitude(), location4.getLongitude());
                            String dtname = arrHospitalName.get(j);
                            String dtaddr = arrHospitalAddr.get(j);
                            distTmapHospitalName.add(dtname);
                            distTmapHospitalAddr.add(dtaddr);
                            distTmapHospitalPoint.add(dtpoint);
                        }
                    }
                    for (int k = 0; k < arrTMapPointShop.size(); k++) {//관련 상점
                        Location location5 = new Location("");
                        location5.setLatitude(arrShopLat.get(k));
                        location5.setLongitude(arrShopLon.get(k));

                        double distance = location2.distanceTo(location5);

                        if (distance <= 1500) {
                            TMapPoint dtpoint = new TMapPoint(location5.getLatitude(), location5.getLongitude());
                            String dtname = arrHospitalName.get(k);
                            String dtaddr = arrHospitalAddr.get(k);
                            distTmapShopPoint.add(dtpoint);
                            distTmapShopName.add(dtname);
                            distTmapShopAddr.add(dtaddr);
                        }
                    }

                    makeMarkerShop(distTmapShopPoint, distTmapShopName, distTmapShopAddr);
                    makeMarkerHospital(distTmapHospitalPoint, distTmapHospitalName, distTmapHospitalAddr);
                    makeMarkerPark(distTmapParkPoint, distTmapParkName, distTmapParkAddr);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        try {
            SharedPreferences prefs = getContext().getSharedPreferences("trailInfo", getContext().MODE_PRIVATE);

            int size = prefs.getInt("Status_size", 0);
            String name = sf.getString("name", "");
            String start = sf.getString("start", "");
            String end = sf.getString("end", "");
            Log.d("이름, 시작, 끝", name + ", " + start + ", " + end);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
/*
                for(int i=0;i<size;i++)
                {
                    trailInfo.add(prefs.getString("Status_" + i, null));
                }
                for(String e : trailInfo) {
                    if(e.contains("km")){
                         name = e;
                    }else {
                        String[] pt = e.split(":");
                        TMapPoint tp = new TMapPoint(Double.valueOf(pt[0]), Double.valueOf(pt[1]));
                        passList.add(tp);
                        Log.d("받은 좌표 값 : ", e);
                    }
                }*/
        TMapData tmapdata = new TMapData();
/*                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, passList, 0,
                             new TMapData.FindPathDataListenerCallback() {
                             @Override
                            public void onFindPathData(final TMapPolyLine tMapPolyLine) {



                                      tMapPolyLine.setLineWidth(5);
                                       tMapPolyLine.setLineColor(Color.BLUE);
 Toast.makeText(getApplicationContext(), “” + Pick, Toast.LENGTH_SHORT).show();

                                    tmap.addTMapPath(tMapPolyLine);


                        }
                     });*/

        return v;
    }

    private class dbLoad extends Thread {// 공원, 병원, 상점 정보 가져오기
        public dbLoad() {

        }

        @Override
        public void run() {

            if (arrTMapPointPark.isEmpty()) {// 공원 정보 가져오기
                mReference = mDatabase.getReference("park"); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String db_name = messageData.child("p_name").getValue().toString();
                            String db_lat = messageData.child("p_lat").getValue().toString();
                            String db_lon = messageData.child("p_lon").getValue().toString();
                            String db_addr = messageData.child("p_addr").getValue().toString();

                            arrParkName.add(db_name);
                            arrParkLat.add(Double.valueOf(db_lat));
                            arrParkLon.add(Double.valueOf(db_lon));
                            arrParkAddr.add(db_addr);

                            TMapPoint tMapPoint = new TMapPoint(Double.valueOf(db_lat), Double.valueOf(db_lon));
                            arrTMapPointPark.add(tMapPoint);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (arrTMapPointHospital.isEmpty()) {// 병원 정보 가져오기
                mReference = mDatabase.getReference("hospital"); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String db_name = messageData.child("h_name").getValue().toString();
                            String db_lat = messageData.child("h_lat").getValue().toString();
                            String db_lon = messageData.child("h_lon").getValue().toString();
                            String db_addr = messageData.child("h_addr").getValue().toString();

                            arrHospitalName.add(db_name);
                            arrHospitalLat.add(Double.valueOf(db_lat));
                            arrHospitalLon.add(Double.valueOf(db_lon));
                            arrHospitalAddr.add(db_addr);

                            TMapPoint tMapPoint = new TMapPoint(Double.valueOf(db_lat), Double.valueOf(db_lon));
                            arrTMapPointHospital.add(tMapPoint);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            if (arrTMapPointShop.isEmpty()) {// 관련상점 정보 가져오기
                mReference = mDatabase.getReference("shop"); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String db_name = messageData.child("s_name").getValue().toString();
                            String db_lat = messageData.child("s_lat").getValue().toString();
                            String db_lon = messageData.child("s_lon").getValue().toString();
                            String db_addr = messageData.child("s_addr").getValue().toString();

                            arrShopName.add(db_name);
                            arrShopLat.add(Double.valueOf(db_lat));
                            arrShopLon.add(Double.valueOf(db_lon));
                            arrShopAddr.add(db_addr);


                            TMapPoint tMapPoint = new TMapPoint(Double.valueOf(db_lat), Double.valueOf(db_lon));
                            arrTMapPointShop.add(tMapPoint);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }
    }

    private class loadBoard extends Thread {//게시글 정보 가져오기
        public loadBoard() {

        }

        @Override
        public void run() {

            if (boardTopgPoint.isEmpty()) {
                mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String db_title = messageData.child("title").getValue().toString();
                            String db_lat = messageData.child("lat").getValue().toString();
                            String db_lon = messageData.child("lon").getValue().toString();
                            String db_gPoint = messageData.child("gPoint").getValue().toString();
                            String db_address = messageData.child("address").getValue().toString();

                            if (Integer.valueOf(db_gPoint) >= 20) {


                                boardTopName.add(db_title);
                                boardToplat.add(Double.valueOf(db_lat));
                                boardToplon.add(Double.valueOf(db_lon));
                                boardTopAddr.add(db_address);
                                boardTopgPoint.add(db_gPoint);

                                TMapPoint tMapPoint = new TMapPoint(Double.valueOf(db_lat), Double.valueOf(db_lon));
                                boardTopPoint.add(tMapPoint);
                                Log.d("주소가 잘 나오나", String.valueOf(tMapPoint));
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


        }
    }


    private class calTime extends Thread {//시간 계산 쓰레드
        public calTime() {

        }

        @Override
        public void run() {
            min = (int) (time1 / 60 % 60);
            Log.d("시간 : ", String.valueOf(min));
        }
    }

    private class removeMarker extends Thread {//마커 삭제 쓰레드
        public removeMarker() {

        }

        @Override
        public void run() {
            tmap.removeTMapPath();
            tmap.removeAllMarkerItem();
            tmap.removeAllTMapPolyLine();
            tmap.removeAllTMapCircle();
            for (int c = 0; c < arrHospitalName.size(); c++) {
                tmap.removeMarkerItem2(arrHospitalName.get(c));
            }
            for (int c = 0; c < arrShopName.size(); c++) {
                tmap.removeMarkerItem2(arrShopName.get(c));
            }
            for (int c = 0; c < arrParkName.size(); c++) {
                tmap.removeMarkerItem2(arrParkName.get(c));
            }
            for (int c = 0; c < boardTopName.size(); c++) {
                tmap.removeMarkerItem2(boardTopName.get(c));
            }
        }
    }

    private class ballonEvent extends Thread {//풍선뷰 이벤트 쓰레드
        private static final String TAG = "ballonEvent";

        public ballonEvent() {
            // 초기화 작업
        }

        public void run() {

            TMapData tmapdata = new TMapData();
            tmap.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {

                @Override
                public void onCalloutMarker2ClickEvent(String s, TMapMarkerItem2 tMapMarkerItem2) {//마커 클릭 이벤트
                    tmap.removeAllTMapPolyLine();
                    if (arrHospitalName.contains(tMapMarkerItem2.getID())) {
                        Intent intent = new Intent(getActivity(), hospitalReview.class);
                        intent.putExtra("hpName", String.valueOf(tMapMarkerItem2.getID()));

                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(getActivity(), naviActivity.class);
                        intent.putExtra("naviName", String.valueOf(tMapMarkerItem2.getID()));
                        TMapPoint et = tmap.getMarkerItem2FromID(s).getTMapPoint();
                        intent.putExtra("eplt", tmap.getMarkerItem2FromID(s).getTMapPoint().getLatitude());
                        intent.putExtra("epln", tmap.getMarkerItem2FromID(s).getTMapPoint().getLongitude());

                        startActivity(intent);

                    }

                    location location = new location(getActivity());
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    TMapPoint st_point = new TMapPoint(lat, lon);
                    // 보행자 폴리라인 생성
                    tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, st_point, tmap.getMarkerItem2FromID(s).getTMapPoint(), new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {

                            polyLine.setID("pedLine");
                            tmap.addTMapPath(polyLine);
                        }
                    });

                }
            });
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        sm.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    
    @Override
    public void onSensorChanged(SensorEvent event) {// 센서값 변경시 호출되는 메소드
        sf = getContext().getSharedPreferences("info", getContext().MODE_PRIVATE);
        String weight = sf.getString("weight", "");
        String id = sf.getString("inputId", "");

        if (start == true) {//산책 시작 시
            if (count == 0) {
                try {
                    count++;
                    SharedPreferences wsf = getContext().getSharedPreferences("walkinginfo", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor wsfwalking = wsf.edit();

                    steps = steps + wsf.getInt("steps", 0)-1;
                    totalSteps = totalSteps + wsf.getInt("totalSteps", 0)-1;
                    coin = coin + wsf.getInt("coin", 0);
                    kcal = kcal + wsf.getInt("kcal",0);


                    wsfwalking.remove("steps");
                    wsfwalking.remove("totalSteps");
                    wsfwalking.remove("kcal");
                    wsfwalking.remove("coin");
                    wsfwalking.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long end = System.currentTimeMillis();
            min = (int) ((end - time1) / 1000) / 60 % 60;
            sec = (int) ((end - time1) / 1000) % 60;
            // air= 3.5*몸무게*분 kcal=air*5/1000
            // min = (time1 / 60 % 60);

            Log.d("시간 : ", String.valueOf(min) + "분");
            Log.d("시간 : ", String.valueOf(sec) + "초");

    /*        air = 3.5 * Integer.parseInt(weight) * min;
            kcal = air * 5 / 1000;*/
            kcalView.setText("kcal\n" + (int) kcal);
            coinView.setText("코인 수\n" + coin);
            // 센서 유형이 스텝감지 센서인 경우 걸음수 +1
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                tv_sensor.setText("걸음 수\n" + String.valueOf(totalSteps));

                steps++;
                totalDistance = steps * 0.7; //m
                accDistance = totalSteps * 0.7; //m

                Log.d("총 걸음 수 ", String.valueOf((int) totalSteps));
                Log.d("현재 걸음 수 ", String.valueOf(steps));
                Log.d("현재 거리 ", String.valueOf(accDistance));

                if (totalDistance >= 2000) {//2km이상 걸을 시

                    totalDistance = totalDistance - 2000;
                    steps = 0;
                    dista.setText("이동거리\n" + (int) accDistance + " M");

                    Log.d("코인 수 ", String.valueOf(coin));
                    Log.d("아이디확인 ", id);

                        coinView.setText("코인 수\n" + coin);


                } else {
                    dista.setText("이동거리\n" + (int) accDistance + " M");
                }
            }
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {


        public void onLocationChanged(Location location) {//위치 정보 변경 시 호출

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


    public void setGps() {//위치 권한 요청
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                100, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }
    

    public void makeMarkerPark(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {//공원 마커 표시

        for (int i = 0; i < arrTMapPoint.size(); i++) {
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_park);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            marker.setPosition(0.0f, 0.0f);
            marker.getTMapPoint();

            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));
        }
    }

    public void makeMarkerBoard(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {//좋아요 높은 게시글 마커 표시

        for (int i = 0; i < arrTMapPoint.size(); i++) {
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.good);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / 2, true);
            marker.setPosition(0.0f, 0.0f);
            marker.getTMapPoint();

            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));
        }
    }

    public void makeMarkerShop(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {//관련 상점 마커 표시
        tmap.removeAllMarkerItem();
        for (int i = 0; i < arrTMapPoint.size(); i++) {
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_shop);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            marker.setPosition(0.0f, 0.0f);
            marker.getTMapPoint();
            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));
        }
    }

    public void makeMarkerHospital(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {//병원 마커 표시
        tmap.removeAllMarkerItem();
        for (int i = 0; i < arrTMapPoint.size(); i++) {
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_hospital);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            marker.setPosition(0.0f, 0.0f);
            marker.getTMapPoint();
            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_ADD_CONTACT) {
            if (resultCode == RESULT_OK) {
                tmap.removeTMapPath();
                int no = intent.getIntExtra("contact_no", 0);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        location lc = new location(getContext());
                        TMapPoint mypoint = new TMapPoint(lc.getLatitude(), lc.getLongitude());
                        String trailName = intent.getStringExtra("name");
                        String trailStart = intent.getStringExtra("start");
                        String trailEnd = intent.getStringExtra("end");
                        String[] tstart = trailStart.split(":");
                        String[] tsend = trailEnd.split(":");

                        TMapPoint stpt = new TMapPoint(Double.valueOf(tstart[0]), Double.valueOf(tstart[1]));
                        TMapPoint edpt = new TMapPoint(Double.valueOf(tsend[0]), Double.valueOf(tsend[1]));

                        Log.d("이름 시작 도착 ", trailName + ", " + trailStart + ", " + trailEnd);
                        TMapData tmapdata = new TMapData();//화면에 산책로 표시
                        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, stpt, edpt, passList, 0, new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine tMapPolyLine) {

                                tMapPolyLine.setLineWidth(5);
                                tMapPolyLine.setLineColor(Color.BLUE);
                                tmap.addTMapPath(tMapPolyLine);
                                TMapInfo info = tmap.getDisplayTMapInfo(tMapPolyLine.getLinePoint());

                                int zoom = info.getTMapZoomLevel();
                                if (zoom > 14) {
                                    zoom = 14;
                                }
                                tmap.setZoomLevel(zoom);
                                tmap.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude());
                            }
                        });
                    }
                }).start();

            }

        }
    }



    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    public abstract class OnSingleClickListener implements View.OnClickListener {// 싱글 클릭 리스너

        //중복 클릭 방지 시간 설정 ( 해당 시간 이후에 다시 클릭 가능 )
        private static final long MIN_CLICK_INTERVAL = 600;
        private long mLastClickTime = 0;

        public abstract void onSingleClick(View v);

        @Override
        public final void onClick(View v) {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;

            // 중복클릭 아닌 경우
            if (elapsedTime > MIN_CLICK_INTERVAL) {
                onSingleClick(v);
            }
        }
    }


}
