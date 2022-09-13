package kr.ac.kopo.tripforu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import org.json.simple.JSONObject;

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
    }
    
    
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 메인 페이지에서 남은 여행 일정의 티켓을 보여주는 함수
     * - 클릭시 해당 여행 일정의 상세 내용을 표시
     */
    private void ShowScheduleTickets(){
        for(int i = 0; i < ScheduleController.remainingSchedule.size(); i ++){
            if(i > 2)
                break;
            Schedule thisSchedule =  ScheduleController.remainingSchedule.get(i);
            int[] scheduleTickets = {R.id.LAYOUT_SchTicket1, R.id.LAYOUT_SchTicket2, R.id.LAYOUT_SchTicket3};
            View ticket = findViewById(scheduleTickets[i]);
            Member member = ScheduleController.GetMemberByID(thisSchedule.GetMemberGroupId());
            
            ticket.setVisibility(View.VISIBLE);
            
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Title)).setText(thisSchedule.GetName());
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Course)).setText("여행 코스");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Count)).setText(member.GetUserIdList().size() + "명");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Days)).setText(thisSchedule.GetDays() + "일");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Date)).setText(thisSchedule.GetStartDate() + "일 출발");
            
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
    
    public static int ConvertSPtoPX(@NonNull Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    
    public static int ConvertDPtoPX(@NonNull Context context, int dp) {
        return Math.round((float) dp * context.getResources().getDisplayMetrics().density);
    }
}
