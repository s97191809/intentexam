package com.example.intentexam;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapOverlay;
import com.skt.Tmap.TMapOverlayItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapMarkerItemLayer;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import javax.xml.parsers.ParserConfigurationException;

public class FragmentPage2 extends Fragment implements TMapGpsManager.onLocationChangedCallback {
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    private ListView listView;
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
    private TMapGpsManager tmapgps = null;
    private TMapPoint point = null;
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    TMapView tmap;
    ExampleThread thread;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //XML, java 연결
        //XML이 메인에 직접 붙으면 true, 프래그먼트에 붙으면 false

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_2, container, false);

        LinearLayout linearLayoutTmap = (LinearLayout) v.findViewById(R.id.tmap);

        tmap = new TMapView(getActivity());

        tmap.setSKTMapApiKey(TMAP_API_KEY);
        linearLayoutTmap.addView(tmap);

        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.
        tmap.setTrackingMode(true);
        tmap.setSightVisible(true);
        location location = new location(getActivity());


        setGps();//위치 권한 요청.
        listView = (ListView) v.findViewById(R.id.listviewmsg);
        initDatabase();

        thread = new ExampleThread();
        thread.start();


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
                        String msg2 = messageData.getValue().toString();
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
        button3 = v.findViewById(R.id.button3);// 공원 위치 표시
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmap.removeAllTMapPolyLine();
                for(int c = 0; c < arrHospitalName.size(); c++) {
                    tmap.removeMarkerItem2(arrHospitalName.get(c));
                }
                for(int c = 0; c < arrShopName.size(); c++) {
                    tmap.removeMarkerItem2(arrShopName.get(c));
                }
                for(int c = 0; c < arrParkName.size(); c++) {
                    tmap.removeMarkerItem2(arrParkName.get(c));
                }

                makeMarkerPark(arrTMapPointPark,arrParkName,arrParkAddr);
            }

        });
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
        button4 = v.findViewById(R.id.button4);// 병원 위치 표시
        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tmap.removeAllTMapPolyLine();
                for(int c = 0; c < arrHospitalName.size(); c++) {
                    tmap.removeMarkerItem2(arrHospitalName.get(c));
                }
                for(int c = 0; c < arrShopName.size(); c++) {
                    tmap.removeMarkerItem2(arrShopName.get(c));
                }
                for(int c = 0; c < arrParkName.size(); c++) {
                    tmap.removeMarkerItem2(arrParkName.get(c));
                }
              makeMarkerHospital(arrTMapPointHospital,arrHospitalName,arrHospitalAddr);

            }


        });
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
        button5 = v.findViewById(R.id.button5);// 관련 상점 위치 표시
        button5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tmap.removeAllTMapPolyLine();
                for(int c = 0; c < arrHospitalName.size(); c++) {
                    tmap.removeMarkerItem2(arrHospitalName.get(c));
                }
                for(int c = 0; c < arrShopName.size(); c++) {
                    tmap.removeMarkerItem2(arrShopName.get(c));
                }
                for(int c = 0; c < arrParkName.size(); c++) {
                    tmap.removeMarkerItem2(arrParkName.get(c));
                }
                makeMarkerShop(arrTMapPointShop,arrParkName,arrParkAddr);

            }


        });
        // 반경 설정 콤보 박스
        Spinner spiner = (Spinner) v.findViewById(R.id.select_distance);
        ArrayAdapter sAdapter = ArrayAdapter.createFromResource(getContext(), R.array.question, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(sAdapter);
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectDist = (String) spiner.getSelectedItem();
                final ArrayList<TMapPoint> distTmapParkPoint = new ArrayList<>();
                final ArrayList<String> distTmapParkName = new ArrayList<>();
                final ArrayList<String> distTmapParkAddr = new ArrayList<>();

                final ArrayList<TMapPoint> distTmapHospitalPoint = new ArrayList<>();
                final ArrayList<String> distTmapHospitalName = new ArrayList<>();
                final ArrayList<String> distTmapHospitalAddr = new ArrayList<>();

                final ArrayList<TMapPoint> distTmapShopPoint = new ArrayList<>();
                final ArrayList<String> distTmapShopName = new ArrayList<>();
                final ArrayList<String> distTmapShopAddr = new ArrayList<>();

                if (selectDist.equals("거리 선택")) {
                    tmap.removeAllTMapPolyLine();
                    for(int c = 0; c < arrHospitalName.size(); c++) {
                        tmap.removeMarkerItem2(arrHospitalName.get(c));
                    }
                    for(int c = 0; c < arrShopName.size(); c++) {
                        tmap.removeMarkerItem2(arrShopName.get(c));
                    }
                    for(int c = 0; c < arrParkName.size(); c++) {
                        tmap.removeMarkerItem2(arrParkName.get(c));
                    }
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
                    tmap.removeAllTMapPolyLine();
                    for(int c = 0; c < arrHospitalName.size(); c++) {
                        tmap.removeMarkerItem2(arrHospitalName.get(c));
                    }
                    for(int c = 0; c < arrShopName.size(); c++) {
                        tmap.removeMarkerItem2(arrShopName.get(c));
                    }
                    for(int c = 0; c < arrParkName.size(); c++) {
                        tmap.removeMarkerItem2(arrParkName.get(c));
                    }
                    distTmapParkPoint.clear();
                    tmap.removeAllMarkerItem();
                    location location = new location(getActivity());
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    point = new TMapPoint(lat, lon);
                    TMapCircle tMapCircle = new TMapCircle();
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
                    // 리스트 크기만큼 반복문

                    for (int i = 0; i < arrTMapPointPark.size(); i++) {//공원
                        Location parkDestination1Km = new Location("");
                        parkDestination1Km.setLatitude(arrParkLat.get(i));
                        parkDestination1Km.setLongitude(arrParkLon.get(i));

                        double distance = location2.distanceTo(parkDestination1Km);

                        //걸러진 어레이 리스트도 필요함
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

                        if (distance <= 500) {
                            TMapPoint dtpoint = new TMapPoint(shopDestination1Km.getLatitude(), shopDestination1Km.getLongitude());
                            String dtname = arrHospitalName.get(k);
                            String dtaddr = arrHospitalAddr.get(k);
                            distTmapShopPoint.add(dtpoint);
                            distTmapShopName.add(dtname);
                            distTmapShopAddr.add(dtaddr);
                        }
                    }
                    makeMarkerShop(distTmapShopPoint,distTmapShopName,distTmapShopAddr);
                    makeMarkerHospital(distTmapHospitalPoint,distTmapHospitalName,distTmapHospitalAddr);
                    makeMarkerPark(distTmapParkPoint,distTmapParkName,distTmapParkAddr);
                } else if (selectDist.equals("2km (약 30분)")) {// 2km 선택시
                    tmap.removeAllTMapPolyLine();
                    for(int c = 0; c < arrHospitalName.size(); c++) {
                        tmap.removeMarkerItem2(arrHospitalName.get(c));
                    }
                    for(int c = 0; c < arrShopName.size(); c++) {
                        tmap.removeMarkerItem2(arrShopName.get(c));
                    }
                    for(int c = 0; c < arrParkName.size(); c++) {
                        tmap.removeMarkerItem2(arrParkName.get(c));
                    }
                    location location = new location(getActivity());
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
                    // 리스트 크기만큼 반복문

                    for (int i = 0; i < arrTMapPointPark.size(); i++) {//공원
                        Location parkLocation = new Location("");
                        parkLocation.setLatitude(arrParkLat.get(i));
                        parkLocation.setLongitude(arrParkLon.get(i));

                        double distance = CurrentLocation.distanceTo(parkLocation);

                        if (distance <= 1000) {
                            Log.d("2KM 반경 거리", String.valueOf(distance));
                            Log.d("2km 반경 포인트", String.valueOf(parkLocation.getLatitude()) + "," + parkLocation.getLongitude());
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
                    makeMarkerShop(distTmapShopPoint,distTmapShopName,distTmapShopAddr);
                    makeMarkerHospital(distTmapHospitalPoint,distTmapHospitalName,distTmapHospitalAddr);
                    makeMarkerPark(distTmapParkPoint,distTmapParkName,distTmapParkAddr);
                } else if (selectDist.equals("3km (약 45분)")) {// 3km 선택시
                    tmap.removeAllTMapPolyLine();
                    for(int c = 0; c < arrHospitalName.size(); c++) {
                        tmap.removeMarkerItem2(arrHospitalName.get(c));
                    }
                    for(int c = 0; c < arrShopName.size(); c++) {
                        tmap.removeMarkerItem2(arrShopName.get(c));
                    }
                    for(int c = 0; c < arrParkName.size(); c++) {
                        tmap.removeMarkerItem2(arrParkName.get(c));
                    }
                    distTmapParkPoint.clear();
                    distTmapHospitalPoint.clear();
                    distTmapShopPoint.clear();
                    location location = new location(getActivity());
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
                            Log.d("3KM 반경 거리", String.valueOf(distance));
                            Log.d("3km 반경 포인트", String.valueOf(location3.getLatitude()) + "," + location3.getLongitude());
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
                    // 위치정보를 가져오는건 좋았으나 ㅇ위치에 대한 이름과 주소는 잘 가지고오지 못한거 같다.
                    makeMarkerShop(distTmapShopPoint,distTmapShopName,distTmapShopAddr);
                    makeMarkerHospital(distTmapHospitalPoint,distTmapHospitalName,distTmapHospitalAddr);
                    makeMarkerPark(distTmapParkPoint,distTmapParkName,distTmapParkAddr);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        return v;
    }


        private class ExampleThread extends Thread {
            private static final String TAG = "ExampleThread";

            public ExampleThread() {
                // 초기화 작업
            }

            public void run() {

                TMapData tmapdata = new TMapData();
                tmap.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
                    //마커 정보를 여기서 가져와서

                    @Override
                    public void onCalloutMarker2ClickEvent(String s, TMapMarkerItem2 tMapMarkerItem2) {
                        tmap.removeAllTMapPolyLine();
                        Log.d("아이템 확인 : " , s);
                        location location = new location(getActivity());
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        TMapPoint st_point = new TMapPoint(lat, lon);
                        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, st_point, tmap.getMarkerItem2FromID(s).getTMapPoint(), new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
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

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmap.setLocationPoint(longitude, latitude);
                tmap.setCenterPoint(longitude, latitude);
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void onLocationChange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        //TMapPoint point = new TMapPoint(lat, lon);
        TMapPoint point = tmapgps.getLocation();

    }


    public void setGps() {
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                0, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }

    public void setNavi() {
        TMapPoint tMapPointStart = new TMapPoint(37.570841, 126.985302); // SKT타워(출발지)
        TMapPoint tMapPointEnd = new TMapPoint(37.551135, 126.988205); // N서울타워(목적지)

        try {
            TMapPolyLine tMapPolyLine = new TMapData().findPathData(tMapPointStart, tMapPointEnd);
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);
            tmap.addTMapPolyLine("Line1", tMapPolyLine);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeMarkerPark(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {

        for (int i = 0; i < arrTMapPoint.size(); i++) {
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_park);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            marker.setPosition(0.2f, 0.2f);
            marker.getTMapPoint();

            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));
        }
    }

    public void makeMarkerShop(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {
        tmap.removeAllMarkerItem();
        for (int i = 0; i < arrTMapPoint.size(); i++) {
            TMapMarkerItem markerItem = new TMapMarkerItem();
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_shop);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            marker.setPosition(0.2f, 0.2f);
            marker.getTMapPoint();
            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));
        }
    }

    public void makeMarkerHospital(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrName, ArrayList<String> arrAddr) {
        tmap.removeAllMarkerItem();
        for (int i = 0; i < arrTMapPoint.size(); i++) {
            MarkerOverlay marker = new MarkerOverlay(getContext(), arrName.get(i), arrAddr.get(i));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_hospital);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            marker.setPosition(0.2f, 0.2f);
            marker.getTMapPoint();
            marker.setID(arrName.get(i));
            marker.setIcon(bitmap);
            marker.setTMapPoint(arrTMapPoint.get(i));

            tmap.addMarkerItem2(arrName.get(i), marker);
            tmap.showCallOutViewWithMarkerItemID(arrName.get(i));

        }
    }

    private void setBalloonView(TMapMarkerItem2 marker, String title, String address, TMapPoint point) {


        marker.setPosition(0.2f,0.2f);
        marker.getTMapPoint();
        marker.setID(title);
        marker.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.balloon_overlay_focused));
        marker.setTMapPoint(point);

        tmap.addMarkerItem2(title, marker);
        tmap.showCallOutViewWithMarkerItemID(title);
    }
    private void balloonViewEvent(){
        // 흠 지금 아이템 이름을 주소가아닌 이름으로 했으니 흠 ㅇㅋ
        //함수에 필요한게 마커 아이템이랑 출발, 도착지점 도착지점은 이름 리스트의 인덱스랑 일치하는 곳의 좌표로 설정 그럼될듯
        tmap.removeAllTMapPolyLine();
        tmap.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
            TMapData tmapdata = new TMapData();
            @Override
            public void onCalloutMarker2ClickEvent(String s, TMapMarkerItem2 tMapMarkerItem2) {

                tmap.getMarkerItem2FromID(s).getTMapPoint();
                location location = new location(getActivity());
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                TMapPoint st_point = new TMapPoint(lat, lon);
                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, st_point, tmap.getMarkerItem2FromID(s).getTMapPoint(), new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmap.addTMapPath(polyLine);
                    }
                });



            }
        });
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


}