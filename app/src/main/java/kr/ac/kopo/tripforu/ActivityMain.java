package kr.ac.kopo.tripforu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.common.KakaoSdk;

import org.json.simple.JSONObject;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;

import kr.ac.kopo.tripforu.Retrofit.INetTask;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityMain extends PageController implements OnBackPressedListener{
    public static Context context;//this context
    private FrameLayout fullView;
    
    private LocalTime lastBackPressed;
    
    //툴바 사용
    @Override protected boolean useToolbar(){ return true; }
    
    /***
     * @author 이제경
     * 뒤로가기 버튼 클릭시 화면 상단에 띄워져있는 레이아웃부터 종료
     */
    @Override
    public void onBackPressed() {
        if(this.fullView.findViewById(R.id.LAYOUT_DialogContainer) != null){
            LayoutDialog.instance.closeDialog();
        }
        else if(this.fullView.findViewById(R.id.VIEW_SchInfoScroll) != null){
            ScrollView s = this.fullView.findViewById(R.id.VIEW_SchInfoScroll);
            s.smoothScrollTo(0, 0);
        }
        else if(isSelectMode){
            cancelSelectMode();
        }
        else{
            if(lastBackPressed != null && Duration.between(lastBackPressed, LocalTime.now()).getSeconds() < 1){
                ActivityCompat.finishAffinity(this);
                System.exit(0);
            }
            else{
                Toast.makeText(getApplicationContext(), "한번 더 눌러 종료합니다.", Toast.LENGTH_SHORT).show();
                lastBackPressed = LocalTime.now();
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //카카오 SDK 초기화
        JSONObject obj = JsonController.readJsonObjFromAssets("json/appKey.json", getApplicationContext());
        String kakao_app_key = obj.get("key").toString();
        KakaoSdk.init(this, kakao_app_key);
        ServerController.getInstance();
        
        setContentView(R.layout.layout_main);
        
        context = this.getApplicationContext();
    
        //첫 실행시 권한 요구 화면으로 이동
        checkFirstRun();
        
        //layout_main_schedule_list 화면에 여행 일정 목록 표시
        showScheduleList(findViewById(R.id.LAYOUT_SchListContainer), findViewById(R.id.LAYOUT_FirstSchedule), 0);
        
        //탭 페이지 설정
        settingTabPage();
    
        //스크롤 뷰를 터치를 통해 이동되지 않도록 오버라이드
        findViewById(R.id.VIEW_MainPageTabPage).setOnTouchListener((view, event) -> {return true;});
        
        //권한 허용상태를 메인화면과 연동
        syncPermissionIsChecked();
        
        //로그인 상태일시 프로필 사진 및 이름 동기화
        checkClientHasToken();
        
        //로그인 버튼 클릭시
        findViewById(R.id.BTN_GoLogin).setOnClickListener(viwe -> startActivity(new Intent(this, ActivityLogin.class)));
    
        //로그아웃
        findViewById(R.id.BTN_GoLogout).setOnClickListener(view -> logoutKakao());
        
        //알림 설정
        setAlarmState();
        
        //일정 추천받기
        findViewById(R.id.LAYOUT_MainGotoRecommend).setOnClickListener(view -> {
            Intent i = new Intent(this, ActivityRecommend.class);
            startActivity(i);
        });
        
        //일정 추가하기
        findViewById(R.id.LAYOUT_MainGotoAdd).setOnClickListener(view -> {
            Intent i = new Intent(this, ActivityNewSchedule.class);
            startActivityForResult(i, 1);
        });

        //지금 액티비티의 fullView 가져오기
        this.fullView = PageController.fullView;
//        for (Waypoint Wp : INetTask.getInstance().getWayPoints(""))
//            ScheduleController.getInstance().addWaypointToList(Wp);
    }
    
    @Override
    protected void onAppBarSearchListener(String text){
        super.onAppBarSearchListener(text);
        
        //목록 화면으로 이동
        TabHorizontalScroll(findViewById(R.id.VIEW_MainPageTabPage), 0);
        
        //검색 결과 보여주기

    }
    
    private void setAlarmState(){
        //알림 on/off에 따라 하위 뷰 표시
        View alarmSettings = findViewById(R.id.LAYOUT_AlarmSettings);
        SwitchCompat sAlarm = findViewById(R.id.BTN_SettingAlarm);
        SwitchCompat sDisturbMode = findViewById(R.id.BTN_SettingAlarmDisturbMode);
        SwitchCompat sDday = findViewById(R.id.BTN_SettingAlarmDday);
        SwitchCompat sRecommend = findViewById(R.id.BTN_SettingAlarmRecommend);
        SwitchCompat sFullScreen = findViewById(R.id.BTN_SettingAlarmFullScreen);
    
        //설정 업데이트
        syncSetting();
        
        //설정 값에 따라 동기화
        sAlarm.setChecked(settings.isAlarmOn);
        if(settings.isAlarmOn)
            alarmSettings.setVisibility(View.VISIBLE);
        else
            alarmSettings.setVisibility(View.GONE);
        sDisturbMode.setChecked(settings.isDoNotDisturb);
        sDday.setChecked(settings.isDdayAlarmOn);
        sRecommend.setChecked(settings.isRecommendAlarmOn);
        sFullScreen.setChecked(settings.isFullScreenAlarmOn);
        
        sDisturbMode.setOnClickListener(view -> {
            sDisturbMode.setChecked(
                settings.setDoNotDisturb(
                    !settings.isDoNotDisturb,
                    settings.disturbTimeStart,
                    settings.disturbTimeEnd).isDoNotDisturb);
        });
        sDday.setOnClickListener(view -> {
            sDday.setChecked(
                settings.setDdayAlarmOn(!settings.isDdayAlarmOn).isDdayAlarmOn);
        });
        sRecommend.setOnClickListener(view -> {
            sRecommend.setChecked(
                settings.setRecommendAlarmOn(!settings.isRecommendAlarmOn).isRecommendAlarmOn);
        });
        sFullScreen.setOnClickListener(view -> {
            sFullScreen.setChecked(
                settings.setFullScreenAlarmOn(!settings.isFullScreenAlarmOn).isFullScreenAlarmOn);
        });
        sAlarm.setOnClickListener(view -> {
            if(settings.setAlarmOn(!settings.isAlarmOn).isAlarmOn)
                alarmSettings.setVisibility(View.VISIBLE);
            else
                alarmSettings.setVisibility(View.GONE);
        });
        startJobService();
    }
    
    /***
     * @author 이제경
     *
     *      <p>앱 사용자가 권한을 허용했는지 확인하고</p>
     *      <p>허용되어있지 않은 권한은 사용자 설정 화면에서 다시 설정할 수 있도록 활성화 합니다.</p>
     */
    @SuppressLint("ClickableViewAccessibility")
    private void syncPermissionIsChecked(){
        SwitchCompat swch = findViewById(R.id.SETTING_Storage);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            swch.setChecked(true);
            swch.setOnTouchListener((view, motionEvent) -> {return true;});
        }
        else{
            swch.setOnClickListener(view -> {
                ((SwitchCompat)view).setChecked(false);
                Intent i = new Intent(this, ActivityPermissionCheck.class);
                startActivityForResult(i, 0);
            });
        }
    
        swch = findViewById(R.id.SETTING_Location);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            swch.setChecked(true);
            swch.setOnTouchListener((view, motionEvent) -> {return true;});
        }
        else{
            swch.setOnClickListener(view -> {
                ((SwitchCompat)view).setChecked(false);
                Intent i = new Intent(this, ActivityPermissionCheck.class);
                startActivityForResult(i, 0);
            });
        }
    
        swch = findViewById(R.id.SETTING_Alarm);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED){
            swch.setChecked(true);
            swch.setOnTouchListener((view, motionEvent) -> {return true;});
        }
        else{
            swch.setOnClickListener(view -> {
                ((SwitchCompat)view).setChecked(false);
                Intent i = new Intent(this, ActivityPermissionCheck.class);
                startActivityForResult(i, 0);
            });
        }
    }
    
    /***
     * @author 이제경
     *
     *      탭 페이지 버튼 설정
     */
    private void settingTabPage(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int swidth = size.x;
        
        findViewById(R.id.VIEW_MainPageTabPage).post(new Runnable() {
            @Override
            public void run() {
                ((HorizontalScrollView)findViewById(R.id.VIEW_MainPageTabPage)).setScrollX(swidth);
            }
        });
        
        int ids[] = {R.id.LAYOUT_SchList, R.id.LAYOUT_MainPage, R.id.LAYOUT_Account, R.id.LAYOUT_SearchView};
        int navIds[] = {R.id.LAYOUT_NAVSchList, R.id.LAYOUT_NAVHome, R.id.LAYOUT_NAVAccount};
        for (int i = 0; i < ids.length; i++) {
            //각 페이지의 너비를 화면 크기와 맞춤
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)findViewById(ids[i]).getLayoutParams();
            lp.width = swidth;
            findViewById(ids[i]).setLayoutParams(lp);
        
            //하단의 탭을 눌렀을때의 이벤트 리스너
            if (i < 3){
                findViewById(navIds[i]).setTag(i);
                findViewById(navIds[i]).setOnClickListener(view -> {
                    TabHorizontalScroll(findViewById(R.id.VIEW_MainPageTabPage), (Integer) view.getTag());
                    for (int id: navIds) {
                        ((ImageView)findViewById(id).findViewById(R.id.IMG_NAVicon)).setColorFilter(getResources().getColor(R.color.TEXT_Black));
                        ((TextView)findViewById(id).findViewById(R.id.IMG_NAVtext)).setTextColor(getResources().getColor(R.color.TEXT_Black));
                    }
                    ((ImageView)view.findViewById(R.id.IMG_NAVicon)).setColorFilter(getResources().getColor(R.color.APP_Main));
                    ((TextView)view.findViewById(R.id.IMG_NAVtext)).setTextColor(getResources().getColor(R.color.APP_Main));

                    cancelSelectMode();
                    ResetAppBar();
                });
            }
        }
    }
    
    /***
     * @author 이제경
     *
     *      startActivityForResult 실행시 결과를 받아오는 곳
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {//권한 요구
            if(resultCode == Activity.RESULT_OK){
                syncPermissionIsChecked();
            }
        }
        else if(requestCode == 1) {//권한 요구
            if(resultCode == Activity.RESULT_OK){
                showScheduleList(findViewById(R.id.LAYOUT_SchListContainer), findViewById(R.id.LAYOUT_FirstSchedule), 0);
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        PageController.fullView = this.fullView;
    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}
