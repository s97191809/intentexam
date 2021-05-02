package com.example.intentexam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;


public class maps extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private TMapGpsManager tmapgps = null;
    private TMapPoint point = null;
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    TMapView tmap;

    @Override
    public void onLocationChange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        //TMapPoint point = new TMapPoint(lat, lon);
        TMapPoint point = tmapgps.getLocation();

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

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);

        tmap = new TMapView(this);

        tmap.setSKTMapApiKey(TMAP_API_KEY);

        linearLayoutTmap.addView(tmap);

        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.

        setGps();//위치 권한 요청

        TMapData tmapdata = new TMapData();
        TMapPOIItem tMapPOIItem = new TMapPOIItem();

        final ArrayList<TMapPoint> arrTMapPoint = new ArrayList<>();
        final ArrayList<String> arrTitle = new ArrayList<>();
        final ArrayList<String> arrAddress = new ArrayList<>();
        makeMarker(arrTMapPoint);


    }

    public void makeMarker(ArrayList<TMapPoint> arrTMapPoint) {


        for (int i = 0; i < arrTMapPoint.size(); i++) {
            TMapMarkerItem markerItem = new TMapMarkerItem();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maker_hospital);
            markerItem.setVisible(TMapMarkerItem.VISIBLE);
            Log.d(
                    "point : ", (arrTMapPoint.get(i)).toString()
            );

            // 마커 아이콘 지정
            markerItem.setIcon(bitmap);
            // 마커의 좌표 지정
            markerItem.setTMapPoint(arrTMapPoint.get(i));

            //지도에 마커 추가
            tmap.addMarkerItem("markerItem" + i, markerItem);
        }
    }

    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }

    public void searchPOI(ArrayList<TMapPoint> arrTMapPoint, ArrayList<String> arrTitle, ArrayList<String> arrAddress) {
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI("관광지, 공원, 청주, 흥덕구", new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList tMapPOIItem) {
                for (int i = 0; i < tMapPOIItem.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem) tMapPOIItem.get(i);
                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                            "Point: " + item.getPOIPoint().toString());
                    arrTMapPoint.add(item.getPOIPoint());
                    arrTitle.add(item.getPOIName());
                    arrAddress.add(item.upperAddrName + " " +
                            item.middleAddrName + " " + item.lowerAddrName);
                }
                makeMarker(arrTMapPoint);
            }
        });

    }
}

