package kr.ac.kopo.tripforu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

interface OnBackPressedListener {
    void onBackPressed();
}

interface PageAdepter{
    PageController pageAdepter = new PageController();
}


public class PageController extends AppCompatActivity implements OnBackPressedListener, PageAdepter{
    private static ArrayList<Page> pageStack = new ArrayList<>();
    public boolean isLoggedIn = false;
    final static byte TYPE_ACTIVITY = 0;
    final static byte TYPE_VIEW = 1;
    byte TYPE_HIDEANDSHOW = 2;
    public SharedPreferences prefs;
    
    protected static FrameLayout fullView = null;
    
    public static void AddPage(Page page){
        pageStack.add(page);
    }
    public static void AddPage(View page, byte type){
        pageStack.add(new Page(page, TYPE_VIEW));
    }
    public static void AddPage(Activity activity){ pageStack.add(new Page(activity, TYPE_ACTIVITY)); }
    public static void PopPage(){
        pageStack.remove(pageStack.size() - 1);
    }
    
    /***
     * @author 이제경
     * 뒤로가기 버튼 클릭시 화면 상단에 띄워져있는 레이아웃부터 종료
     *
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(pageStack.size() > 1) {
            Page lastPage = pageStack.get(pageStack.size() - 1);
            switch (lastPage.GetPageType()) {
                case 0:
                    ((Activity) lastPage.GetPageObject()).finish();
                    break;
                case 1:
                    ScrollView v = ((View)lastPage.GetPageObject()).findViewById(R.id.VIEW_SchInfoScroll);
                    String tag = v.getTag().toString();
                    if(tag.equals("onTop") || tag.equals("init")) {
                        v.setOnTouchListener(((view1, motionEvent) -> true));
                        v.setTag("remove");
                        v.smoothScrollTo(0, 0);
                        //((ViewGroup)((View) lastPage.GetPageObject()).getParent()).removeView((View) lastPage.GetPageObject());s
                    }
                    else {
                        v.smoothScrollTo(0, findViewById(R.id.LAYOUT_SchInfoMiddle).getBottom() - v.getHeight());
                        v.setTag("onTop");
                    }
                    return;
                case 2:
                    break;
            }
            PopPage();
        }
        else{
            //종료 팝업
        }
    }
    
    /***
     * @author 이제경
     * @return 툴바 사용 여부
     * <p>
     * 다른 액티비티에서 앱바를 보여줄지를 정할 수 있게해주는 함수
     * 해당 액티비티에서 오버라이드 해서 사용
     * 모든 액티비티와 연결된 자바파일에 적용
     * </p>
     * <p>
     * 사용법 : 해당 자바파일에서 Class 영역 안에 
     * @Override protected boolean useToolbar(){ return true; } 추가
     * 이때 true면 화면 상단에 앱바가 나타나고 false면 안 나타남
     * </p>
     */
    protected boolean useToolbar(){
        return true;
    }
    
    
    @Override
    public void setContentView(int layoutResID){
        fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        View child = getLayoutInflater().inflate(layoutResID, activityContainer, true);
        ResetAppBar();
        
        super.setContentView(fullView);
    
        Toolbar toolbar = fullView.findViewById(R.id.LAYOUT_AppBar);
        if(useToolbar()){
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    /**
     * 앱바 좌, 우에 원하는 텍스트 버튼 배치
     *
     * @author 이제경
     * @param type - 텍스트뷰의 색 지정(0:검정, 1:positive, 2:negative)
     * @param isLeft - 텍스트뷰의 위치 지정(true:왼쪽, false:오른쪽)
     * @param text - 텍스트뷰의 내용
     * @return 설정된 텍스트뷰(View 클래스) 반환
     */
    protected View SetAppBarAction(int type, boolean isLeft, String text){
        String color;
        int id;
        switch (type){
            case 0://default black
                color = "#5E5E5E";
                break;
            case 1://positive
                color = "#43992a";
                break;
            case 2://negative
                color = "#ff8888";
                break;
            default:
                return null;
        }
        
        if(isLeft){
            id = R.id.TEXT_AppBarLeft;
            fullView.findViewById(R.id.TEXT_AppBarTittle).setVisibility(View.GONE);
        }
        else{
            id = R.id.TEXT_AppBarRight;
            fullView.findViewById(R.id.IMG_AppBarRight).setVisibility(View.GONE);
        }
    
        TextView view = fullView.findViewById(id);
        view.setTextColor(Color.parseColor(color));
        view.setVisibility(View.VISIBLE);
        view.setText(text);
        return view;
    }
    
    /**
     * 앱바를 기본 상태로 초기화
     *
     * @author 이제경
     */
    protected void ResetAppBar(){
        fullView.findViewById(R.id.TEXT_AppBarLeft).setVisibility(View.GONE);
        fullView.findViewById(R.id.TEXT_AppBarRight).setVisibility(View.GONE);
        fullView.findViewById(R.id.TEXT_AppBarTittle).setVisibility(View.VISIBLE);
        fullView.findViewById(R.id.IMG_AppBarRight).setVisibility(View.VISIBLE);
    }
    
    
    
    ObjectAnimator scrollAnimator;
    static int scrollTime = 0;
    int oldX = 0;
    @SuppressLint("ClickableViewAccessibility")
    protected void AnimateHorizontalScroll(HorizontalScrollView horizontalScrollView){
        horizontalScrollView.setTag(0);
        horizontalScrollView.setOnTouchListener((view, motionEvent) -> {
            ViewParent vp = view.getParent();
            vp.requestDisallowInterceptTouchEvent(true);
            if(motionEvent.getAction() == MotionEvent.ACTION_MOVE ){
                scrollTime++;
                if(scrollTime < 2)
                    oldX = (int)motionEvent.getX();
            }
            else if((motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
                     && scrollTime > 5){
                //스크롤 뷰 속 컨테이너의 X좌표 가져오기
                scrollTime = 0;
                int count = 0;
                ArrayList<Integer> posX = new ArrayList<>();
                LinearLayout parent = ((LinearLayout)horizontalScrollView.getChildAt(0));
                for(int i = 0; i < parent.getChildCount(); i++){
                    if(parent.getChildAt(i).getVisibility() == View.VISIBLE) {
                        parent.getChildAt(i).setTag(count++);
                        posX.add(parent.getChildAt(i).getLeft());
                    }
                }
                if(oldX - (int)motionEvent.getX() > 50 &&
                    (int)horizontalScrollView.getTag() < count - 1){//오른쪽으로 이동
                    horizontalScrollView.setTag((int)horizontalScrollView.getTag() + 1);
                }
                else if(oldX - (int)motionEvent.getX() < -50 &&
                    (int)horizontalScrollView.getTag() > 0){//왼쪽으로 이동
                    horizontalScrollView.setTag((int)horizontalScrollView.getTag() - 1);
                }
                if(scrollAnimator != null && scrollAnimator.isRunning())
                    scrollAnimator.end();
                scrollAnimator = ObjectAnimator.ofInt(horizontalScrollView, "ScrollX", posX.get((int)horizontalScrollView.getTag()));
                scrollAnimator.setDuration(400);
                scrollAnimator.start();
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                Log.d("TAG", "a: " + ((ViewGroup)((ViewGroup)view).getChildAt(0)).getChildAt((int)horizontalScrollView.getTag()).dispatchTouchEvent(motionEvent));
                return false;
            }
            return true;
        });
    }
    
    protected void TabHorizontalScroll(HorizontalScrollView horizontalScrollView, int index){
        scrollTime = 0;
        int count = 0;
        ArrayList<Integer> posX = new ArrayList<>();
        LinearLayout parent = ((LinearLayout)horizontalScrollView.getChildAt(0));
        for(int i = 0; i < parent.getChildCount(); i++){
            if(parent.getChildAt(i).getVisibility() == View.VISIBLE) {
                parent.getChildAt(i).setTag(count++);
                posX.add(parent.getChildAt(i).getLeft());
            }
        }
        horizontalScrollView.setTag(index);
        if(scrollAnimator != null && scrollAnimator.isRunning())
            scrollAnimator.end();
        scrollAnimator = ObjectAnimator.ofInt(horizontalScrollView, "ScrollX", posX.get((int)horizontalScrollView.getTag()));
        scrollAnimator.setDuration(400);
        scrollAnimator.start();
    }
    
    /***
     * @author 이제경
     * -> 함수 : 사용자가 로그인 상태인지, 또 사용자의 정보를 가져올 수 있는 상태인지 확인
     */
    protected void CheckClientHasToken(){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if(throwable != null){
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.VISIBLE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.TEXT_WelcomeText)).setText("안녕하세요!");
                    this.isLoggedIn = false;
                }
                else if (user != null) {
                    this.isLoggedIn = true;
                    /*Log.i("TAG", "사용자 정보 요청 성공");
                    Log.i("TAG", "회원번호: " + user.getId());
                    Log.i("TAG", "이메일: " + user.getKakaoAccount().getEmail());
                    Log.i("TAG", "닉네임: " + user.getKakaoAccount().getProfile().getNickname());
                    Log.i("TAG", "프로필사진: " + user.getKakaoAccount().getProfile().getProfileImageUrl());*/
                    Glide.with(this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into((ImageView) findViewById(R.id.IMG_Profile));
                    ((TextView)findViewById(R.id.TEXT_AccountName)).setText(user.getKakaoAccount().getProfile().getNickname());
                    String welcomeText = user.getKakaoAccount().getProfile().getNickname() + "님";
                    ((TextView)findViewById(R.id.TEXT_WelcomeText)).setText(welcomeText);
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.GONE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.VISIBLE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.TEXT_WelcomeText)).setText("안녕하세요!");
                    this.isLoggedIn = false;
                }
                return null;
            });
        }
        else {
            this.isLoggedIn = false;
        }
    }
    
    /***
     * @author 이제경
     * @param schedule - 보여질 일정 정보를 담고있는 Schedule 객체
     * @return 화면에 추가된 View
     * -> 함수 : 일정의 세부내용을 보여주는 Schedule Info의 내용을 동기화 합니다.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    public View ShowScheduleInfo(Schedule schedule){
        //뷰를 화면 상단(Z축)에 복제
        View view = getLayoutInflater().inflate(R.layout.layout_scheduleinfo, fullView, false).findViewById(R.id.LAYOUT_SchInfo);
        fullView.addView(view);
        
        //자식 뷰의 객체 가져오기
        View scrollMarginTop = view.findViewById(R.id.VIEW_ScrollMargin);//여행 티켓의 상단 마진
        TextView txt_schName = view.findViewById(R.id.TEXT_SchInfoName); //일정의 제목
        TextView txt_schUpperLoc = view.findViewById(R.id.TEXT_SchInfoUpperLoc);//상위지역
        TextView txt_schSpecLoc = view.findViewById(R.id.TEXT_SchInfoSpecLoc);//하위지역
        TextView txt_schStartDate = view.findViewById(R.id.TEXT_SchInfoStartDate);//여행 출발일
        TextView txt_schDays = view.findViewById(R.id.TEXT_SchInfoEndDate);//숙박일 수
        TextView txt_schCompanyNum = view.findViewById(R.id.TEXT_SchInfoNum);//일정의 참여자 수
        ScrollView layout_schScroll = view.findViewById(R.id.VIEW_SchInfoScroll);
        FloatingActionButton fab = findViewById(R.id.BTN_SchFAB);
        
        //스케줄 객체의 정보를 자식뷰에 표시
        String schName = schedule.GetName();
        String Destination = schedule.GetDestination();
        if(Destination.contains("시 ")) {
            txt_schUpperLoc.setText(Destination.split("시 ")[0] + "시");
            txt_schSpecLoc.setText(Destination.split("시 ")[1]);
        }
        else if(Destination.contains("도 ")) {
            txt_schUpperLoc.setText(Destination.split("도 ")[0] + "도");
            txt_schSpecLoc.setText(Destination.split("도 ")[1]);
        }
        String schStartDate = pageAdepter.getDateDay(schedule.GetStartDate(), "MM. dd, EEE");
        String schDays = schedule.GetDays() + "";
        Member member = ScheduleController.GetMemberByID(schedule.GetMemberGroupId());
        String schCompanyNum = member.GetUserIdList().size() + "";
        txt_schName.setSelected(true);
        txt_schName.setText("Trip Pass : " + schName);
        txt_schStartDate.setText(schStartDate);
        txt_schDays.setText(schDays);
        txt_schCompanyNum.setText(schCompanyNum);
        view.setVisibility(View.VISIBLE);
        scrollMarginTop.setLayoutParams(new LinearLayout.LayoutParams(0, getResources().getDisplayMetrics().heightPixels + 10));
        layout_schScroll.setTag("init");
        
        //뷰에 경유지 내용 삽입
        AddWaypointInfo(view, schedule);
        
        //뷰가 화면에 그려질때까지 대기 후 적정 위치로 스크롤
        new Thread(() -> {
            while(findViewById(R.id.LAYOUT_SchInfoMiddle).getBottom() == 0){
                try { Thread.sleep(100);} catch (InterruptedException e) { }
            }
            int dy = findViewById(R.id.LAYOUT_SchInfoMiddle).getBottom() - view.getHeight();
            layout_schScroll.smoothScrollBy(0, dy);
            return;
        }).start();
    
        layout_schScroll.setOnScrollChangeListener((scroll, x, y, dx, dy) -> {
            try{
                int vy = findViewById(R.id.LAYOUT_SchInfoMiddle).getBottom() - view.getHeight();
                Log.d("TAG", "scrollV: " + scroll.getTag());
                //티켓 상단부에서 한번 걸침
                if(Math.abs(dy - vy) < 1)
                    scroll.setTag("onTop");
                
                if(dy < vy && (scroll.getTag() == "scrolled" || scroll.getTag() == "init"))
                    ((ScrollView) scroll).smoothScrollTo(0, vy);
                else if(dy > vy && scroll.getTag() == "onTop")
                    scroll.setTag("scrolled");
                
                //화면 위치에따라 FAB 표시
                if(y < vy + ConvertDPtoPX(getApplicationContext(), 106) && isFABShown/*
                        && ((FrameLayout.LayoutParams)fab.getLayoutParams()).bottomMargin > ConvertDPtoPX(getApplicationContext(), 31)*/)
                   hideFAB(fab, dy - y);
                else if(y > vy + ConvertDPtoPX(getApplicationContext(), 106) && !isFABShown/*
                        && ((FrameLayout.LayoutParams)fab.getLayoutParams()).bottomMargin < ConvertDPtoPX(getApplicationContext(), -47)*/)
                    showFAB(fab);
                    
                
                //걸친 상태에서 더 스크롤 할시 삭제
                if (dy < vy - 2 && (scroll.getTag() == "onTop")) {
                    scroll.setTag("remove");
                    ((ScrollView) scroll).smoothScrollTo(0, 0);
                }
                else if (y < 10 && scroll.getTag() == "remove") {
                    ((ViewGroup) fullView).removeView(view);
                    PopPage();
                }
            }catch(Exception e){
                Log.e("TAG", "ShowScheduleInfo: ", e);
            }
        });
        
        //뒤로기기키로 닫기 위해 페이지 등록
        AddPage(new Page(view, pageAdepter.TYPE_VIEW));
        
        return view;
    }
    
    private static void AddWaypointInfo(View view, Schedule schedule){
        final LinearLayout wpContainer = view.findViewById(R.id.VIEW_SchInfoWPContainer);
        for(Waypoint waypoint : schedule.GetWayPointList()){
            View layoutWP = View.inflate(ActivityMain.context, R.layout.layout_scheduleinfo_waypoint, null);
            
            final TextView wpName = (TextView) layoutWP.findViewById(R.id.TEXT_WaypointName);
            final TextView wpDisc = (TextView) layoutWP.findViewById(R.id.TEXT_WaypointDisc);
            ImageView wpIcon = (ImageView) layoutWP.findViewById(R.id.IMG_WaypointIcon);
            
            wpName.setText(waypoint.GetName());
            int time = waypoint.GetTime();
            String disc = time + "분";
            if(time > 60) {
                disc = (time / 60) + "시간 ";
                if(time % 60 > 0)
                    disc += (time % 60) + "분";
            }
            wpDisc.setText(disc);
            
            int imgSrc = -1;
            switch (waypoint.GetType()){
                case 1:
                    imgSrc = R.drawable.ic_waypoint_home;
                    break;
                case 2:
                    imgSrc = R.drawable.ic_waypoint_circle;
                    break;
                case 3:
                    imgSrc = R.drawable.ic_waypoint_train;
                    break;
                case 4:
                    imgSrc = R.drawable.ic_waypoint_subway;
                    break;
                case 5:
                    imgSrc = R.drawable.ic_waypoint_car;
                    break;
                case 6:
                    imgSrc = R.drawable.ic_waypoint_hotel;
                    break;
                case 7:
                    imgSrc = R.drawable.ic_waypoint_restaurant;
                    break;
                case 8:
                    imgSrc = R.drawable.ic_waypoint_pin;
                    break;
                default:
                    imgSrc = R.drawable.ic_waypoint_pin;
                    break;
            }
            wpIcon.setImageResource(imgSrc);
            
            wpContainer.addView(layoutWP);
        }
    }
    
    ValueAnimator va;
    boolean isFABShown;
    private void showFAB(FloatingActionButton fab){
        if(va != null && va.isRunning())
            va.pause();
        isFABShown = true;
        va = ValueAnimator.ofInt(((FrameLayout.LayoutParams) fab.getLayoutParams()).bottomMargin, ConvertDPtoPX(getApplicationContext(), 32));
        va.addUpdateListener(valueAnimator -> {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
            lp.setMargins(0, 0, ConvertDPtoPX(getApplicationContext(), 32), (Integer) valueAnimator.getAnimatedValue());
            fab.setLayoutParams(lp);
        });
        va.setDuration(500);
        va.setInterpolator(new DecelerateInterpolator());
        va.start();
    }
    
    private void hideFAB(FloatingActionButton fab, int velocity){
        if(va != null && va.isRunning())
            va.pause();
        isFABShown = false;
        va = ValueAnimator.ofInt(((FrameLayout.LayoutParams) fab.getLayoutParams()).bottomMargin, ConvertDPtoPX(getApplicationContext(), -48));
        va.addUpdateListener(valueAnimator -> {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
            lp.setMargins(0, 0, ConvertDPtoPX(getApplicationContext(), 32), (int) Math.max(ConvertDPtoPX(getApplicationContext(), -48), (Integer) valueAnimator.getAnimatedValue() - velocity ));
            fab.setLayoutParams(lp);
        });
        va.setDuration(250);
        va.setInterpolator(new DecelerateInterpolator());
        va.start();
    }
    
    public String getDateDay(String date, String pattern){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat nDate = new SimpleDateFormat(pattern);
            Date formatDate = dateFormat.parse(date);
            String strNewDtFormat = nDate.format(formatDate);
            return strNewDtFormat;
        }catch (Exception e){
            Log.e("TAG", "getDateDay: ", e);
            return "";
        }
    }
    
    public void checkFirstRun(){
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun){
            Intent i = new Intent(getApplicationContext(), ActivityPermissionCheck.class);
            startActivityForResult(i, 0);
            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
    }
    
    public static int ConvertSPtoPX(@NonNull Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    
    public static int ConvertDPtoPX(@NonNull Context context, int dp) {
        return Math.round((float) dp * context.getResources().getDisplayMetrics().density);
    }
    
    public static int ConvertPXtoDP(@NonNull Context context, int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = px * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(dp);
    }
}
