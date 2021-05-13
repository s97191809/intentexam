package com.example.intentexam;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapMarkerItemLayer;

import org.xml.sax.SAXException;

import java.io.IOException;
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
    List<String> arrParkName = new ArrayList<>();
    List<Double> arrParkLat = new ArrayList<>();
    List<Double> arrParkLon = new ArrayList<>();
    List<String> arrParkAddr = new ArrayList<>();
    List<String> arrHospitalName = new ArrayList<>();
    List<Double> arrHospitalLat = new ArrayList<>();
    List<Double> arrHospitalLon = new ArrayList<>();
    List<String> arrHospitalAddr = new ArrayList<>();
    List<String> arrShopName = new ArrayList<>();
    List<Double> arrShopLat = new ArrayList<>();
    List<Double> arrShopLon = new ArrayList<>();
    List<String> arrShopAddr = new ArrayList<>();
    final ArrayList<TMapPoint> arrTMapPointPark = new ArrayList<>();
    final ArrayList<TMapPoint> arrTMapPointHospital = new ArrayList<>();
    final ArrayList<TMapPoint> arrTMapPointShop = new ArrayList<>();
    private TMapGpsManager tmapgps = null;
    private TMapPoint point = null;
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    TMapView tmap;


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

        TMapData tmapdata = new TMapData();

        if (arrTMapPointPark.isEmpty()) {// 공원 정보 가져오기
            mReference = mDatabase.getReference("park"); // 변경값을 확인할 child 이름
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                        String db_name = messageData.child("p_name").getValue().toString();
                        String db_lat = messageData.child("p_lat").getValue().toString();
                        String db_lon = messageData.child("p_lon").getValue().toString();
                        String msg2 = messageData.getValue().toString();
                        arrParkName.add(db_name);
                        arrParkLat.add(Double.valueOf(db_lat));
                        arrParkLon.add(Double.valueOf(db_lon));

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


                makeMarkerPark(arrTMapPointPark);
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

                makeMarkerHospital(arrTMapPointHospital);
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
                        arrShopName.add(db_name);
                        arrShopLat.add(Double.valueOf(db_lat));
                        arrShopLon.add(Double.valueOf(db_lon));


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

                makeMarkerShop(arrTMapPointShop);

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
                final ArrayList<TMapPoint> distTmapHospitalPoint = new ArrayList<>();
                final ArrayList<TMapPoint> distTmapShopPoint = new ArrayList<>();
                if (selectDist.equals("거리 선택")) {
                    // 배열 초기화
                    distTmapParkPoint.clear();
                    distTmapHospitalPoint.clear();
                    distTmapShopPoint.clear();
                    // 마커와 원 삭제
                    tmap.removeAllMarkerItem();
                    tmap.removeAllTMapCircle();
                } else if (selectDist.equals("1km (약 15분)")) {// 1km 선택시
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
                        Location location3 = new Location("");
                        location3.setLatitude(arrParkLat.get(i));
                        location3.setLongitude(arrParkLon.get(i));

                        double distance = location2.distanceTo(location3);

                        //걸러진 어레이 리스트도 필요함
                        if (distance <= 500) {// 단위 (M)
                            Log.d("1KM 반경 거리", String.valueOf(distance));
                            Log.d("1km 반경 포인트", String.valueOf(location3.getLatitude()) + "," + location3.getLongitude());
                            TMapPoint dtpoint = new TMapPoint(location3.getLatitude(), location3.getLongitude());
                            distTmapParkPoint.add(dtpoint);
                        }
                    }
                    for (int j = 0; j < arrTMapPointHospital.size(); j++) {//병원
                        Location location4 = new Location("");
                        location4.setLatitude(arrHospitalLat.get(j));
                        location4.setLongitude(arrHospitalLon.get(j));

                        double distance = location2.distanceTo(location4);

                        if (distance <= 500) {
                            TMapPoint dtpoint = new TMapPoint(location4.getLatitude(), location4.getLongitude());
                            distTmapHospitalPoint.add(dtpoint);
                        }
                    }
                    for (int k = 0; k < arrTMapPointShop.size(); k++) {//관련 상점
                        Location location5 = new Location("");
                        location5.setLatitude(arrShopLat.get(k));
                        location5.setLongitude(arrShopLon.get(k));

                        double distance = location2.distanceTo(location5);

                        if (distance <= 500) {
                            TMapPoint dtpoint = new TMapPoint(location5.getLatitude(), location5.getLongitude());
                            distTmapShopPoint.add(dtpoint);
                        }
                    }
                    makeMarkerShop(distTmapShopPoint);
                    makeMarkerHospital(distTmapHospitalPoint);
                    makeMarkerPark(distTmapParkPoint);
                } else if (selectDist.equals("2km (약 30분)")) {// 2km 선택시
                    distTmapParkPoint.clear();
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
                            distTmapParkPoint.add(dtpoint);
                        }
                    }
                    for (int j = 0; j < arrTMapPointHospital.size(); j++) {//병원
                        Location hostpitalLocation = new Location("");
                        hostpitalLocation.setLatitude(arrHospitalLat.get(j));
                        hostpitalLocation.setLongitude(arrHospitalLon.get(j));

                        double distance = CurrentLocation.distanceTo(hostpitalLocation);

                        if (distance <= 1000) {
                            TMapPoint dtpoint = new TMapPoint(hostpitalLocation.getLatitude(), hostpitalLocation.getLongitude());
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
                            distTmapShopPoint.add(dtpoint);
                        }
                    }
                    makeMarkerShop(distTmapShopPoint);
                    makeMarkerHospital(distTmapHospitalPoint);

                    makeMarkerPark(distTmapParkPoint);
                } else if (selectDist.equals("3km (약 45분)")) {// 3km 선택시
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
                            distTmapShopPoint.add(dtpoint);
                        }
                    }
                    makeMarkerShop(distTmapShopPoint);
                    makeMarkerHospital(distTmapHospitalPoint);
                    makeMarkerPark(distTmapParkPoint);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        return v;
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

    public void makeMarkerPark(ArrayList<TMapPoint> arrTMapPoint) {
        for (int i = 0; i < arrTMapPoint.size(); i++) {
            TMapMarkerItem markerItem = new TMapMarkerItem();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_park);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            markerItem.setVisible(TMapMarkerItem.VISIBLE);

            markerItem.setIcon(bitmap);

            markerItem.setTMapPoint(arrTMapPoint.get(i));


            tmap.addMarkerItem("markerItemP" + i, markerItem);
        }
    }

    public void makeMarkerShop(ArrayList<TMapPoint> arrTMapPoint) {
        for (int i = 0; i < arrTMapPoint.size(); i++) {
            TMapMarkerItem markerItem = new TMapMarkerItem();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_shop);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            markerItem.setVisible(TMapMarkerItem.VISIBLE);


            markerItem.setIcon(bitmap);

            markerItem.setTMapPoint(arrTMapPoint.get(i));


            tmap.addMarkerItem("markerItemS" + i, markerItem);
        }
    }

    public void makeMarkerHospital(ArrayList<TMapPoint> arrTMapPoint) {

        for (int i = 0; i < arrTMapPoint.size(); i++) {
            TMapMarkerItem markerItem = new TMapMarkerItem();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_hospital);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            bitmap = bitmap.createScaledBitmap(bitmap, 100, height / (width / 100), true);
            markerItem.setVisible(TMapMarkerItem.VISIBLE);


            markerItem.setIcon(bitmap);

            markerItem.setTMapPoint(arrTMapPoint.get(i));


            tmap.addMarkerItem("markerItemH" + i, markerItem);
            setBalloonView(markerItem, arrHospitalName.get(i), arrHospitalAddr.get(i));

        }
    }
    private void setBalloonView(TMapMarkerItem marker, String title, String address)
    {
        marker.setCanShowCallout(true);
// 병원 이름 어레이랑 주소 어레이 크기만큼 반복해서 받아온다 아니면

            if( marker.getCanShowCallout() )
            {
                marker.setCalloutTitle(title);
                marker.setCalloutSubTitle(address);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
                marker.setCalloutRightButtonImage(bitmap);
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


}