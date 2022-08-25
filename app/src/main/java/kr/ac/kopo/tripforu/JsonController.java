package kr.ac.kopo.tripforu;


import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonController extends AppCompatActivity {
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : json확장자 파일을 url을 통해 접근하여 내용을 String으로 반환
     * -> 인자 : fileUrl = JSON파일이 있는 경로
     */
    public static void WriteJson(JSONObject obj, String fileUrl){
        try {
            FileWriter file = new FileWriter(fileUrl);
            file.write(obj.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(obj);
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 파일 경로에 있는 Json 파일을 읽어 JsonObject로 반환
     * -> 인자 : fileUrl = JSON파일이 있는 경로
     */
    public static JSONArray ReadJson(String fileUrl, Context context) {
        try {
            InputStream is = context.getAssets().open(fileUrl);
            int fileSize = is.available();
    
            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();
    
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
     * -> 작성자 : 이제경
     * -> 함수 : 파일 경로에 있는 Json 파일을 읽어 JsonObject로 반환
     * -> 인자 : fileUrl = JSON파일이 있는 경로
     */
    public static JSONObject ReadJsonObj(String fileUrl, Context context) {
        try {
            InputStream is = context.getAssets().open(fileUrl);
            int fileSize = is.available();
            
            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();
            
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
