package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.os.Build;
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
    public static ArrayList<Member> memberList = new ArrayList<>();
    public static ArrayList<Waypoint> waypointList = new ArrayList<>();
    static int wpc = 10000000;
    
    public final int IC_HOME = 1;
    public final static int IC_CIRCLE = 2;
    public final static int IC_SUBWAY = 3;
    public final static int IC_TRAIN = 4;
    public final static int IC_CAR = 5;
    public final static int IC_HOTEL = 6;
    public final static int IC_RESTAURANT = 7;
    public final static int IC_PIN = 8;
    
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : JsonArray형식의 .Json파일을 읽어와 객체로 변환합니다.
     * -> 인자 : json = json파일
     * -> 인자 : className = 저장할 객체의 클래스 이름
     */
    public static void syncJsonToObject(org.json.simple.JSONArray json, String className){
        Gson gson = new Gson();
        ArrayList<Object> newObj;
        switch (className){
            case "class kr.ac.kopo.tripforu.Waypoint":
                
                newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Waypoint>>(){}.getType());
                for (int i = 0; i < newObj.size(); i++)
                    waypointList.add((Waypoint)newObj.get(i));
                break;
            case "class kr.ac.kopo.tripforu.Member":
    
                newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Member>>(){}.getType());
                for (int i = 0; i < newObj.size(); i++) {
                    memberList.add((Member) newObj.get(i));
                }
                break;
            case "class kr.ac.kopo.tripforu.Schedule":
    
                newObj = gson.fromJson(json.toString(), new TypeToken<ArrayList<Schedule>>(){}.getType());
                for (int i = 0; i < newObj.size(); i++) {
                    Schedule newSch = (Schedule)newObj.get(i);
                    scheduleDictionary.put(newSch.GetId(), newSch);
                }
                break;
            default:
                
                System.out.println("Schedule");
                break;
        }
    }
    
    
    public static Member GetMemberByID(int id){
        for(int i = 0; i < memberList.size(); i ++){
            if(memberList.get(i).GetId() == id)
                return memberList.get(i);
        }
        return null;
    }
}



