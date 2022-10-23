package kr.ac.kopo.tripforu;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Alarm extends PageController{
    //D-Day 날짜 확인
    public void schAlarm(){
        try {
            ArrayList<Schedule> scheduleArrayList = ScheduleController.getInstance().getAllScheduleValue();
            //현재 날짜 가져오기
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
            Date now = Calendar.getInstance().getTime();
            String str = sdfNow.format(now);
            Date date = sdfNow.parse(str);
            //스케쥴 시작 날짜와 현재 날짜 기간 차이 구하기
            for (int i = 0 ; i < scheduleArrayList.size(); i++){
                Schedule schedule = scheduleArrayList.get(0);
                Date startDate = sdfNow.parse(schedule.getStartDate());
                long calDate = startDate.getTime() - date.getTime();
                long calDateDays = calDate / (24*60*60*1000);
                calDateDays = Math.abs(calDateDays);
                //남은 여행이 7일 이하로 남으면 알림 출력
                if (calDateDays <= 7 && calDateDays > -1){
                    showNotification(schedule, calDateDays);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    //알림 생성
    private void showNotification(Schedule schedule, Long day) {
        String CHANNEL_ID = Integer.toString(schedule.getId());
        String CHANEL_NAME = schedule.getName();
        NotificationCompat.Builder builder = null;
        NotificationManager manager = (NotificationManager) ActivityMain.context.getSystemService(ActivityMain.context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, CHANEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(ActivityMain.context, CHANNEL_ID);
        }else {
            builder = new NotificationCompat.Builder(ActivityMain.context);
        }
        Intent intent = new Intent(ActivityMain.context, ActivityMain.class);
        //넘길 데이터 추가
        intent.putExtra("schName",schedule.getName());
        PendingIntent pendingIntent = PendingIntent.getActivity(ActivityMain.context, 1,
                intent, PendingIntent.FLAG_MUTABLE);
        //알림창 제목
        builder.setContentTitle("TripForu");
        //알림창 메시지
        builder.setContentText(schedule.getName()+" 일정이 " + day +"일 남았습니다.");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_alarm);
        //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
        builder.setAutoCancel(true);
        //pendingIntent를 builder에 설정 해줍니다.
        //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        //오늘날짜의 몇시 몇분 몇초에 출력할지 지정
        Calendar resetCal = Calendar.getInstance();
        resetCal.setTimeInMillis(System.currentTimeMillis());
        resetCal.set(Calendar.HOUR_OF_DAY, 2);
        resetCal.set(Calendar.MINUTE, 0);
        resetCal.set(Calendar.SECOND, 0);

        //알람 매니저에 셋팅
        AlarmManager alarmManager = (AlarmManager)getSystemService(ActivityMain.context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, resetCal.getTimeInMillis(), pendingIntent);
        Log.d("TAG", "showNotification123123: ");
        //알림창 실행
        manager.notify(1,notification);
    }

    public void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);
    }
}
