package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScheduleController extends Activity{
    public static HashMap<Integer, Schedule> scheduleDictionary = new HashMap<>();
    public static ArrayList<Schedule> remainingSchedule = new ArrayList<>();
    public static ArrayList<Member> memberList = new ArrayList<>();
    public static ArrayList<Waypoint> waypointList = new ArrayList<>();
    public static LayoutInflater inflater;
    static int wpc = 10000000;
    
    public final int IC_HOME = 1;
    public final static int IC_CIRCLE = 2;
    public final static int IC_SUBWAY = 3;
    public final static int IC_TRAIN = 4;
    public final static int IC_CAR = 5;
    public final static int IC_HOTEL = 6;
    public final static int IC_RESTAURANT = 7;
    public final static int IC_PIN = 8;
    
    public static void syncJsonToObject(org.json.simple.JSONArray json, String cls){
        Gson gson = new Gson();
        ArrayList<Object> newObj;
        System.out.println(cls);
        switch (cls){
            case "class kr.ac.kopo.tripforu.Waypoint":
                
                System.out.println("Waypoint");
                newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Waypoint>>(){}.getType());
                for (int i = 0; i < newObj.size(); i++)
                    waypointList.add((Waypoint)newObj.get(i));
                break;
            case "class kr.ac.kopo.tripforu.Member":
    
                System.out.println("Member");
                newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Member>>(){}.getType());
                for (int i = 0; i < newObj.size(); i++) {
                    memberList.add((Member) newObj.get(i));
                }
                break;
            case "class kr.ac.kopo.tripforu.Schedule":
    
                System.out.println("Schedule");
                newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Schedule>>(){}.getType());
                for (int i = 0; i < newObj.size(); i++) {
                    remainingSchedule.add((Schedule)newObj.get(i));
                }
                break;
        }
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 일정의 세부내용을 보여주는 Schedule Info의 내용을 동기화 합니다.
     * -> 인자 : parent = view가 들어가게 될 부모 뷰(FrameLayout)
     * -> 인자 : schedule = 스케쥴 정보를 가지고있는 schedule 객체
     * -> 반환 : 화면에 추가된 View
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static View ShowScheduleInfo(FrameLayout parent, Schedule schedule){
        View view = View.inflate(ActivityMain.context, R.layout.layout_scheduleinfo, (ViewGroup) parent);
        
        TextView txt_schName = view.findViewById(R.id.TEXT_SchInfoName); //일정의 제목
        TextView txt_schStartDate = view.findViewById(R.id.TEXT_SchInfoStartDate);//여행 출발일
        TextView txt_schDays = view.findViewById(R.id.TEXT_SchInfoEndDate);//숙박일 수
        TextView txt_schCompanyNum = view.findViewById(R.id.TEXT_SchInfoNum);//일정의 참여자 수
    
        String schName = schedule.GetName();
        String schStartDate = schedule.GetStartDate();
        schStartDate = schStartDate.split("-")[1] + ". " + schStartDate.split("-")[2];
        String schDays = schedule.GetDays() + "일";
        Member member = ScheduleController.GetMemberByID(schedule.GetMemberGroupId());
        String schCompanyNum = member.GetUserIdList().size() + "명";
    
        txt_schName.setText(schName);
        txt_schStartDate.setText(schStartDate);
        txt_schDays.setText(schDays);
        txt_schCompanyNum.setText(schCompanyNum);
    
        view.setVisibility(View.VISIBLE);
    
        AddWaypointInfo(view, schedule);
        
        return view;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void AddWaypointInfo(View view, Schedule schedule){
        final LinearLayout wpContainer = view.findViewById(R.id.VIEW_SchInfoWPContainer);
        schedule.GetWayPointList().forEach(waypoint -> {
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
        });
    }
    
    
    public static Member GetMemberByID(int id){
        for(int i = 0; i < memberList.size(); i ++){
            if(memberList.get(i).GetId() == id)
                return memberList.get(i);
        }
        return null;
    }
}



