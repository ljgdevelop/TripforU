package kr.ac.kopo.tripforu;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class ActivitySettings extends PageController {
    public void showSettings(){
        Button btn_AlarmForbidTime = findViewById(R.id.BTN_AlarmForbidTime);
        Button btn_AlarmForbidTimePrevious = findViewById(R.id.BTN_AlarmForbidTime_Previous);
        Button btn_AlarmForbidTimeStorage = findViewById(R.id.BTN_AlarmForbidTime_Storage);
        Button btn_Credit = findViewById(R.id.BTN_Credit);
        SwitchCompat switch_AllAlarm = findViewById(R.id.SWITCH_AllAlarm);
        LinearLayout layout_AllAlarmScreenFence = findViewById(R.id.LAYOUT_AllAlarmScreenFence);
        LinearLayout layout_AlarmForbidTime = findViewById(R.id.LAYOUT_AlarmForbidTime);
        LinearLayout layout_Credit = findViewById(R.id.LAYOUT_Credit);

        //전체 알림 상태 확인
        if (switch_AllAlarm.isChecked()){
            layout_AllAlarmScreenFence.setVisibility(View.GONE);
        }else{
            layout_AllAlarmScreenFence.setVisibility(View.VISIBLE);
        }

        //전체 알림 on , off 기능
        switch_AllAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheckd) {
                if(isCheckd){
                    layout_AllAlarmScreenFence.setVisibility(View.GONE);
                } else {
                    layout_AllAlarmScreenFence.setVisibility(View.VISIBLE);
                }
            }
        });

        //전체 알림 가림막 터치 막는기능
        layout_AllAlarmScreenFence.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //알림 차단 시간 설정창 들어가기
        btn_AlarmForbidTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_AlarmForbidTime.setVisibility(View.VISIBLE);
            }
        });

        //알람 차단 시간 설정 (이전)
        btn_AlarmForbidTimePrevious.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                layout_AlarmForbidTime.setVisibility(View.GONE);
            }
        });

        //알림 차단 시간 설정(저장)
        btn_AlarmForbidTimeStorage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                layout_AlarmForbidTime.setVisibility(View.GONE);
            }
        });

        //크레딧창 들어가기
        btn_Credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_credit);
            }
        });
    }
}