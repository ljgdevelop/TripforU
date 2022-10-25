package kr.ac.kopo.tripforu;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
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
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityNewSchedule extends PageController implements OnMapReadyCallback  {
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private SearchView searchView;
    private boolean scrollAction;
    private int memberCount = 1;
    private LayoutCalendar calendar;
    private Schedule newSchedule = new Schedule();
    private Waypoint selectedWaypoint;

    @Override
    protected boolean useToolbar() {
        return true;
    }
    
    @Override
    public void onBackPressed() {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullView.findViewById(R.id.TEXT_AppBarSearchText).getWindowToken(), 0);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_newsch);
    
        //네이버 맵 sdk 로드
        NaverMapSdk.getInstance(this).setClient(new NaverMapSdk.NaverCloudPlatformClient("3l42j6ptcl"));
        
        //horizontalScrollView 속에 있는 레이아웃의 너비를 화면 크기와 맞춤
        setScrollContainerSize();
    
        //horizontalScrollView의 의도치 않은 스크롤 막기
        findViewById(R.id.LAYOUT_SchScroll).setOnTouchListener(((view, motionEvent) -> {return true;}));

        //앱 바 설정
        SetAppBarAction(0, true, "").setOnClickListener(null);
        SetAppBarAction(0, false, "다음").setOnClickListener(v -> onAppBarClick(1));
        
        //캘린더 레이아웃 실행
        onLayoutCalendar();
        
        ImageButton plusBtn = findViewById(R.id.BTN_NewSch_Plus);
        ImageButton minusBtn = findViewById(R.id.BTN_NewSch_Minus);
        TextView countText = findViewById(R.id.TEXT_NewSch_Strength);
        plusBtn.setOnClickListener(view -> {
            if(memberCount < 8)
                memberCount++;
            countText.setText(memberCount + "");
        });
        minusBtn.setOnClickListener(view -> {
            if(memberCount > 1)
                memberCount--;
            countText.setText(memberCount + "");
        });
    }
    
    @SuppressLint("ClickableViewAccessibility")
    public void setScrollContainerSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y - ConvertDPtoPX(getApplicationContext(), 64);
    
        for (int i = 0; i < 5; i++) {
            View v = ((ViewGroup)((ViewGroup)findViewById(R.id.LAYOUT_SchScroll)).getChildAt(0)).getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
            layoutParams.width = width;
            v.setLayoutParams(layoutParams);
        }
    }
    
    private void onLayoutCalendar(){
        for (int i = 0; i < 12; i++) {
            calendar = new LayoutCalendar(getApplicationContext());
            calendar.setDate(LocalDate.now().plusMonths(i));
            ((LinearLayout) findViewById(R.id.LAYOUT_NewSch_Calendar_Container)).addView(calendar);
        }
    }
    
    private void onLayoutNewSchList() {
        ExtendedFloatingActionButton BTN_AddSchedule = (ExtendedFloatingActionButton) findViewById(R.id.BTN_AddSchedule);
        BTN_AddSchedule.setOnClickListener(v -> {
            findViewById(R.id.LAYOUT_NewSch_sch).setVisibility(View.VISIBLE);
            TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 3);
            SetAppBarAction(0, true, "취소").setOnClickListener(view -> onAppBarClick(30));
            SetAppBarAction(3, false, "선택").setOnClickListener(null);
            findViewById(R.id.LAYOUT_NewSch_Wp_View).setVisibility(View.GONE);
            onLayoutNewWaypoint();
        });
    }
    
    private void onLayoutNewWaypoint(){
        //네이버 맵 로드
        MapView mapView = (MapView) findViewById(R.id.map_View);
        mapView.getMapAsync(this);
        mapView.requestDisallowInterceptTouchEvent(true);
        
        //검색창
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.AutoTextView);
        autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        autoCompleteTextView.setAdapter(
            new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                getListOfWaypoints()));
        
        //검색 완료
        autoCompleteTextView.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        autoCompleteTextView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE){
                searchWaypointByName(autoCompleteTextView.getText().toString());
                return true;
            }
            return false;
        });
        autoCompleteTextView.setOnItemClickListener((adapterView, view1, i, l) -> {
            searchWaypointByName(autoCompleteTextView.getText().toString());
        });
    }
    
    private void searchWaypointByName(String name){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y - ConvertDPtoPX(getApplicationContext(), 56);
        
        //결과 레이아웃 보여주기
        NestedScrollView mapScroll = ((NestedScrollView) findViewById(R.id.LAYOUT_NewSch_Wp_Container));
        
        findViewById(R.id.LAYOUT_NewSch_Wp_View).setVisibility(View.VISIBLE);
    
        //키보드 닫기
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullView.findViewById(R.id.TEXT_AppBarSearchText).getWindowToken(), 0);
    
        //목록 확장 & 닫기
        findViewById(R.id.BTN_NewSch_Wp_ViewMore).setOnClickListener(view -> {
            if(mapScroll.getScrollY() < 10) {
                mapScroll.smoothScrollTo(0, view.getTop());
            }
            else {
                mapScroll.smoothScrollTo(0, 0);
            }
        });
        
        //검색 결과 초기화
        mapScroll.smoothScrollTo(0, 0);
        scrollAction = false;
        for(int i = 0; i < ((ViewGroup) findViewById(R.id.LAYOUT_NewSch_Wp_View)).getChildCount() - 2; i ++){
            ((ViewGroup) findViewById(R.id.LAYOUT_NewSch_Wp_View)).removeViewAt(3);
        }
        
        for (Waypoint waypoint:
                ScheduleController.getInstance().getAllWaypointValues()){
            if (waypoint.GetName().contains(name)){
                //리스트에 뷰 추가, 정보 입력
                View card = getLayoutInflater().inflate(R.layout.layout_newsch_wp_card, findViewById(R.id.LAYOUT_NewSch_Wp_View), false);
                ((LinearLayout) findViewById(R.id.LAYOUT_NewSch_Wp_View)).addView(card);
    
                TextView cardName = card.findViewById(R.id.LAYOUT_NewSch_WpName);
                TextView cardAddress = card.findViewById(R.id.LAYOUT_NewSch_WpAddress);
                TextView cardRating = card.findViewById(R.id.LAYOUT_NewSch_WpRating);
    
                cardName.setText(waypoint.GetName());
                cardAddress.setText(waypoint.GetName());
                cardRating.setText(waypoint.GetRating() + "");
    
                card.setOnClickListener(view -> onSelectWaypoint(view, waypoint));

                //지도에 마커 표시 후 이동
                Marker marker = new Marker();
                marker.setPosition(new LatLng(waypoint.GetPosY(), waypoint.GetPosX()));
                marker.setMap(naverMap);

                marker.setIconPerspectiveEnabled(true);
                
                if(!scrollAction) {
                    scrollAction = true;
                    mapScroll.scrollTo(0, card.getBottom());
    
                    selectedWaypoint = waypoint;
                    
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(waypoint.GetPosY(), waypoint.GetPosX()));
                    naverMap.moveCamera(cameraUpdate);
                }
            }
        }
    
        //목록 아래 정렬
        View margin = mapScroll.findViewById(R.id.LAYOUT_NewSch_Wp_CardContainer_Margin);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) margin.getLayoutParams();
        lp.height = (height - ConvertDPtoPX(getApplicationContext(), 64)) / 5 - findViewById(R.id.LAYOUT_NewSch_Wp_Card).getHeight();
        margin.setLayoutParams(lp);
    
        SetAppBarAction(1, false, "선택").setOnClickListener(v -> {
            View banner = getLayoutInflater().inflate(R.layout.layout_newsch_list_banner, findViewById(R.id.LAYOUT_NewSch_List_WPContainer), false);
            ((ViewGroup)findViewById(R.id.LAYOUT_NewSch_List_WPContainer)).addView(banner);
        
            ImageButton wicon = banner.findViewById(R.id.IMG_NewSch_List_Banner_Icon);
            TextView wname = banner.findViewById(R.id.TEXT_NewSch_List_Banner_Name);
            ImageButton wclose = banner.findViewById(R.id.BTN_NewSch_List_Banner_Close);
    
            wicon.setImageDrawable(ScheduleController.getInstance().getWayPointDrawable(selectedWaypoint));
            wname.setText(selectedWaypoint.GetName());
        
            newSchedule.addWayPoint(selectedWaypoint);
        
            TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 2);
        
            SetAppBarAction(0, true, "이전").setOnClickListener(vv -> onAppBarClick(20));
            SetAppBarAction(0, false, "완료").setOnClickListener(vv -> onAppBarClick(21));
        });
    }

    private void onSelectWaypoint(View view, Waypoint waypoint){
        //이동
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(waypoint.GetPosY(), waypoint.GetPosX()));
        naverMap.moveCamera(cameraUpdate);
    
        selectedWaypoint = waypoint;
    }
    
    private ArrayList<String> getListOfWaypoints(){
        ArrayList<String> filter = new ArrayList<>();
        for (Waypoint w : ScheduleController.getInstance().getAllWaypointValues()) {
            filter.add(w.GetName());
        }
        return filter;
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
        UiSettings uiSettings = naverMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(false);
        uiSettings.setLocationButtonEnabled(false);
        uiSettings.setLogoGravity(Gravity.RIGHT| Gravity.BOTTOM);

        LocationButtonView locationButtonView = findViewById(R.id.location);
        locationButtonView.setMap(naverMap);

        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
    }

    private void onAppBarClick(int index) {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullView.findViewById(R.id.TEXT_AppBarSearchText).getWindowToken(), 0);
        switch (index) {
            case 1:
                saveScheduleInfo();
                break;
            case 10:
                TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 0);
                SetAppBarAction(0, true, "").setOnClickListener(null);
                SetAppBarAction(0, false, "다음").setOnClickListener(v -> onAppBarClick(1));
                break;
            case 11:
                saveDateToSchedule();
                break;
            case 20:
                SetAppBarAction(0, true, "이전").setOnClickListener(v -> onAppBarClick(10));
                SetAppBarAction(0, false, "다음").setOnClickListener(v -> onAppBarClick(11));
                TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 1);
                break;
            case 21:
                ScheduleController.getInstance().addScheduleToDictionary(newSchedule);
                Log.d("TAG", "onAppBarClick: "+ newSchedule.getStartDate());
                setResult(RESULT_OK);
                finish();
                break;
            case 30:
                SetAppBarAction(0, true, "이전").setOnClickListener(v -> onAppBarClick(20));
                SetAppBarAction(0, false, "완료").setOnClickListener(v -> onAppBarClick(21));
                TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 2);
                break;
            case 31:
        
                break;
            case 40:
                break;
            case 41:
        
                break;
            default:
                break;
        }
    }
    
    private void saveScheduleInfo(){
        EditText title = findViewById(R.id.EDT_NewSch_Title);
        
        if(!title.getText().toString().equals("")) {
            newSchedule.setId(ScheduleController.getInstance().getAllSchedule().size());
            Member newMemeber = new Member(ScheduleController.getInstance().getAllMemberValues().size(), newSchedule.getId());
            for (int i = 0; i < memberCount; i++) {
                newMemeber.AddUserIdInList(i);
            }
            newSchedule.setMemberGroupId(newMemeber.GetId());
            newSchedule.setName(title.getText().toString());
            TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 1);
            SetAppBarAction(0, true, "이전").setOnClickListener(v -> onAppBarClick(10));
            SetAppBarAction(0, false, "다음").setOnClickListener(v -> onAppBarClick(11));
        }
        else
            Toast.makeText(getApplicationContext(), "여행 제목을 입력 해주세요", Toast.LENGTH_SHORT).show();
    }
    
    private void saveDateToSchedule(){
        if(calendar.firstDate != null && calendar.secondDate != null){
            SetAppBarAction(0, true, "이전").setOnClickListener(v -> onAppBarClick(20));
            SetAppBarAction(0, false, "완료").setOnClickListener(v -> onAppBarClick(21));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            newSchedule.setStartDate(calendar.firstDate.format(formatter));
            Log.d("TAG", "saveDateToSchedule: " + calendar.firstDate.format(formatter));
            Log.d("TAG", "saveDateToSchedule: " + newSchedule.getStartDate());
            newSchedule.setDays(Period.between(calendar.firstDate, calendar.secondDate).getDays());
            onLayoutNewSchList();
            TabHorizontalScroll(findViewById(R.id.LAYOUT_SchScroll), 2);
        }
        else
            Toast.makeText(getApplicationContext(), "날짜를 선택 해주세요.", Toast.LENGTH_SHORT).show();
    }
}


