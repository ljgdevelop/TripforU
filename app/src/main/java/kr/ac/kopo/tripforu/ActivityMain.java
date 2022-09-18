package kr.ac.kopo.tripforu;

import androidx.annotation.NonNull;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.MotionEvent;
import android.view.View;

import android.content.Context;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends PageController implements OnBackPressedListener{
    @Override protected boolean useToolbar(){ return true; }
    
    static Context context;//this context
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //카카오 SDK 초기화
        JSONObject obj = JsonController.ReadJsonObj("json/appKey.json", getApplicationContext());
        String kakao_app_key = obj.get("key").toString();
        KakaoSdk.init(this, kakao_app_key);
        
        setContentView(R.layout.layout_main);
        
        context = this.getApplicationContext();
        AddPage(new Page(ActivityMain.this, TYPE_ACTIVITY));
    
        //첫 실행시 권한 요구 화면으로 이동
        checkFirstRun();
        
        //Json형식의 데이터 동기화
        ScheduleController.syncJsonToObject(JsonController.ReadJson("json/member.json", getApplicationContext()),
                                            Member.class.toString());
        ScheduleController.syncJsonToObject(JsonController.ReadJson("json/waypoints.json", getApplicationContext()),
                                            Waypoint.class.toString());
        ScheduleController.syncJsonToObject(JsonController.ReadJson("json/schedule.json", getApplicationContext()),
                                            Schedule.class.toString());
        
        //메인 화면의 남은 여행 일정을 표시
        ShowScheduleTickets();
        ShowScheduleList();
        
        //탭 페이지 설정
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int swidth = size.x;
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
            });
        }
    
        //스크롤 뷰를 터치를 통해 이동되지 않도록 오버라이드
        findViewById(R.id.VIEW_MainPageTabPage).setOnTouchListener((view, event) -> {return true;});
        
        //메인 화면의 남은 일정 티켓에 스크롤 애니메이션 적용
        AnimateHorizontalScroll(findViewById(R.id.VIEW_TicketScroll));
        
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
        
        View v = SetAppBarAction(1, false, "완료");
        v.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "완료 버튼 클릭", Toast.LENGTH_SHORT).show();
        });
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 메인 화면에서 설정 내용 속 권한 습득여부를 표시해주는 함수
     * - 습득되어있으면 스위치 버튼을 체크 상태로, 안되어있으면 터치를 통해 해당 화면으로 이동할 수 있도록 유도
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
     * -> 작성자 : 이제경
     * -> 함수 : 메인 페이지에서 남은 여행 일정의 티켓을 보여주는 함수
     * - 클릭시 해당 여행 일정의 상세 내용을 표시
     */
    @SuppressLint("ClickableViewAccessibility")
    private void ShowScheduleTickets(){
        for(int i = 0; i < ScheduleController.remainingSchedule.size(); i ++){
            if(i > 2)
                return;
            Schedule thisSchedule =  ScheduleController.remainingSchedule.get(i);
            int[] scheduleTickets = {R.id.LAYOUT_SchTicket1, R.id.LAYOUT_SchTicket2, R.id.LAYOUT_SchTicket3};
            View ticket = findViewById(scheduleTickets[i]);
            Member member = ScheduleController.GetMemberByID(thisSchedule.GetMemberGroupId());
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ticket.getLayoutParams();
            
            if(ScheduleController.remainingSchedule.size() > i + 1 && i != 2)
                lp.rightMargin = ConvertPXtoDP(getApplicationContext(),15);
            ticket.setLayoutParams(lp);
            
            ticket.setOnTouchListener((view, event) -> {
                Log.d("TAG", "r: " + event);
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ShowScheduleInfo(thisSchedule);
                }
                return false;
            });
            
            ticket.setVisibility(View.VISIBLE);
            
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Title)).setText(thisSchedule.GetName());
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Course)).setText(thisSchedule.GetDestination());
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Count)).setText(member.GetUserIdList().size() + "명");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Days)).setText(thisSchedule.GetDays() + "일");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Date)).setText(getDateDay(thisSchedule.GetStartDate()));
        }
    }
    
    public void doSomthing(){
    
    }
    
    public void ShowScheduleList() {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayout layout_SharedMark = findViewById(R.id.LAYOUT_SharedMark);
        LinearLayout layout_SharedContents = findViewById(R.id.LAYOUT_SharedContents);
        ScheduleContentAdapter mScheduleContentAdapter = new ScheduleContentAdapter();
        RecyclerView mRecyclerView_Schedule = (RecyclerView) findViewById(R.id.RECYCLEVIEW_Schedule);
        mRecyclerView_Schedule.setLayoutManager(manager);
        mRecyclerView_Schedule.setAdapter(mScheduleContentAdapter);
        
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mScheduleContentAdapter));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView_Schedule);
        
        
        // isShared 상태에 맞게 공유표시 레이아웃 생기고 끄는 기능 구현해야됨
        
        mScheduleContentAdapter.setItems();
    }
    
    /**
     *
     */
    private String getDateDay(String date){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat nDate = new SimpleDateFormat("EEE, MM. dd");
            Date formatDate = dateFormat.parse(date);
            String strNewDtFormat = nDate.format(formatDate);
            return strNewDtFormat;
        }catch (Exception e){
            Log.e("TAG", "getDateDay: ", e);
            return "";
        }
    }
    
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
