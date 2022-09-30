package kr.ac.kopo.tripforu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ScheduleTicket extends FrameLayout {
    private Context context;
    private int scheduleId;//여행 일정의 ID
    public void setScheduleId(int id){this.scheduleId = id;}
    public int getScheduleId(){return scheduleId;}
    
    /**
     * @author 이제경
     * @param context
     * @param attrs
     * @param defStyleAttr
     *
     *      커스텀 뷰의 생성자
     */
    public ScheduleTicket(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize(context);
    }
    public ScheduleTicket(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ScheduleTicket(Context context){
        this(context, null);
    }
    
    
    /***
     * @author 이제경
     *      커스텀 뷰(ScheduleTicket)가 생성되었을때 작동합니다.
     */
    private void initalize(Context context){
        this.context = context;
        View fullView = inflate(getContext(), R.layout.layout_scheduleticket, this);
        fullView.setOnClickListener(this::OnTouch);
        fullView.post(this::SyncTicketText);
    }
    
    private void OnTouch(View view){
        if(PageController.getTagFromView(this.getParent(), "isSelectMode").equals("true")) {
            PageController.setTagToView(this, "isSelected", !PageController.getTagFromView(this, "isSelected").equals("true"));
            Log.d("TAG", "OnTouch: isSelected: " + PageController.getTagFromView(this, "isSelected").equals("true"));
            if(PageController.getTagFromView(this, "isSelected").equals("true"))
                this.findViewById(R.id.LAYOUT_TicketBG).setBackground(getResources().getDrawable(R.drawable.background_ticket_selected));
            else
                this.findViewById(R.id.LAYOUT_TicketBG).setBackground(getResources().getDrawable(R.drawable.background_ticket_new));
        }
        else
            ShowScheduleInfo(context, ScheduleController.scheduleDictionary.get(scheduleId));
    }
    
    /***
     * @author 이제경
     * 커스텀 뷰(ScheduleTicket)에 일정 정보를 대입합니다.
     */
    public void SyncTicketText(){
        try {
            Schedule schedule = ScheduleController.scheduleDictionary.get(scheduleId);
            if (schedule == null)
                return;
            
            if(schedule.CheckIsShared())
                findViewById(R.id.IMG_IsSharedIcon).setVisibility(VISIBLE);
            
            ((TextView)findViewById(R.id.TEXT_TicketTitle)).setText(schedule.GetName());
            
            ((TextView)findViewById(R.id.TEXT_TicketStartDate)).setText(getFormattedDate(schedule.GetStartDate(), "MM. dd"));
        
            ((TextView)findViewById(R.id.TEXT_TicketDateDelta)).setText(String.format("%dD", schedule.GetDays()));
            
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = transFormat.parse(schedule.GetStartDate());
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(startDate);
            endDate.add(Calendar.DATE, schedule.GetDays());
            StringBuilder formattedDate = new StringBuilder();
            formattedDate.append(endDate.get(Calendar.MONTH) + 1);
            formattedDate.append(". ");
            formattedDate.append(endDate.get(Calendar.DATE));
            ((TextView)findViewById(R.id.TEXT_TicketEndDate)).setText(formattedDate);
        }catch (Exception e){ Log.e("TAG", "syncTicketText: ", e); }
    }
    
    /**
     * @author 이제경
     * @param schedule - 보여질 일정 정보를 담고있는 Schedule 객체
     * @return 화면에 추가된 View
     *
     *      일정의 세부내용을 보여주는 Schedule Info의 내용을 동기화 합니다.
     */
    @SuppressLint("ClickableViewAccessibility")
    public View ShowScheduleInfo(Context context, Schedule schedule){
        //뷰를 화면 상단(Z축)에 복제
        FrameLayout fullView = PageController.fullView;
        View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
            inflate(R.layout.layout_scheduleinfo, fullView, false).findViewById(R.id.LAYOUT_SchInfo);
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
        LinearLayout layout_schTicketMid = view.findViewById(R.id.LAYOUT_SchInfoMiddle);
        AppCompatButton btn_schInfoShare = view.findViewById(R.id.BTN_SchInfoShare);
        
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
        else{
            txt_schUpperLoc.setText("대한민국");
            txt_schSpecLoc.setText(Destination);
        }
        String schStartDate = getFormattedDate(schedule.GetStartDate(), "MM. dd, EEE");
        String schDays = schedule.GetDays() + "";
        Member member = ScheduleController.getMemberByID(schedule.GetMemberGroupId());
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
        
        //적정 위치로 스크롤
        layout_schTicketMid.post(() -> {
            int targetY = layout_schTicketMid.getBottom() - view.getHeight();
            layout_schScroll.smoothScrollBy(0, targetY);
        
            layout_schScroll.setOnScrollChangeListener((scroll, x, y, dx, dy) -> {
                try{
                    int vy = layout_schTicketMid.getBottom() - view.getHeight();
                    Log.d("TAG", "scrollV: " + scroll.getTag());
                    //티켓 상단부에서 한번 걸침
                    if(Math.abs(dy - vy) < 1)
                        scroll.setTag("onTop");
                    
                    if(dy < vy && (scroll.getTag() == "scrolled" || scroll.getTag() == "init"))
                        ((ScrollView) scroll).smoothScrollTo(0, vy);
                    else if(dy > vy && scroll.getTag() == "onTop")
                        scroll.setTag("scrolled");
                    
                    
                    //걸친 상태에서 더 스크롤 할시 삭제
                    if (dy < vy - 2 && (scroll.getTag() == "onTop")) {
                        scroll.setTag("remove");
                        ((ScrollView) scroll).smoothScrollTo(0, 0);
                    }
                    else if (y < 10 && scroll.getTag() == "remove") {
                        scroll.setTag("removed");
                        ((ViewGroup) fullView).removeView(view);
                        PageController.PopPage();
                    }
                }catch(Exception e){
                    Log.e("TAG", "ShowScheduleInfo: ", e);
                }
            });
        });
        
        //공유하기 버튼 클릭시
        btn_schInfoShare.setOnClickListener(button -> {
            Intent i = new Intent(getContext(), ActivityUserShare.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("putSchedule", schedule);
            context.startActivity(i);
        });
        
        //뒤로기기키로 닫기 위해 페이지 등록
        PageController.AddPage(new Page(view, PageController.TYPE_VIEW));
        
        return view;
    }
    
    /***
     * @author 이제경
     *
     *      ShowScheduleInfo <- 실행 중 관광지 정보를 추가하는 코드
     */
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
    
    
    /***
     * @author 이제경
     * @param date - 변환 할 날짜 값("0000-00-00")
     * @param pattern - 원하는 날짜 형식
     */
    public String getFormattedDate(String date, String pattern){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat nDate = new SimpleDateFormat(pattern);
            Date formatDate = dateFormat.parse(date);
            String strNewDtFormat = nDate.format(formatDate);
            return strNewDtFormat;
        }catch (Exception e){
            Log.e("TAG", "getFormattedDate: ", e);
            return "";
        }
    }
}
