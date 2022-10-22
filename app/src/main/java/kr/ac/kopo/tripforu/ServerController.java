package kr.ac.kopo.tripforu;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kopo.tripforu.AWS.S3;
import kr.ac.kopo.tripforu.Retrofit.INetTask;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ServerController extends PageController{
    private User user;
    
    // singleton 객체
    private static ServerController instance;
    
    private ServerController(){
        instance = this;
    }
    
    public static ServerController getInstance(){
        if(instance == null){
            if (AuthApiClient.getInstance().hasToken()) {
                UserApiClient.getInstance().me((user, throwable) -> {
                    instance.user = user;
                    return null;
                });
            }
            return new ServerController();
        }else
            return instance;
    }
    
    public void updateUserData(){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                instance.user = user;
                return null;
            });
        }
    }
    
    public boolean isLoggedIn(){
        return user != null;
    }
    
    public String getUserName(){
        return user.getKakaoAccount().getProfile().getNickname();
    }
    
    public String getUserProfileUrl(){
        return user.getKakaoAccount().getProfile().getNickname();
        
    }
    
    public void uploadSchedule(SharedSchedule sharedSchedule, Schedule schedule){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if (user != null) {
                    Log.d("TAG", "upLoading: " + 2);
                    INetTask.getInstance().uploadUserInfo(user);
                    Log.d("TAG", "upLoading: " + 3);
                    INetTask.getInstance().uploadSchedule(schedule);
                    Log.d("TAG", "upLoading: " + 4);
                    sharedSchedule.setOwnerId(user.getId());
                    INetTask.getInstance().uploadSharedSchedule(sharedSchedule);
                }
                return null;
            });
        }
    }
}
