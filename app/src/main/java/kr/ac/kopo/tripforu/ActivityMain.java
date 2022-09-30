package kr.ac.kopo.tripforu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import org.json.simple.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ActivityMain extends PageController implements OnBackPressedListener{
    @Override protected boolean useToolbar(){ return true; }
    
    static Context context;//this context
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //카카오 SDK 초기화
        JSONObject obj = JsonController.readJsonObjFromAssets("json/appKey.json", getApplicationContext());
        String kakao_app_key = obj.get("key").toString();
        KakaoSdk.init(this, kakao_app_key);
        
        setContentView(R.layout.layout_main);
        
        context = this.getApplicationContext();
        AddPage(new Page(ActivityMain.this, TYPE_ACTIVITY));
    
        //첫 실행시 권한 요구 화면으로 이동
        checkFirstRun();
        
        //layout_main_schedule_list 화면에 여행 일정 목록 표시
        ShowScheduleList();
        
        //탭 페이지 설정
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
        int ids[] = {R.id.LAYOUT_SchList, R.id.LAYOUT_MainPage, R.id.LAYOUT_Account};
        int navIds[] = {R.id.LAYOUT_NAVSchList, R.id.LAYOUT_NAVHome, R.id.LAYOUT_NAVAccount};
        for (int i = 0; i < ids.length; i++) {
            //각 페이지의 너비를 화면 크기와 맞춤
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)findViewById(ids[i]).getLayoutParams();
            lp.width = swidth;
            findViewById(ids[i]).setLayoutParams(lp);
            
            //하단의 탭을 눌렀을때의 이벤트 리스너
            findViewById(navIds[i]).setTag(i);
            findViewById(navIds[i]).setOnClickListener(view -> {
                TabHorizontalScroll(findViewById(R.id.VIEW_MainPageTabPage), (Integer) view.getTag());
                for (int id: navIds) {
                    ((ImageView)findViewById(id).findViewById(R.id.IMG_NAVicon)).setColorFilter(getResources().getColor(R.color.TEXT_Black));
                    ((TextView)findViewById(id).findViewById(R.id.IMG_NAVtext)).setTextColor(getResources().getColor(R.color.TEXT_Black));
                }
                ((ImageView)view.findViewById(R.id.IMG_NAVicon)).setColorFilter(getResources().getColor(R.color.APP_Main));
                ((TextView)view.findViewById(R.id.IMG_NAVtext)).setTextColor(getResources().getColor(R.color.APP_Main));
            });
        }
    
        //스크롤 뷰를 터치를 통해 이동되지 않도록 오버라이드
        findViewById(R.id.VIEW_MainPageTabPage).setOnTouchListener((view, event) -> {return true;});
        
        //권한 허용상태를 메인화면과 연동
        SyncPermissionIsChecked();
        
        //로그인 상태일시 프로필 사진 및 이름 동기화
        CheckClientHasToken();
        
        //로그인 버튼 클릭시
        findViewById(R.id.BTN_GoLogin).setOnClickListener(viwe -> {
            Intent i = new Intent(this, ActivityLogin.class);
            startActivity(i);
        });
    
        //로그아웃
        findViewById(R.id.BTN_GoLogout).setOnClickListener(view -> {
            UserApiClient.getInstance().logout(throwable -> {
                if(throwable == null){
                    Intent i = new Intent(this, ActivityMain.class);
                    finish();
                    startActivity(i);
                }
                return null;
            });
        });
        
        //설정 - 알림 온 오프시
        findViewById(R.id.BTN_SettingAlarm).setOnClickListener(view -> {
            View alarmSettings = findViewById(R.id.LAYOUT_AlarmSettings);
            if(alarmSettings.getVisibility() == View.VISIBLE) {
                findViewById(R.id.LAYOUT_AlarmSettings).setVisibility(View.GONE);
            }
            else{
                findViewById(R.id.LAYOUT_AlarmSettings).setVisibility(View.VISIBLE);
            }
        });
    
        
        LayoutDialog dialog = new LayoutDialog(getApplicationContext());
        dialog.setDialogTitle("제목 영역");
        dialog.setDialogMessage("본문 영역");
        dialog.addButton(R.color.TEXT_Gray, "닫기").setOnClickListener(view -> dialog.closeDialog());
        dialog.addButton(R.color.TEXT_Black, "확인1");
        dialog.addButton(R.color.APP_Main, "확인2");
    }
    
    /***
     * @author 이제경
     *
     *      <p>앱 사용자가 권한을 허용했는지 확인하고</p>
     *      <p>허용되어있지 않은 권한은 사용자 설정 화면에서 다시 설정할 수 있도록 활성화 합니다.</p>
     */
    @SuppressLint("ClickableViewAccessibility")
    private void SyncPermissionIsChecked(){
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
     *      목록 화면에 여행 일정들을 표시합니다.
     */
    boolean isSelectMode = false;
    private void ShowScheduleList(){
        LinearLayout container = findViewById(R.id.LAYOUT_SchListContainer);
        container.removeAllViewsInLayout();
        int thisYear = 9999;
        for (Schedule sch:ScheduleController.getSortedScheduleByDate()) {
            LayoutScheduleTicket newTicket = new LayoutScheduleTicket(getApplicationContext());
            newTicket.setScheduleId(sch.GetId());
            container.addView(newTicket);
            
            //년도가 바뀔때마나 표시
            int scheduleYear = Integer.parseInt(sch.GetStartDate().split("-")[0]);
            if(thisYear > scheduleYear){
                thisYear = scheduleYear;
                newTicket.findViewById(R.id.TEXT_TicketDateHeader).setVisibility(View.VISIBLE);
                ((TextView)newTicket.findViewById(R.id.TEXT_TicketDateHeader)).setText(thisYear + "년");
            }
            
            //길게 터치시 선택모드 진입
            newTicket.setOnLongClickListener(view -> {
                if(!isSelectMode) {
                    isSelectMode = true;
                    setTagToView(container, "isSelectMode", true);
                    setTagToView(newTicket, "isSelected", true);
                    newTicket.findViewById(R.id.LAYOUT_TicketBG).setBackground(getResources().getDrawable(R.drawable.background_ticket_selected));
                    
                    //선택모드 진입 시 취소, 삭제 버튼 출력
                    SetAppBarAction(0, true, "취소").setOnClickListener(v -> {
                        setTagToView(container, "isSelectMode", false);
                        isSelectMode = false;
                        for (int i = 0; i < container.getChildCount(); i ++) {
                            setTagToView(container.getChildAt(i), "isSelected", false);
                            container.getChildAt(i).findViewById(R.id.LAYOUT_TicketBG).setBackground(getResources().getDrawable(R.drawable.background_ticket_new));
                        }
                        ResetAppBar();
                    });
                    SetAppBarAction(2, false, "삭제").setOnClickListener(v -> {
                        setTagToView(container, "isSelectMode", false);
                        isSelectMode = false;
                        for (int i = container.getChildCount() - 1; i >= 0; i--) {
                            if(getTagFromView(container.getChildAt(i), "isSelected").equals("true")){
                                ScheduleController.removeScheduleById(((LayoutScheduleTicket)container.getChildAt(i)).getScheduleId(), getApplicationContext());
                            }
                        }
                        container.removeAllViewsInLayout();
                        ShowScheduleList();
                        ResetAppBar();
                    });
                }
                return true;
            });
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
                SyncPermissionIsChecked();
            }
        }
    }
}
