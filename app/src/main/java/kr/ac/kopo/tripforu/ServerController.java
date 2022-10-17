package kr.ac.kopo.tripforu;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import java.io.File;
import java.util.ArrayList;

import kr.ac.kopo.tripforu.AWS.S3;
import kr.ac.kopo.tripforu.Retrofit.INetTask;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ServerController extends PageController{
    // singleton 객체
    private static ServerController instance;
    private Context context;
    
    String adress = "ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com";
    short port = 6060;
    byte connectionTry = 0;//5회까지 5초 간격으로 재 접속 시도
    
    private ServerController(){
        instance = this;
        context = ActivityMain.context;
    }
    
    public static ServerController getInstance(){
        if(instance == null){
            return new ServerController();
        }else
            return instance;
    }
    
    public void uploadSchedule(SharedSchedule sharedSchedule, Schedule schedule, ArrayList<File> imageList){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if (user != null) {
                    INetTask.getInstance().uploadUserInfo(user);
                    INetTask.getInstance().uploadSchedule(schedule);
                    INetTask.getInstance().uploadSharedSchedule(sharedSchedule);
                    if(imageList != null)
                    for (File img:imageList) {
                        int id = INetTask.getInstance().getAvailableImageId();
                        S3.getInstance(getApplicationContext()).uploadWithTransferUtilty(id + ".jpg", img);
                    }
                }
                return null;
            });
        }
    }
}
