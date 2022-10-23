package kr.ac.kopo.tripforu;

import android.content.Context;


import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {
    public LocalDate lastAlarmCheck;
    public HashMap<String, LocalDateTime> alarmTimeList = new HashMap<>();
    public boolean isAlarmOn = false;
    public boolean isDoNotDisturb = false;
    public LocalTime disturbTimeStart = LocalTime.of(0, 0);;
    public LocalTime disturbTimeEnd = LocalTime.of(0, 0);
    public boolean isDdayAlarmOn = false;
    public boolean isRecommendAlarmOn = false;
    public boolean isFullScreenAlarmOn = false;
    
    public Settings setLastAlarmCheck(LocalDate date){
        this.lastAlarmCheck = date;
        return PageController.saveSettings();
    }
    
    public Settings addAlarmTime(String key, LocalDateTime value){
        this.alarmTimeList.put(key, value);
        return PageController.saveSettings();
    }
    
    public Settings updateAlarmTime(String key, LocalDateTime value){
        this.alarmTimeList.remove(key);
        this.alarmTimeList.put(key, value);
        return PageController.saveSettings();
    }
    
    public void removeAlarmTime(String key){
        alarmTimeList.remove(key);
        PageController.saveSettings();
    }
    
    public Settings setAlarmOn(boolean isOn){
        this.isAlarmOn = isOn;
        return PageController.saveSettings();
    }
    
    public Settings setDoNotDisturb(boolean isOn, LocalTime start, LocalTime end){
        this.isDoNotDisturb = isOn;
        this.disturbTimeStart = start;
        this.disturbTimeEnd = end;
        return PageController.saveSettings();
    }
    
    public Settings setDdayAlarmOn(boolean isOn){
        this.isDdayAlarmOn = isOn;
        return PageController.saveSettings();
    }
    
    public Settings setRecommendAlarmOn(boolean isOn){
        this.isRecommendAlarmOn = isOn;
        return PageController.saveSettings();
    }
    
    public Settings setFullScreenAlarmOn(boolean isOn){
        this.isFullScreenAlarmOn = isOn;
        return PageController.saveSettings();
    }
}
