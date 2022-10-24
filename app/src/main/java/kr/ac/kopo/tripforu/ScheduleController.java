package kr.ac.kopo.tripforu;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ScheduleController extends Application {
    
    // singleton 객체
    private static ScheduleController instance;
    private Context context;
    
    private HashMap<Integer, Schedule> scheduleDictionary = new HashMap<>();
    private HashMap<Integer, Member> memberList = new HashMap<>();
    private HashMap<Integer, Waypoint> waypointList = new HashMap<>();
    private HashMap<Long, String[]> ownerList = new HashMap<>();
    private ArrayList<SharedSchedule> sharedSchedules = new ArrayList<>();
    
    private ScheduleController(){
        instance = this;
        context = ActivityMain.context;
    }
    
    public static ScheduleController getInstance(){
        if(instance == null){
            return new ScheduleController();
        }else
            return instance;
    }
    
    
    /***
     * @author 이제경
     *
     *      ScheduleDictionary에 저장된 Schedule 객체를
     *      여행 시작일을 기준으로 정렬(내림차순)하여 가져옵니다.
     */
    public static ArrayList<Schedule> getSortedScheduleByDate(){
        try {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
    
            Collection<Schedule> tempArray = getInstance().getAllScheduleValue();
            ArrayList<Schedule> result = new ArrayList<>();
    
            //버블 정렬
            for (Schedule sch : tempArray) {
                //기준점을 위해 첫 Schedule 객체 삽입 후 continue
                if (result.size() == 0) {
                    result.add(sch);
                    continue;
                }
                //버블 삽입
                for (int i = 0; i < result.size(); i++) {
                    Date compareDate = transFormat.parse(result.get(i).getStartDate());
                    Date newSDate = transFormat.parse(sch.getStartDate());
                    if (compareDate.before(newSDate)) {
                        result.add(i, sch);
                        break;
                    } else if (i == result.size() - 1) {
                        result.add(sch);
                        break;
                    }
                }
            }
    
            return result;
        }catch (Exception e){
            Log.e("TAG", "getSortedScheduleByDate: ", e);
            return new ArrayList<>();
        }
    }
    
    /***
     * @author 이제경
     * @param json - 데이터가 담긴 json
     * @param  className - 데이터를 담을 객체
     *
     *      Json파일을 읽어와 객체로 저장합니다.
     */
    public static void syncJsonToObject(org.json.simple.JSONArray json, String className){
        try {
            Gson gson = new Gson();
            ArrayList<Object> newObj;
            switch (className) {
                case "class kr.ac.kopo.tripforu.Waypoint":
                    newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Waypoint>>() {
                    }.getType());
                    for (int i = 0; i < newObj.size(); i++)
                        getInstance().addWaypointToList((Waypoint) newObj.get(i));
                    break;
                    
                case "class kr.ac.kopo.tripforu.Member":
                    newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Member>>() {
                    }.getType());
                    for (int i = 0; i < newObj.size(); i++) {
                        getInstance().addMemberToList((Member) newObj.get(i));
                    }
                    break;
                    
                case "class kr.ac.kopo.tripforu.Schedule":
                    newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Schedule>>() {}.getType());
                    for (int i = 0; i < newObj.size(); i++) {
                        Schedule newSch = (Schedule) newObj.get(i);
                        getInstance().addScheduleToDictionary(newSch);
                    }
                    break;
                    
                case "class kr.ac.kopo.tripforu.SharedSchedule":
                    newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<SharedSchedule>>() {}.getType());
                    for (int i = 0; i < newObj.size(); i++) {
                        getInstance().addSharedSchedule((SharedSchedule) newObj.get(i));
                    }
                    break;
                    
                default:
                    break;
            }
        }catch (Exception e) {
            Log.e("", "syncJsonToObject: ", e);
        }
    }
    
    /***
     * @author 이제경
     *
     *      ShowScheduleInfo <- 실행 중 관광지 정보를 추가하는 코드
     */
    public int getWayPointIcon(Schedule schedule, int index){
        int imgSrc = -1;
        switch (schedule.getWayPointList().get(index).GetType()){
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
        return imgSrc;
    }
    
    public int getWayPointIcon(Waypoint waypoint){
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
        return imgSrc;
    }
    
    public Drawable getWayPointDrawable(Waypoint waypoint){
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
        return context.getResources().getDrawable(imgSrc);
    }
    
    public Member getMemberByID(int id){
        for(int i = 0; i < getInstance().getAllMemberValues().size(); i ++){
            if(getInstance().getAllMemberValues().get(i).GetId() == id)
                return getInstance().getAllMemberValues().get(i);
        }
        return null;
    }
    
    
    public ArrayList<Schedule> getAllScheduleValue(){
        return new ArrayList<>(getInstance().scheduleDictionary.values());
    }
    public Schedule getScheduleById(int id){
        return getInstance().scheduleDictionary.get(id);
    }
    public HashMap<Integer, Schedule> getAllSchedule(){
        return getInstance().scheduleDictionary;
    }
    public void removeScheduleById(int id){
        getInstance().scheduleDictionary.remove(id);
        JsonController.saveJson(getAllScheduleValue(), "schedule", context);
    }
    public void removeScheduleBySchedule(Schedule schedule){
        getInstance().scheduleDictionary.remove(schedule.getId());
        JsonController.saveJson(getAllScheduleValue(), "schedule", context);
    }
    public void addScheduleToDictionary(int id, Schedule schedule){
        getInstance().scheduleDictionary.put(id, schedule);
        JsonController.saveJson(getAllScheduleValue(), "schedule", context);
    }
    public void addScheduleToDictionary(Schedule schedule){
        getInstance().scheduleDictionary.put(schedule.getId(), schedule);
        JsonController.saveJson(getAllScheduleValue(), "schedule", context);
    }
    public void updateSchedule(Schedule schedule){
        getInstance().scheduleDictionary.remove(schedule.getId());
        getInstance().scheduleDictionary.put(schedule.getId(), schedule);
        JsonController.saveJson(getAllScheduleValue(), "schedule", context);
    }
    
    
    public ArrayList<Member> getAllMemberValues() {
        return new ArrayList<>(getInstance().memberList.values());
    }
    public Member getMemberById(int id){
        return getInstance().memberList.get(id);
    }
    public void addMemberToList(Member member){
        getInstance().memberList.put(member.GetId(), member);
        JsonController.saveJson(getAllMemberValues(), "member", context);
    }
    public void setMemberList(HashMap<Integer, Member> memberList){
        getInstance().memberList = memberList;
        JsonController.saveJson(getAllMemberValues(), "member", context);
    }
    
    
    public ArrayList<Waypoint> getAllWaypointValues() {
        return new ArrayList<>(getInstance().waypointList.values());
    }
    public Waypoint getWaypointById(int id) {
        return getInstance().waypointList.get(id);
    }
    public void addWaypointToList(Waypoint waypoint) {
        getInstance().waypointList.put(waypoint.GetId(), waypoint);
    }
    public void setWaypointList(HashMap<Integer, Waypoint> waypointList) {
        getInstance().waypointList = waypointList;
    }
    public void saveWaypoint(){
        JsonController.saveJson(getAllWaypointValues(), "waypoints", context);
    }
    
    public void addOwnerList(long id, String name, String profileUrl) {
        String[] info = new String[2];
        info[0] = name;
        info[1] = profileUrl;
        getInstance().ownerList.put(id, info);
    }
    public void addOwnerList(long id, String[] info) {
        getInstance().ownerList.put(id, info);
    }
    public String[] getOwnerList(long id) {
        return getInstance().ownerList.get(id);
    }
    
    public ArrayList<SharedSchedule> getSharedSchedules() {
        return getInstance().sharedSchedules;
    }
    public void addSharedSchedule(SharedSchedule sharedSchedule) {
        getInstance().sharedSchedules.add(sharedSchedule);
    }
    public void setSharedSchedules(ArrayList<SharedSchedule> sharedSchedules) {
        getInstance().sharedSchedules = sharedSchedules;
    }
}



