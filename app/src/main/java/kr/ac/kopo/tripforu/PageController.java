package kr.ac.kopo.tripforu;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;

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
    public static void AddPage(View page){
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
        if(pageStack.size() > 0) {
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
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) activityContainer.getLayoutParams();
        if(useToolbar()){
            setSupportActionBar(toolbar);
            lp.topMargin = ConvertDPtoPX(getApplicationContext(), 56);
            activityContainer.setLayoutParams(lp);
        } else {
            toolbar.setVisibility(View.GONE);
            lp.topMargin = 0;
            activityContainer.setLayoutParams(lp);
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
    /**
     * @author 이제경
     * @param horizontalScrollView - HorizontalScrollView
     * @param index - 스크롤 할 위치(뷰의 순서 기준)
     */
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
     *
     *      카카오톡으로 로그인 되어있는지 확인한 후,
     *      로그인 상태라면 사용자 설정 화면에 로그인 정보 표시
     */
    protected void checkClientHasToken(){
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
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void logoutKakao(){
        UserApiClient.getInstance().logout(throwable -> {
            if(throwable == null){
                Intent i = new Intent(this, ActivityMain.class);
                finish();
                startActivity(i);
            }
            return null;
        });
    }
    
    /***
     * @author 이제경
     *
     *      앱을 처음 실행하는지 확인.
     *      처음 실행시 권한 동의화면으로 이동
     */
    public void checkFirstRun(){
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun){
            Intent i = new Intent(getApplicationContext(), ActivityPermissionCheck.class);
            startActivityForResult(i, 0);
            JsonController.saveJsonFromAssets(getApplicationContext());
            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
        else{
            //Json형식의 데이터 동기화
            ScheduleController.syncJsonToObject(JsonController.readJson("member", getApplicationContext()),
                Member.class.toString());
            ScheduleController.syncJsonToObject(JsonController.readJson("waypoints", getApplicationContext()),
                Waypoint.class.toString());
            ScheduleController.syncJsonToObject(JsonController.readJson("schedule", getApplicationContext()),
                Schedule.class.toString());
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
    
    /***
     * @author 이제경
     * @param view - 태그를 적용 할 view
     * @param tag - 태그의 이름
     * @param value - 저장할 값
     *
     *      <p>view에 key-value 쌍으로 되어있는 Json형태의 데이터로 tag-value 저장</p>
     *      <p/>
     *      <p>HashMap처럼 여러개의 태그 저장 가능, 이미 있는 태그 이름으로 시도할 시 value를 덮어씌웁니다.</p>
     */
    public static void setTagToView(Object view, String tag, Object value){
        View v = ((View) view);
        if(v.getTag() == null || v.getTag().toString().isEmpty())
            v.setTag("\"" + tag + "\":" + value);
        else if(!v.getTag().toString().contains("\"" + tag + "\":")){
            v.setTag(v.getTag() + ", \"" + tag + "\":" + value);
        }
        else{
            String afterTag = v.getTag().toString().split("\"" + tag + "\":")[1];
            StringBuilder result = new StringBuilder();
            result.append(v.getTag().toString().split("\"" + tag + "\":")[0]);
            result.append("\"");
            result.append(tag);
            result.append("\":");
            result.append(value);
            if(afterTag.contains(",")) {
                result.append(",");
                result.append(afterTag.split(",")[1]);
            }
                
            v.setTag(result);
        }
    }
    
    /***
     * @author 이제경
     * @param view - 태그를 적용 할 view
     * @param tag - 태그의 이름
     * @return (String) 저장된 값
     *
     *      <p>view에 저장된 tag를 key로 갖는 value를 가져옵니다.</p>
     */
    public static String getTagFromView(Object view, String tag){
        View v = ((View) view);
        if(v.getTag() == null || !v.getTag().toString().contains(tag))
            return "";
        return v.getTag().toString().split("\"" + tag + "\":")[1].split(",")[0];
    }
}
