package kr.ac.kopo.tripforu;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityNewSchedule extends PageController implements OnMapReadyCallback {


    private MapView mapView;
    private static NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private int currLayout = 0;

    @Override protected boolean useToolbar(){ return true; }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout_naver_map);

        //네이버맵 표시하기
        showNaverMap();

        SetAppBarAction(0, true, "취소").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppBarClickListener(currLayout);

            }
        });
        SetAppBarAction(0, false, "다음").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppBarClickListener(currLayout);

            }
        });
    }

    private void setAppBarClickListener(int isLeft){

    }

    //네이버 지도를 보여주는 함수
    private void showNaverMap(){
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3l42j6ptcl")
        );

        mapView = (MapView) findViewById(R.id.map_View);
        mapView.getMapAsync(this);
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    //좌표에 찍힌 마커를 표시
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d( TAG, "onMapReady");

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.007482, 127.175927));
        marker.setMap(naverMap);

        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

    }

    // 현제 위치 표시해주는 함수
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResult){
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResult)){
            if (locationSource.isActivated()){
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResult);
    }

    public void onLayoutDraw(int currLayout){
        switch (currLayout){
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;

            default:

                break;
        }
    }

    public void initOtherSetting(){

    }
}

