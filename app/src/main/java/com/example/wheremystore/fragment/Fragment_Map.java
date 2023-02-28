package com.example.wheremystore.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.wheremystore.R;
import com.example.wheremystore.activity.Activity_Info;
import com.example.wheremystore.activity.Popup_option;
import com.example.wheremystore.api_zip.StoreInfoAPI;
import com.example.wheremystore.dto.StoreInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;
import com.example.wheremystore.item.ZoomLevel;
import com.google.android.material.snackbar.Snackbar;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Map extends Fragment implements OnMapReadyCallback {
    NaverMap mNaverMap;
    Button refreshBtn;
    ProgressDialog progressDialog;

    // 지도에서 내 위치 표시할 떄 사용하기 위한 것들
    FusedLocationSource mLocationSource;
    int LOCATION_PERMISSION_CODE = 1000;
    int OPTION_INTENT_CODE = 2000;

    List<Marker> markerList = new ArrayList<>();

    StoreInfoAPI storeInfoAPI = Retrofit_Item.getRetrofit().create(StoreInfoAPI.class);
    Sp sp;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__map, container, false);

        sp = new Sp(requireActivity());

        // fragment 옵션 메뉴를 가질 수 있도록 설정
        setHasOptionsMenu(true);

        refreshBtn = view.findViewById(R.id.map_refreshBtn);
        refreshBtn.setOnClickListener(v -> {
            refreshBtn.setVisibility(View.INVISIBLE);
            setMapMarker();
        });

        // 내 위치를 알아오기 위한 로케이션 소스
        mLocationSource = new FusedLocationSource(requireActivity(), LOCATION_PERMISSION_CODE);

        // 지도 객체 생성
        FragmentManager fm = getChildFragmentManager();
        MapFragment mf = (MapFragment) fm.findFragmentById(R.id.map_mapView);
        if (mf == null) {
            mf = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_mapView, mf).commit();
        }
        // MapReady 연결
        mf.getMapAsync(this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;

        // 가장 마지막에 저장되어 있던 위치를 가지고와 화면(카메라) 이동 > 즉, 초기 셋팅
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lastLat = location.getLatitude();
        double lastLng = location.getLongitude();
        CameraPosition cameraPosition = new CameraPosition(new LatLng(lastLat, lastLng), 16);
        mNaverMap.setCameraPosition(cameraPosition);

        // 지도에 위치관련 소스를 설정하고 추적모드를 팔로우로 해서 화면을 내가 있는 위치로 이동
        mNaverMap.setLocationSource(mLocationSource);
        mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // 네이버지도 UI 설정
        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);

        mNaverMap.addOnCameraChangeListener((i, b) -> {
            // 지도가 움직여질 때 즉 화면(카메라)가 이동될 때 int i 값은 -1이 뜨므로, -1일 때가 움직였을 때로 판단하여 새로고침 버튼을 보여준다.
            if((i == -1)) {
                refreshBtn.setVisibility(View.VISIBLE);
            }
        });

        setMapMarker();
    }

    private void setMapMarker() {
        progressDialog = ProgressDialog.show(requireActivity(), "가져오는 중", "잠시만 기다려주세요...", true, false);
        CameraPosition cameraPosition = mNaverMap.getCameraPosition();
        String centerLat = String.valueOf(cameraPosition.target.latitude);
        String centerLng = String.valueOf(cameraPosition.target.longitude);
        String option = sp.getOption();
        String distance = String.valueOf(ZoomLevel.kilometer((float) cameraPosition.zoom));

        storeInfoAPI.getMarker(centerLat, centerLng, distance, option).enqueue(new Callback<List<StoreInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<StoreInfoDTO>> call, @NonNull Response<List<StoreInfoDTO>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        List<StoreInfoDTO> dtoList = response.body();
                        // marker 초기화
                        for(Marker marker : markerList) {
                            marker.setMap(null);
                        }
                        markerList.clear();

                        // 가져온 자료로 마커 추가
                        for(StoreInfoDTO dto : dtoList) {
                            Marker marker = new Marker();
                            double lat = Double.parseDouble(dto.getLat());
                            double lng = Double.parseDouble(dto.getLng());
                            marker.setPosition(new LatLng(lat, lng));
                            marker.setIcon(OverlayImage.fromResource(R.drawable.icon_location)); // icon 이미지
                            marker.setWidth(100); // icon 크기
                            marker.setHeight(100); // icon 크기
                            marker.setAlpha(0.8f); // icon 투명도
                            marker.setMap(mNaverMap);
                            marker.setOnClickListener(v -> {
                                Intent intent = new Intent(requireActivity(), Activity_Info.class);
                                intent.putExtra("seq", dto.getSeq());
                                startActivity(intent);
                                return false;
                            });
                            markerList.add(marker);
                        }
                    }
                    progressDialog.dismiss();
                } else {
                    Log.e("getMarker", "get Marker failure");
                    progressDialog.dismiss();
                    Snackbar.make(requireView(), "잠시 후 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StoreInfoDTO>> call, @NonNull Throwable t) {
                Log.e("getMarker", "failure get markers reason : " + t.getMessage());
                progressDialog.dismiss();
                Snackbar.make(requireView(), "잠시 후 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // 상단 옵션을 설정하여 보이게 하는 메소드
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.top_map,menu);
    }

    // 상단 옵션에서 버튼 별 실행을 설정하는 메소드
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.top_map_searchBtn) {
            Intent intent = new Intent(requireActivity(), Popup_option.class);
            startActivityForResult(intent, OPTION_INTENT_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPTION_INTENT_CODE) {
            if(resultCode == RESULT_OK) {
                setMapMarker();
            }
        }
    }
}