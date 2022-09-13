package kr.ac.kopo.tripforu;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kakao.sdk.common.KakaoSdk;

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
        
        //
        findViewById(R.id.BTN_CreateNewSch).setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), ActivityCreateSch.class);
            startActivity(i);
        });
    }
    
    
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 메인 페이지에서 남은 여행 일정의 티켓을 보여주는 함수
     * - 클릭시 해당 여행 일정의 상세 내용을 표시
     */
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
            
            ticket.setVisibility(View.VISIBLE);
            
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Title)).setText(thisSchedule.GetName());
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Course)).setText(thisSchedule.GetDestination());
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Count)).setText(member.GetUserIdList().size() + "명");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Days)).setText(thisSchedule.GetDays() + "일");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Date)).setText(getDateDay(thisSchedule.GetStartDate()));
        }
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat nDate = new SimpleDateFormat("EEE, MM. dd");
            Date formatDate = dateFormat.parse(date);
            String strNewDtFormat = nDate.format(formatDate);
            return strNewDtFormat;
        }catch (Exception e){
            Log.e("TAG", "getDateDay: ", e);
            return "";
        }
    }
}
