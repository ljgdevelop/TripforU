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
import android.view.LayoutInflater;
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

public class PageController extends AppCompatActivity implements OnBackPressedListener{
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
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.GONE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.VISIBLE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.GONE);
                    this.isLoggedIn = false;
                }
                return null;
            });
        }
        else {
            this.isLoggedIn = false;
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
