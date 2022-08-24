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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ControllerSchedule extends Activity{
    public static HashMap<Integer, Schedule> scheduleDictionary = new HashMap<>();
    public static ArrayList<Schedule> remainingSchedule = new ArrayList<>();
    public static ArrayList<Member> memberList = new ArrayList<>();
    public static ArrayList<Waypoint> waypointList = new ArrayList<>();
    public static LayoutInflater inflater;
    static int wpc = 10000000;
    
    public final static int IC_HOME = 1;
    public final static int IC_CIRCLE = 2;
    public final static int IC_SUBWAY = 3;
    public final static int IC_TRAIN = 4;
    public final static int IC_CAR = 5;
    public final static int IC_HOTEL = 6;
    public final static int IC_RESTAURANT = 7;
    public final static int IC_PIN = 8;
    
    public static void AddScheduleFromJSON(String json){
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("schedules");
            
            JSONParser parser = new JSONParser();
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jsonObject = (JSONObject)jArray.get(i);
    
                Schedule sch = new Schedule(jsonObject.get("id"), jsonObject.get("name"),
                    jsonObject.get("destination"), jsonObject.get("days"), jsonObject.get("startDate"),
                    jsonObject.get("wayPointList"), jsonObject.get("memberGroupId"));
                
                remainingSchedule.add(sch);
            }
        } catch (JSONException e) {
            Log.d("TAG", "AddScheduleFromJSON: " + e);
            e.printStackTrace();
        }
    }
    
    public static void AddMemberFromJSON(String json){
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("members");
            
            JSONParser parser = new JSONParser();
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jsonObject = (JSONObject)jArray.get(i);
                
                Member member = new Member(jsonObject.get("id"), jsonObject.get("scheduleId"));
                
                JSONArray userIdList = (JSONArray)jsonObject.get("userIdList");
                if(userIdList.length() > 0){
                    for(int j = 0; j < userIdList.length(); j++){
                        JSONObject userId = (JSONObject) userIdList.get(j);
                        
                        member.AddUserIdInList((int)userId.get("id"));
                    }
                }
                
                memberList.add(member);
            }
        } catch (JSONException e) {
            Log.d("TAG", "AddScheduleFromJSON: " + e);
            e.printStackTrace();
        }
    }
    
    public static void AddWaypointFromJSON(String json){
        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("waypoints");
            
            JSONParser parser = new JSONParser();
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jsonObject = (JSONObject)jArray.get(i);
                
                Waypoint wp = new Waypoint(jsonObject.get("id"), jsonObject.get("name"),
                    jsonObject.get("posX"), jsonObject.get("posY"), jsonObject.get("reviewCount"),
                    jsonObject.get("type"), jsonObject.get("rating"), jsonObject.get("originLink"),
                    jsonObject.get("time"));
                
                waypointList.add(wp);
                Log.e("TAG",  ControllerSchedule.waypointList.size() + "");
            }
        } catch (JSONException e) {
            Log.d("TAG", "AddScheduleFromJSON: " + e);
            e.printStackTrace();
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
        Member member = ControllerSchedule.GetMemberByID(schedule.GetMemberGroupId());
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

class Memo {
    int id;
    String title;
    String memoText;
    boolean isEditing;
}

class Waypoint{
    private int id;
    private String name;
    private double posX;
    private double posY;
    private byte rating;
    private int reviewCount;
    private int type;
    private String originLink;
    private int time;
    
    public Waypoint(int id, String name, double posX, double posY, byte rating, int reviewCount,
                    int type, String originLink, int time){
        this.id = id;
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.type = type;
        this.originLink = originLink;
        this.time = time;
    }
    //생성자 : JSON Object 입력
    public Waypoint(Object id, Object name ,Object posX, Object posY, Object reviewCount, Object type,
                    Object rating, Object originalLink, Object time){
        this.id = (int)id;
        this.name = (String)name;
        this.posX = (double)posX;
        this.posY = (double)posY;
        this.reviewCount = (int)reviewCount;
        this.type = (int)type;
        this.rating = (byte) ((int)rating);
        this.originLink = (String)originalLink;
        this.time = (int)time;
    }
    
    public int GetId(){
        return this.id;
    }
    public void SetId(int id) {
        this.id = id;
    }
    
    public String GetName(){
        return this.name;
    }
    public void SetName(String name) {
        this.name = name;
    }
    
    public double GetPosX(){
        return this.posX;
    }
    public void SetPosX(double posX){
        this.posX = posX;
    }
    
    public double GetPosY(){
        return this.posY;
    }
    public void SetPosY(double posY){
        this.posY = posY;
    }
    
    public byte GetRating(){
        return rating;
    }
    public void SetRating(byte rating){
        this.rating = rating;
    }
    public void AddRating(byte rating){
        this.rating = (byte)((this.rating * this.reviewCount + rating) / this.reviewCount);
    }
    
    public int GetReviewCount(){
        return this.reviewCount;
    }
    public void SetReviewCount(int reviewCount){
        this.reviewCount = reviewCount;
    }
    
    public int GetType(){
        return this.type;
    }
    public void SetType(int type){
        this.type = type;
    }
    
    public String GetOriginalLink(){
        return originLink;
    }
    public void SetOriginalLink(String originLink){
        this.originLink = originLink;
    }
    
    public int GetTime(){
        return this.time;
    }
    public void SetTime(int minuteTime){
        this.time = minuteTime;
    }
}

class Member{
    private int id;
    private ArrayList<Integer> userIdList = new ArrayList<>();
    private ArrayList<Memo> memoList = new ArrayList<>();
    private int scheduleId;
    
    //생성자
    public Member(int id, int scheduleId){
        this.id = id;
        this.userIdList = new ArrayList<Integer>();
        this.memoList = new ArrayList<Memo>();
        this.scheduleId = scheduleId;
    }
    //생성자 : JSON Object 입력
    public Member(Object id, Object scheduleId){
        this.id = (int)id;
        this.userIdList = new ArrayList<Integer>();
        this.memoList = new ArrayList<Memo>();
        this.scheduleId = (int)scheduleId;
    }
    
    public int GetId(){
        return id;
    }
    
    public ArrayList<Integer> GetUserIdList(){
        return userIdList;
    }
    public void AddUserIdInList(int id){
        this.userIdList.add(id);
    }
    public void RemoveUserIdInList(int id){
        for(int i = 0; i < userIdList.size(); i ++){
            if(this.userIdList.get(i) == id)
                this.userIdList.remove(i);
        }
    }
    
    public ArrayList<Memo> GetMemoList(){
        return memoList;
    }
    
    public int GetScheduleId(){
        return scheduleId;
    }
}

class Schedule{
    private int id;
    private String name;
    private String destination;
    private int days;
    private String startDate;
    private ArrayList<Waypoint> wayPointList = new ArrayList<>();
    private int memberGroupId;
    private byte likes;
    private boolean isShared;
    private int sharedCount;
    
    //생성자 : wayPointList 포함
    public Schedule(int id, String name, String destination, int days, String startDate,
                         ArrayList<Waypoint> wayPointList, int memberGroupId){
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.days = days;
        this.startDate = startDate;
        this.wayPointList = wayPointList;
        this.memberGroupId = memberGroupId;
        this.likes = 0;
        this.isShared = false;
        this.sharedCount = 0;
    }
    //생성자 : wayPointList 미포함
    public Schedule(int id, String name, String destination, int days, String startDate,
                         int memberGroupId){
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.days = days;
        this.startDate = startDate;
        this.memberGroupId = memberGroupId;
        this.likes = 0;
        this.isShared = false;
        this.sharedCount = 0;
    }
    //생성자 : JSON Object 입력
    public Schedule(Object id, Object name, Object destination, Object days, Object startDate,
                    Object wayPointString, Object memberGroupId){
        this.id = (int)id;
        this.name = (String)name;
        this.destination = (String)destination;
        this.days = (int)days;
        this.startDate = (String)startDate;
        String[] wpStrSplitted = wayPointString.toString().split("/");
        for (String wpId: wpStrSplitted) {
            for (Waypoint wp:ControllerSchedule.waypointList) {
                if(wp.GetId() == Integer.parseInt(wpId)){
                    this.wayPointList.add(wp);
                }
            }
        }
        this.memberGroupId = (int)memberGroupId;
        this.likes = 0;
        this.isShared = false;
        this.sharedCount = 0;
    }
    
    //id 변수의 getter setter
    public int GetId(){
        return this.id;
    }
    
    //name 변수의 getter setter
    public String GetName(){
        return this.name;
    }
    public void SetName(String name){
        this.name = name;
    }
    
    //destination 변수의 getter setter
    public String GetDestination(){
        return this.destination;
    }
    public void SetDestination(String destination){
        this.destination = destination;
    }
    
    //days 변수의 getter setter
    public int GetDays(){
        return this.days;
    }
    public void SetDays(byte days){
        this.days = days;
    }
    public void SetDays(int days){
        this.days = (byte)days;
    }
    
    //startDate 변수의 getter setter
    public String GetStartDate(){
        return this.startDate;
    }
    public void SetStartDate(){
        this.startDate = startDate;
    }
    
    //wayPointId 변수의 getter setter adder remover
    public ArrayList<Waypoint> GetWayPointList(){
        return this.wayPointList;
    }
    public Waypoint GetWayPointFromId(int i){
        return this.wayPointList.get(i);
    }
    public void AddWayPoint(Waypoint waypoint){
        this.wayPointList.add(waypoint);
    }
    public void RemoveWayPointById(int id){
        for(int i = 0; i < wayPointList.size(); i ++){
            if(this.wayPointList.get(i).GetId() == id)
                this.wayPointList.remove(i);
        }
    }
    public void ClearWayPoint(){
        this.wayPointList.clear();
    }
    
    //memberGroup 변수의 getter setter
    public int GetMemberGroupId(){
        return this.memberGroupId;
    }
    public void SetMemberGroupId(int id){
        this.memberGroupId =  id;
    }
    
    //likes 변수의 getter setter
    public double GetLikes(){ return (this.likes/10); }
    public void SetLikes(double value){ this.likes = (byte)(value*10); }
    
    //isShared 변수의 getter setter
    public boolean CheckIsShared(){ return this.isShared; }
    public void SetSharedState(boolean isShared){ this.isShared = isShared; }
    
    //sharedCount 변수의 getter setter adder
    public int GetSharedCount(){ return this.sharedCount; }
    public void AddSharedCount(int i){ this.sharedCount++; }
    public void SetSharedCount(int i){ this.sharedCount = i; }
}
