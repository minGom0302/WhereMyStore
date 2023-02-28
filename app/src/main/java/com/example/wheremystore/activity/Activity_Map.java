package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import com.example.wheremystore.R;
import com.example.wheremystore.fragment.Fragment_Add;
import com.example.wheremystore.item.GeoCoding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.indoor.IndoorSelection;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.Objects;

public class Activity_Map extends AppCompatActivity implements OnMapReadyCallback {
    TextView addrTv;
    NaverMap mNaverMap;
    FusedLocationSource mLocationSource;

    int LOCATION_PERMISSION_CODE = 2000;
    String address, strLat, strLng;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 액션바 숨기기
        Objects.requireNonNull(getSupportActionBar()).setTitle("위치 선택하기");

        // 내 위치를 알아오기 위한 로케이션 소스
        mLocationSource = new FusedLocationSource(Activity_Map.this, LOCATION_PERMISSION_CODE);

        // 지도 객체 생성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mf = (MapFragment) fm.findFragmentById(R.id.map_mapView);
        if (mf == null) {
            mf = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.mapChoice_mapView, mf).commit();
        }
        // MapReady 연결
        mf.getMapAsync(this);

        addrTv = findViewById(R.id.mapChoice_addrTv);
        Button okBtn = findViewById(R.id.mapChoice_okBtn);
        okBtn.setOnClickListener(v -> addressOk());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;

        // 가장 마지막에 저장되어 있던 위치를 가지고와 화면(카메라) 이동 > 즉, 초기 셋팅
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lastLat = location.getLatitude();
        double lastLng = location.getLongitude();
        CameraPosition cameraPosition = new CameraPosition(new LatLng(lastLat, lastLng), 16);
        mNaverMap.setCameraPosition(cameraPosition);

        strLng = String.valueOf(lastLng);
        strLat = String.valueOf(lastLat);
        address = GeoCoding.getAddress(getApplicationContext(), lastLat, lastLng);
        addrTv.setText(address);

        // 화면의 움직임이 끝났을 때 작동하는 리스너
        mNaverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double lng = mNaverMap.getCameraPosition().target.longitude;
                double lat = mNaverMap.getCameraPosition().target.latitude;
                address = GeoCoding.getAddress(getApplicationContext(), lat, lng);
                strLng = String.valueOf(lng);
                strLat = String.valueOf(lat);
                addrTv.setText(address);
            }
        });
    }

    private void addressOk() {
        Intent intent = new Intent();
        intent.putExtra("lat", strLat);
        intent.putExtra("lng", strLng);
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }
}