package kr.ac.kopo.tripforu;

import static android.content.ContentValues.TAG;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.Header;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityNewSchedule extends PageController implements OnMapReadyCallback  {

    private MapView mapView;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private int currLayout = 0;
    Button BTN_AddSchedule, btnNewSch_Wp;
    LinearLayout container;
    private SearchView searchView;
    private List<String> list;
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

        mapView = (MapView) findViewById(R.id.map_View);
        mapView.getMapAsync(this);
        mapView.getMapAsync(this);

        HorizontalScrollView horizontalScrollView = findViewById(R.id.LAYOUT_SchScroll);
        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        container = (LinearLayout) findViewById(R.id.LAYOUT_NewSch_Container);
        View view = getLayoutInflater().inflate(R.layout.layout_newsch_date, container, false);
        container.addView(view);



        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3l42j6ptcl"));

        for (int i = 0; i < 12; i++) {
            LayoutCalendar c = new LayoutCalendar(getApplicationContext());
            c.setDate(LocalDate.now().plusMonths(i));
            ((LinearLayout) findViewById(R.id.LAYOUT_CC)).addView(c);
        }

        SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> {

        });
        SetAppBarAction(1, false, "다음").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHeader(view, 1, null, null);
            }
        });

        // 임시용
        Waypoint info1 = new Waypoint(3000, "고양이 정원", 37.5757, 126.8031, 40, 500, 1,
                "https://map.naver.com/v5/entry/place/38711752?c=14115640.8834218,4519644.4903778,17.77,0,0,0,dh",
                90);

        Waypoint info2 = new Waypoint(3001, "댕댕이 블루스", 37.1743, 126.0257, 40, 500, 1,
                "https://map.naver.com/v5/entry/place/1674853966?c=14140393.7563370,4463428.0987850,17.82,0,0,0,dh",
                90);

        Waypoint info3 = new Waypoint(3002, "댕댕이 블루스2", 37.1763, 126.0260, 40, 500, 1,
                "https://map.naver.com/v5/entry/place/1674853966?c=14140393.7563370,4463428.0987850,17.82,0,0,0,dh",
                90);

        ScheduleController.getInstance().addWaypointToList(info1);
        ScheduleController.getInstance().addWaypointToList(info2);
        ScheduleController.getInstance().addWaypointToList(info3);

        list = new ArrayList<String>();

        settingList();

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.AutoTextView);
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list));
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (keycode == KeyEvent.KEYCODE_ENTER){
                    searchWaypointByName(autoCompleteTextView.getText().toString());

                }
                return false;
            }
        });

        autoCompleteTextView.setSingleLine(true);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
    }

    private void searchWaypointByName(String name){
        View v = findViewById(R.id.LAYOUT_NewSch_WpList);
        v.setVisibility(View.VISIBLE);
        for (Waypoint w:
                ScheduleController.getInstance().getAllWaypointValues()){
            if (w.GetName().contains(name)){
                View vv = getLayoutInflater().inflate(R.layout.layout_newsch_wp, (ViewGroup) v, true);
                ImageView img = vv.findViewById(R.id.LAYOUT_NewSch_WpImg);
                TextView Name = vv.findViewById(R.id.LAYOUT_NewSch_WpName);
                TextView Address = vv.findViewById(R.id.LAYOUT_NewSch_WpAddress);
                TextView Rating = vv.findViewById(R.id.LAYOUT_NewSch_WpRating);

                Name.setText(w.GetName());
                Address.setText(w.GetName());
                Rating.setText(w.GetName());

                Marker marker = new Marker();
                marker.setPosition(new LatLng(w.GetPosX(), w.GetPosY()));
                marker.setMap(naverMap);

                marker.setIconPerspectiveEnabled(true);

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(w.GetPosX(), w.GetPosY()));
                naverMap.moveCamera(cameraUpdate);

            }
        }
    }

    private void settingList(){
        for (Waypoint w:
        ScheduleController.getInstance().getAllWaypointValues()) {
            list.add(w.GetName());
        }
    }

    private void onLayoutNewSchList() {

        ExtendedFloatingActionButton BTN_AddSchedule = (ExtendedFloatingActionButton) findViewById(R.id.BTN_AddSchedule);
        BTN_AddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout layout_WayPointSCHContainer = findViewById(R.id.LAYOUT_WayPointSCHContainer);
                LinearLayout layout_WaypointSch = v.findViewById(R.id.LAYOUT_NewSch_sch);
                layout_WayPointSCHContainer.addView(layout_WaypointSch);

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

        int ids[] = {R.id.LAYOUT_NewSch_List, R.id.LAYOUT_NewSch_sch, R.id.LAYOUT_NewSch_Container,
                R.id.LAYOUT_NEWSch_Setting, R.id.LAYOUT_NewSch_Check};

        for (int i = 0; i < ids.length; i++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById(ids[i]).getLayoutParams();
            layoutParams.width = width;
            findViewById(ids[i]).setLayoutParams(layoutParams);
        }

    }

    ObjectAnimator scrollAnimator;
    static int scrollTime;
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

    // 안드로이드 권환 호출
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

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

    //좌표에 찍힌 마커를 표시
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG, "onMapReady");

        UiSettings uiSettings = naverMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setLocationButtonEnabled(false);
        uiSettings.setLogoGravity(Gravity.RIGHT|Gravity.BOTTOM);

        LocationButtonView locationButtonView = findViewById(R.id.location);
        locationButtonView.setMap(naverMap);

        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);



    }



    private void onClickHeader(View view1, int i, View view2, ArrayList<Bitmap> bitmapArrayList){
        FrameLayout layout_newsch_list = findViewById(R.id.LAYOUT_NewSch_List);
        LinearLayout layout_newsch_sch = findViewById(R.id.LAYOUT_NewSch_sch);
        LinearLayout layout_newsch_date = findViewById(R.id.LAYOUT_CC);
    }

    public void onClickAppBar(View v, int currLayout) {
        switch (currLayout) {
            case 0:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 0));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 0));
                break;
            case 1:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 1));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 1));
                break;
            case 2:
                SetAppBarAction(1, false, "다음").setOnClickListener(view1 -> onClickAppBar(view1, 2));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickAppBar(view1, 2));
                break;

        }
    }
}


