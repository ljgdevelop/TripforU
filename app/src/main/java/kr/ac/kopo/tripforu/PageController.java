package kr.ac.kopo.tripforu;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;
import java.util.List;

interface OnBackPressedListener {
    void onBackPressed();
}



public class PageController extends AppCompatActivity implements OnBackPressedListener{
    private static ArrayList<Page> pageStack = new ArrayList<>();
    public boolean isLoggedIn = false;
    byte TYPE_ACTIVITY = 0;
    byte TYPE_INFLATE = 1;
    byte TYPE_HIDEANDSHOW = 2;
    byte TYPE_NAVDRAWER = 3;
    
    public static void AddPage(Page page){
        pageStack.add(page);
    }
    public static void PopPage(){
        pageStack.remove(pageStack.size() - 1);
    }
    
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
                    break;
                case 2:
                    break;
                case 3:
                    ((DrawerLayout)lastPage.GetPageObject()).closeDrawers();
                    break;
            }
            PopPage();
        }
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 설명 : 다른 액티비티에서 앱바를 보여줄지를 정할 수 있게해주는 함수
     * ->       해당 액티비티에서 오버라이드 해서 사용
     * ->       모든 액티비티와 연결된 자바파일에 적용
     * 
     * -> 사용법 : 해당 자바파일에서 Class 영역 안에 
     *            @Override protected boolean useToolbar(){ return true; } 추가
     *            이때 true면 화면 상단에 앱바가 나타나고 false면 안 나타남
     */
    protected boolean useToolbar(){
        return true;
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setContentView(int layoutResID){
        FrameLayout fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        View child = getLayoutInflater().inflate(layoutResID, activityContainer, true);
        
        super.setContentView(fullView);
        
        Toolbar toolbar = (Toolbar) fullView.findViewById(R.id.LAYOUT_AppBar);
        if(useToolbar()){
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }
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
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP ){
                Log.d("TAG", "Up: ");
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
            return true;
        });
    }
    
    protected void TabHorizontalScroll(HorizontalScrollView horizontalScrollView, int index){
        Log.d("TAG", "TabHorizontalScroll: " + index);
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
     * -> 작성자 : 이제경
     * -> 함수 : 사용자가 로그인 상태인지, 또 사용자의 정보를 가져올 수 있는 상태인지 확인
     */
    protected void CheckClientHasToken(){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if(throwable != null){
                    this.isLoggedIn = false;
                }
                else if (user != null) {
                    this.isLoggedIn = true;
                    Log.i("TAG", "사용자 정보 요청 성공");
                    Log.i("TAG", "회원번호: " + user.getId());
                    Log.i("TAG", "이메일: " + user.getKakaoAccount().getEmail());
                    Log.i("TAG", "닉네임: " + user.getKakaoAccount().getProfile().getNickname());
                    Log.i("TAG", "프로필사진: " + user.getKakaoAccount().getProfile().getProfileImageUrl());
                    UserApiClient.getInstance().accessTokenInfo((accessTokenInfo, error) -> {
                        if (error != null) {
                            Log.e("TAG", "토큰 정보 보기 실패", error);
                        }
                        else if (accessTokenInfo != null) {
                            Log.i("TAG", "토큰 정보 보기 성공" +
                                "\n회원번호: " + accessTokenInfo.getId() +
                                "\n만료시간: " + accessTokenInfo.getExpiresIn() + " 초");
                        }
                        return null;
                    });
                    /*int layoutHeight = ConvertSPtoPX(findViewById(R.id.IMG_KakaoProfile).getContext(), 50) +
                        ConvertDPtoPX((findViewById(R.id.LAYOUT_UserProfile).getContext()), 42);
                    
                    ((TextView)findViewById(R.id.TEXT_KakaoName)).setText(user.getKakaoAccount().getProfile().getNickname());
                    ((TextView)findViewById(R.id.TEXT_KakaoAdress)).setText(user.getKakaoAccount().getEmail());
                    Glide.with(this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into((ImageView) findViewById(R.id.IMG_KakaoProfile));
                    findViewById(R.id.LAYOUT_UserProfile).getLayoutParams().height = layoutHeight;
                    LayerDrawable drawable =
                        (LayerDrawable) getApplicationContext().getDrawable(R.drawable.background_white_rcorner_15);
                    findViewById(R.id.IMG_KakaoProfile).setBackground(drawable);
                    findViewById(R.id.IMG_KakaoProfile).setClipToOutline(true);*/
                    /*findViewById(R.id.IMG_KakaoProfile).setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            //outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), ConvertSPtoPX(findViewById(R.id.IMG_KakaoProfile).getContext(), 15));
    
                        }
                    });*/
                }
                else{
                    this.isLoggedIn = false;
                }
                return null;
            });
        }
        else {
            this.isLoggedIn = false;
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
