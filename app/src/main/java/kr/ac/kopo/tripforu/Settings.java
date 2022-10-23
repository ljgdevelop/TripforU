package kr.ac.kopo.tripforu;

import android.content.Context;


import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class Settings {
    public LocalDate lastAlarmCheck;
    public HashMap<String, LocalDateTime> alarmTimeList = new HashMap<>();
    public boolean isAlarmOn = false;
    public boolean isDoNotDisturb = false;
    public LocalTime disturbTimeStart;
    public LocalTime disturbTimeEnd;
    public boolean isDdayAlarmOn = false;
    public boolean isRecommendAlarmOn = false;
    public boolean isFullScreenAlarmOn = false;
    
    private static Context context;
    private static Settings instance = null;
    public static Settings getInstance(){
        if(instance == null)
            return instance = new Settings();
        else
            return instance;
    }
    
    public Settings syncSetting(Context c){
        context = c;
        instance = new Gson().fromJson(JsonController.readJsonObj("settings", context).toJSONString(), Settings.class);
        return this;
    }
    
    public Settings setLastAlarmCheck(LocalDate date){
        this.lastAlarmCheck = date;
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public Settings addAlarmTime(String key, LocalDateTime value){
        this.alarmTimeList.put(key, value);
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public Settings updateAlarmTime(String key, LocalDateTime value){
        this.alarmTimeList.remove(key);
        this.alarmTimeList.put(key, value);
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public void removeAlarmTime(String key){
        alarmTimeList.remove(key);
        JsonController.saveJsonObj(this, "settings", context);
    }
    
    public Settings setAlarmOn(boolean isOn){
        this.isAlarmOn = isOn;
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public Settings setDoNotDisturb(boolean isOn, LocalTime start, LocalTime end){
        this.isDoNotDisturb = isOn;
        this.disturbTimeStart = start;
        this.disturbTimeEnd = end;
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public Settings setDdayAlarmOn(boolean isOn){
        this.isDdayAlarmOn = isOn;
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public Settings setRecommendAlarmOn(boolean isOn){
        this.isRecommendAlarmOn = isOn;
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
    
    public Settings setFullScreenAlarmOn(boolean isOn){
        this.isFullScreenAlarmOn = isOn;
        JsonController.saveJsonObj(this, "settings", context);
        return this;
    }
}
