package kr.ac.kopo.tripforu;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kopo.tripforu.AWS.S3;
import kr.ac.kopo.tripforu.Retrofit.INetTask;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ServerController extends PageController{
    // singleton 객체
    private static ServerController instance;
    
    private ServerController(){
        instance = this;
    }
    
    public static ServerController getInstance(){
        if(instance == null){
            return new ServerController();
        }else
            return instance;
    }
    
    public void uploadSchedule(SharedSchedule sharedSchedule, Schedule schedule){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if (user != null) {
                    INetTask.getInstance().uploadUserInfo(user);
                    INetTask.getInstance().uploadSchedule(schedule);
                    sharedSchedule.setOwnerId(user.getId());
                    INetTask.getInstance().uploadSharedSchedule(sharedSchedule);
                }
                return null;
            });
        }
    }
}
