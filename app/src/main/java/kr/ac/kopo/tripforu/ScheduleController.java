package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

public class ScheduleController extends Activity{
    public static HashMap<Integer, Schedule> scheduleDictionary = new HashMap<>();
    public static ArrayList<Member> memberList = new ArrayList<>();
    public static ArrayList<Waypoint> waypointList = new ArrayList<>();
    
    public final int IC_HOME = 1;
    public final static int IC_CIRCLE = 2;
    public final static int IC_SUBWAY = 3;
    public final static int IC_TRAIN = 4;
    public final static int IC_CAR = 5;
    public final static int IC_HOTEL = 6;
    public final static int IC_RESTAURANT = 7;
    public final static int IC_PIN = 8;
    
    /***
     * @author 이제경
     * @param id - 제거할 일정의 ID
     *
     *      ID값으로 scheduleDictionary에서 Schedule을 제거하고
     *      assets/json/schedule.json에 결과를 저장합니다
     */
    public static void removeScheduleById(int id, Context context){
        scheduleDictionary.remove(id);
        JsonController.saveJson(scheduleDictionary.values(), "schedule", context);
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
    
            Collection<Schedule> tempArray = scheduleDictionary.values();
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
                        waypointList.add((Waypoint) newObj.get(i));
                    break;
                case "class kr.ac.kopo.tripforu.Member":
            
                    newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Member>>() {
                    }.getType());
                    for (int i = 0; i < newObj.size(); i++) {
                        memberList.add((Member) newObj.get(i));
                    }
                    break;
                case "class kr.ac.kopo.tripforu.Schedule":
                    newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Schedule>>() {}.getType());
                    for (int i = 0; i < newObj.size(); i++) {
                        Schedule newSch = (Schedule) newObj.get(i);
                        scheduleDictionary.put(newSch.getId(), newSch);
                    }
                    break;
                default:
                    
                    System.out.println("Schedule");
                    break;
            }
        }catch (Exception e) {
            Log.e("", "syncJsonToObject: ", e);
        }
    }
    
    public static Member getMemberByID(int id){
        for(int i = 0; i < memberList.size(); i ++){
            if(memberList.get(i).GetId() == id)
                return memberList.get(i);
        }
        return null;
    }
}



