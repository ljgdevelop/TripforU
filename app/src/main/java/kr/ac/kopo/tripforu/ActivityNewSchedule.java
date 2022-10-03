package kr.ac.kopo.tripforu;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityNewSchedule extends PageController implements OnMapReadyCallback {


    private MapView mapView;
    private static NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private int currLayout = 0;
    Button BTN_AddSchedule;
    LinearLayout container;
    String[] strs = {"AAA, BBB, CCC, DDD"};
    SearchView searchView;
    ListView listView;

    @Override protected boolean useToolbar(){ return true; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_newsch);
        container = (LinearLayout) findViewById(R.id.LAYOUT_NewSch_Container);
        View view = getLayoutInflater().inflate(R.layout.layout_newsch_date,container,false);
        container.addView(view);

        for (int i = 0; i < 12; i ++){
            LayoutCalendar c = new LayoutCalendar(getApplicationContext());
            c.setDate(LocalDate.now().plusMonths(i));
            ((LinearLayout)findViewById(R.id.LAYOUT_CC)).addView(c);
        }



        SetAppBarAction(0, true, "취소").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppBarClickListener(currLayout);
            }
        });
        SetAppBarAction(1, false, "다음").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAppBarClickListener(currLayout);
            }
        });

    }

    private void setAppBarClickListener(int isLeft){

    }

    private void onLayoutNewSchList(){

        ExtendedFloatingActionButton BTN_AddSchedule = (ExtendedFloatingActionButton) findViewById(R.id.BTN_AddSchedule);
        BTN_AddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout layout_WayPointSCHContainer = findViewById(R.id.LAYOUT_WayPointSCHContainer);

                LinearLayout layout_WaypointSch = v.findViewById(R.id.LAYOUT_WaypointSch);
                layout_WayPointSCHContainer.addView(layout_WaypointSch);

            }
        });

    }

    protected void findSearchView(){
        searchView = (SearchView) findViewById(R.id.LAYOUT_Search_View);
        listView = (ListView) findViewById(R.id.List_View);
        listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strs));
        listView.setTextFilterEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    listView.setFilterText(newText);
                }else{
                    listView.clearTextFilter();
                }
                return false;
            }
        });
    }

    public void showNaverMap(){
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3l42j6ptcl")

        );

        //네이버맵 표시하기
        showNaverMap();

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

