package kr.ac.kopo.tripforu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AlarmReceiver extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    public void onReceive(Context context, Intent intent){

        Log.e("TAG", "onReceive 알람이 들어옴!!");
        String contentName = "c";
        String contentId = "1";

        builder = null;

        //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //안드로이드 오레오 버전 대응
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(contentName, contentId, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, contentId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        //알림창 클릭 시 지정된 activity 화면으로 이동
        Intent intent2 = new Intent(context, ActivityMain.class);

        // FLAG_UPDATE_CURRENT ->
        // 설명된 PendingIntent가 이미 존재하는 경우 유지하되, 추가 데이터를 이 새 Intent에 있는 것으로 대체함을 나타내는 플래그입니다.
        // getActivity, getBroadcast 및 getService와 함께 사용
        PendingIntent pendingIntent = PendingIntent.getActivity(context,Integer.parseInt(contentId),intent2,
                PendingIntent.FLAG_MUTABLE);

        //알림창 제목
        builder.setContentTitle(contentName); //회의명노출
        builder.setContentText(contentName + " 일정 시작일입니다."); //회의 내용
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_alarm);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        //푸시알림 빌드
        Notification notification = builder.build();

        //NotificationManager를 이용하여 푸시 알림 보내기
        manager.notify(Integer.parseInt(contentId)+1,notification);
        Log.d(TAG, "onReceive: 완료");
    }
}
