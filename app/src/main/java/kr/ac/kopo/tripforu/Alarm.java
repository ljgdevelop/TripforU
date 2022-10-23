package kr.ac.kopo.tripforu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Alarm extends PageController {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        schAlarm();
    }
    public void schAlarm(){
        try {
            ArrayList<Schedule> scheduleArrayList = ScheduleController.getInstance().getAllScheduleValue();
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
            Date now = Calendar.getInstance().getTime();
            String str = sdfNow.format(now);
            Date date = sdfNow.parse(str);
            for (int i = 0; i < scheduleArrayList.size(); i++) {
                Schedule schedule = scheduleArrayList.get(i);
                Date startDate = sdfNow.parse(schedule.getStartDate());
                long calDate = startDate.getTime() - date.getTime();
                long calDateDays = calDate / (24*60*60*1000);
                calDateDays = Math.abs(calDateDays);
                if (calDateDays <= 7){
                    showNotification(schedule, calDateDays);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showNotification(Schedule schedule, Long date) {
        String CHANNEL_ID = Integer.toString(schedule.getId());
        String CHANEL_NAME = schedule.getName();
        NotificationCompat.Builder builder = null;
        NotificationManager manager = (NotificationManager) ActivityMain.context.getSystemService(ActivityMain.context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(ActivityMain.context, CHANNEL_ID);
        }else {
            builder = new NotificationCompat.Builder(ActivityMain.context);
        }
        Intent intent = new Intent(ActivityMain.context, ActivityMain.class);
        //넘길 데이터 추가
        intent.putExtra("schName",schedule.getName());
        PendingIntent pendingIntent = PendingIntent.getActivity(ActivityMain.context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);
        //알림창 제목
        builder.setContentTitle("TripForu");
        //알림창 메시지
        builder.setContentText(schedule.getName()+" 일정이 " + date +"일 남았습니다.");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_alarm);
        //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
        builder.setAutoCancel(true);
        //pendingIntent를 builder에 설정 해줍니다.
        //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        //알림창 실행
        manager.notify(1,notification);
    }
}
