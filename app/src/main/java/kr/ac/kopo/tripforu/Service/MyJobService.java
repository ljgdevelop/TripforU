package kr.ac.kopo.tripforu.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kr.ac.kopo.tripforu.Alarm;
import kr.ac.kopo.tripforu.JsonController;
import kr.ac.kopo.tripforu.PageController;
import kr.ac.kopo.tripforu.Settings;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class MyJobService extends JobService {
    
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new Thread(() -> checkAlarm()).start();
        return true;
    }
    
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkAlarm(){
        try {
            List<String> jsonList = Files.readAllLines(Paths.get(getFilesDir() + "/settings.json"));
            StringBuilder json = new StringBuilder("");

            for (String line : jsonList) {
                json.append(line);
            }
    
            ArrayList<Settings> settingList =
                new Gson()
                    .fromJson(JsonController.readJson("settings", this).toJSONString(), new TypeToken<ArrayList<Settings>>() {}.getType());
            Settings setting = settingList.get(0);
            
            
            if(setting.lastAlarmCheck == null || LocalDate.now().isAfter(setting.lastAlarmCheck)){
                String settingText = new Gson().toJson(setting.lastAlarmCheck = LocalDate.now());
                Files.write(Paths.get(getFilesDir() + "/settings.json"), settingText.getBytes());
                Alarm alarm = new Alarm().asContext(this);
                alarm.schAlarm();
            }else {
                Alarm alarm = new Alarm().asContext(this);
                alarm.removeNotification();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
