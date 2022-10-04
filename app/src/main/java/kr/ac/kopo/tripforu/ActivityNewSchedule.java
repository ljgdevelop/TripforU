package kr.ac.kopo.tripforu;

import static android.content.ContentValues.TAG;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.style.light.Position;
import com.naver.maps.map.util.FusedLocationSource;

import java.time.LocalDate;
import java.util.ArrayList;

import retrofit2.http.Header;

@RequiresApi(api = Build.VERSION_CODES.O)
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
    ScrollView SchScroll;
    private boolean enabled;

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_newsch);


        scrollView();

        onLayoutNewSchList();

        container = (LinearLayout) findViewById(R.id.LAYOUT_NewSch_Container);
        View view = getLayoutInflater().inflate(R.layout.layout_newsch_date, container, false);
        container.addView(view);

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3l42j6ptcl"));

        mapView = (MapView) findViewById(R.id.map_View);
        mapView.getMapAsync(this);
        mapView.getMapAsync(this);

        for (int i = 0; i < 12; i++) {
            LayoutCalendar c = new LayoutCalendar(getApplicationContext());
            c.setDate(LocalDate.now().plusMonths(i));
            ((LinearLayout) findViewById(R.id.LAYOUT_CC)).addView(c);
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

    private void setAppBarClickListener(int isLeft) {

    }

    private void onLayoutNewSchList() {

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

    protected void findSearchView() {
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
                if (!TextUtils.isEmpty(newText)) {
                    listView.setFilterText(newText);
                } else {
                    listView.clearTextFilter();
                }
                return false;
            }
        });
    }


    public void scrollView() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x;

        HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.LAYOUT_SchScroll);
        scrollView.setHorizontalScrollBarEnabled(true);

        findViewById(R.id.LAYOUT_SchScroll).post(new Runnable() {
            @Override
            public void run() {
                ((HorizontalScrollView) findViewById(R.id.LAYOUT_SchScroll)).setScrollX(width);
            }
        });

        int ids[] = {R.id.LAYOUT_NewSch_List, R.id.LAYOUT_WaypointSch, R.id.LAYOUT_NewSch_Container,
                R.id.LAYOUT_NEWSch_Setting, R.id.LAYOUT_NewSch_Check};

        for (int i = 0; i < ids.length; i++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById(ids[i]).getLayoutParams();
            layoutParams.width = width;
            findViewById(ids[i]).setLayoutParams(layoutParams);
        }

    }

    protected void TabHorizontalScroll(HorizontalScrollView horizontalScrollView, int index) {
        scrollTime = 0;
        int count = 0;
        ArrayList<Integer> posX = new ArrayList<>();
        LinearLayout parent = ((LinearLayout) horizontalScrollView.getChildAt(1));
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).getVisibility() == View.VISIBLE) {
                parent.getChildAt(i).setTag(count++);
                posX.add(parent.getChildAt(i).getLeft());
            }
        }
        horizontalScrollView.setTag(index);
        if (scrollAnimator != null && scrollAnimator.isRunning())
            scrollAnimator.end();
        scrollAnimator = ObjectAnimator.ofInt(horizontalScrollView,
                "ScrollX", posX.get((int) horizontalScrollView.getTag()));
        scrollAnimator.setDuration(400);
        scrollAnimator.start();
    }

    //좌표에 찍힌 마커를 표시
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG, "onMapReady");

        UiSettings uiSettings = naverMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(false);

        Marker marker = new Marker();
        Marker marker1 = new Marker();
        Marker marker2 = new Marker();
        marker.setPosition(new LatLng(37.007482, 127.175927));
        marker1.setPosition(new LatLng(36.990951, 127.085106));
        marker2.setPosition(new LatLng(36.994694, 127.147171));
        marker.setMap(naverMap);
        marker1.setMap(naverMap);
        marker2.setMap(naverMap);

        marker.setIconPerspectiveEnabled(true);

        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    // 현제 위치 표시해주는 함수
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResult)) {
            if (locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResult);
    }

    public void onClickAppBar(View v, int currLayout) {
        switch (currLayout) {
            case 0:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 0));
                SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickAppBar(view1, 0));
                break;
            case 1:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 1));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 1));
                break;
            case 2:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 2));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 2));
                break;

            case 3:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 3));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 3));
                break;

            case 4:
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickAppBar(view1, 4));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 4));
                break;
        }
    }
}


