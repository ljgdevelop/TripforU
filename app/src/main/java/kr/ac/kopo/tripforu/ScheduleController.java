package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class ScheduleController extends Application {
    private static ScheduleController instance;
    private Context context;
    
    private HashMap<Integer, Schedule> scheduleDictionary = new HashMap<>();
    private ArrayList<Member> memberList = new ArrayList<>();
    private ArrayList<Waypoint> waypointList = new ArrayList<>();
    private ArrayList<SharedSchedule> sharedSchedules = new ArrayList<>();
    
    public final static int IC_HOME = 1;
    public final static int IC_CIRCLE = 2;
    public final static int IC_SUBWAY = 3;
    public final static int IC_TRAIN = 4;
    public final static int IC_CAR = 5;
    public final static int IC_HOTEL = 6;
    public final static int IC_RESTAURANT = 7;
    public final static int IC_PIN = 8;
    
    
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
    
    public static Member getMemberByID(int id){
        for(int i = 0; i < getInstance().getMemberList().size(); i ++){
            if(getInstance().getMemberList().get(i).GetId() == id)
                return getInstance().getMemberList().get(i);
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
    
    public ArrayList<Member> getMemberList(){
        return getInstance().memberList;
    }
    public void addMemberToList(Member member){
        getInstance().memberList.add(member);
    }
    public void setMemberList(ArrayList<Member> memberList){
        getInstance().memberList = memberList;
    }
    
    public ArrayList<Waypoint> getWaypointList() {
        return getInstance().waypointList;
    }
    public void addWaypointToList(Waypoint waypoint) {
        getInstance().waypointList.add(waypoint);
    }
    public void setWaypointList(ArrayList<Waypoint> waypointList) {
        getInstance().waypointList = waypointList;
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



