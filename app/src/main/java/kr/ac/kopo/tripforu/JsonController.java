package kr.ac.kopo.tripforu;


import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonController extends AppCompatActivity {
    
    /***
     * @author 이제경
     * @param objList - 저장할 데이터가 담긴 데이터 집합
     * @param fileName - 저장할 Json 파일 위치
     */
    public static void saveJson(Collection objList, String fileName, Context context){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            
            StringBuilder path = new StringBuilder(fileName);
            path.append(".json");
            
            FileOutputStream fos = context.openFileOutput(path.toString(), Context.MODE_PRIVATE);
            
            JSONArray ja = new JSONArray();
            ja.addAll(objList);
            
            fos.write(gson.toJson(ja).getBytes());
    
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /***
     * @author 이제경
     * @param context - applicationContext
     */
    public static void saveJsonFromAssets(Context context){
        ScheduleController.syncJsonToObject(JsonController.readJsonArrayFromAssets("json/member.json", context),
            Member.class.toString());
        ScheduleController.syncJsonToObject(JsonController.readJsonArrayFromAssets("json/waypoints.json", context),
            Waypoint.class.toString());
        ScheduleController.syncJsonToObject(JsonController.readJsonArrayFromAssets("json/schedule.json", context),
            Schedule.class.toString());
    
        saveJson(ScheduleController.memberList, "member", context);
        saveJson(ScheduleController.waypointList, "waypoints", context);
        saveJson(ScheduleController.scheduleDictionary.values(), "schedule", context);
    }
    
    /***
     * @author 이제경
     * @param context - 저장할 데이터가 담긴 데이터 집합
     * @param fileName - 저장할 Json 파일 이름
     */
    public static JSONArray readJson(String fileName, Context context) {
        String json = "";
        try {
            StringBuilder path = new StringBuilder(fileName);
            path.append(".json");
            
            FileInputStream fis = context.openFileInput(path.toString());
            StringBuffer buffer = new StringBuffer();
            String data = null;
            BufferedReader iReader = new BufferedReader(new InputStreamReader(fis));
            
            data = iReader.readLine();
            while(data != null){
                buffer.append(data);
                data = iReader.readLine();
            }
    
            json = buffer.toString();
            
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            //JSONObject obj = (JSONObject) parser.parse(json);
            
            //JSONArray jsonArray = (JSONArray) obj.get("items");
    
            return jsonArray;
        
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /***
     * @author 이제경
     * @param context - 저장할 데이터가 담긴 데이터 집합
     * @param filePath - 저장할 Json 파일 이름
     */
    public static JSONArray readJsonArrayFromAssets(String filePath, Context context) {
        try {
            InputStream fis = context.getResources().getAssets().open(filePath);
            int fileSize = fis.available();
        
            byte[] buffer = new byte[fileSize];
            fis.read(buffer);
            fis.close();
        
            String json = new String(buffer, "UTF-8");
        
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);
        
            JSONArray jsonArray = (JSONArray) obj.get("items");
        
            return jsonArray;
        
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /***
     * @author 이제경
     * @param context - 저장할 데이터가 담긴 데이터 집합
     * @param filePath - 저장할 Json 파일 이름
     */
    public static JSONObject readJsonObjFromAssets(String filePath, Context context) {
        try {
            InputStream fis = context.getResources().getAssets().open(filePath);
            int fileSize = fis.available();
            
            byte[] buffer = new byte[fileSize];
            fis.read(buffer);
            fis.close();
            
            String json = new String(buffer, "UTF-8");
            
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);
            
            return obj;
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
