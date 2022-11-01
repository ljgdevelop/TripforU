package kr.ac.kopo.tripforu;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Alarm{
    Context context;
    private NotificationManager notificationManager = null;
    //D-Day 날짜 확인

    public void schAlarm(){
        try {
            ArrayList<Schedule> scheduleArrayList = ScheduleController.getInstance().getSortedScheduleByDate();
            //현재 날짜 가져오기
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
            Date now = Calendar.getInstance().getTime();
            String str = sdfNow.format(now);
            Date date = sdfNow.parse(str);
            //스케쥴 시작 날짜와 현재 날짜 기간 차이 구하기
            for (int i = 0 ; i < scheduleArrayList.size(); i++){
                Schedule schedule = scheduleArrayList.get(i);
                Date startDate = sdfNow.parse(schedule.getStartDate());

                long calDate = startDate.getTime() - date.getTime();
                int calDateDays = (int)(calDate / (24*60*60*1000));
                //남은 여행이 7일 이하로 남으면 알림 출력
                if (calDateDays <= 7 && calDateDays > -1){
                    showNotification(schedule, calDateDays);
                }else{
                    break;
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Alarm asContext (Context context){
        this.context = context;
        return this;
    }

    //알림 생성
    private void showNotification(Schedule schedule, int day) {
        creatNotificationChannel(schedule);
        NotificationCompat.Builder builder = getBuilder(schedule, day);
        Intent intent = new Intent(context, ActivityMain.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, schedule.getId(),
                intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar resetCal = getCalendar();
        //알람 매니저에 셋팅
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, resetCal.getTimeInMillis(), pendingIntent);
        //알림창 실행
        NotificationManager manager = getManager();
        manager.notify(schedule.getId(),builder.build());
    }

    //날짜와 시간 지정
    private Calendar getCalendar(){
        //오늘날짜의 몇시 몇분 몇초에 출력할지 지정
        Calendar resetCal = Calendar.getInstance();
        resetCal.setTimeInMillis(System.currentTimeMillis());
        resetCal.set(Calendar.HOUR_OF_DAY, 15);
        resetCal.set(Calendar.MINUTE, 41);
        resetCal.set(Calendar.SECOND, 10);
        if (Calendar.getInstance().after(resetCal)){
            resetCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return resetCal;
    }
    //NotificationManager 생성
    private NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    //PendingIntent 생성
    private PendingIntent getPendingIntent(Schedule schedule){
        //인텐트 추가 구간*
        PendingIntent pendingIntent = null;
        Intent intent = new Intent(context, ActivityMain.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //넘길 데이터 추가
        intent.putExtra("schId",schedule.getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getActivity(context, schedule.getId(),
                    intent, PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(context, schedule.getId(),
                    intent, 0);
        }

        return pendingIntent;
    }

    //NotificationCompat 빌더 생성
    private NotificationCompat.Builder getBuilder(Schedule schedule, int day){
        PendingIntent pendingIntent = getPendingIntent(schedule);
        //빌드 구간*
        NotificationCompat.Builder builder = null;
        builder = new NotificationCompat.Builder(context, Integer.toString(schedule.getId()));
        //알림창 제목
        builder.setContentTitle("TripForu");
        //알림창 메시지
        if (day == 0){
            builder.setContentText(schedule.getName()+" 일정 시작일입니다.");
        }else {
            builder.setContentText(schedule.getName()+" 일정이 " + day +"일 남았습니다.");
        }
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_alarm);
        //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
        builder.setAutoCancel(true);
        //pendingIntent를 builder에 설정 해줍니다.
        //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
        builder.setContentIntent(pendingIntent);
        builder.setChannelId(Integer.toString(schedule.getId()));
        return builder;
    }

    //채널 생성
    private void creatNotificationChannel(Schedule schedule){
        String CHANNEL_ID = Integer.toString(schedule.getId());
        String CHANEL_NAME = schedule.getName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, CHANEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(schedule.getName());
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    public void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(context).cancel(1);
    }
}
