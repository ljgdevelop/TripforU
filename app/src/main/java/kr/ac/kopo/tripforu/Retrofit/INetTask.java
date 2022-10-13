package kr.ac.kopo.tripforu.Retrofit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import kr.ac.kopo.tripforu.ActivityMain;
import kr.ac.kopo.tripforu.ScheduleController;
import kr.ac.kopo.tripforu.SharedSchedule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class INetTask {
    // singleton 객체
    private static INetTask instance;
    private INetTask(){
        instance = this;
    }
    
    public static INetTask getInstance(){
        if(instance == null){
            return new INetTask();
        }else
            return instance;
    }
    
    
    private void dataReceived(Object data){
    
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getAvailableImageId(){
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/image/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        
                Call<GetImageId> call = jsonPlaceHolderApi.getImage();
    
                GetImageId id = call.execute().body();
                Log.d("TAG", "getAvailableImageId: " + id.getResult());
                return id.getResult();
            }catch (IOException e){
                e.printStackTrace();
                return -1;
            }
        });
        try {
            int result = future.get();
            Log.d("TAG", "getAvailableImageId: " + result);
            return result;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void uploadSharedSchedule(SharedSchedule schedule){
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(schedule);
    
            Post post = new Post();
            post.setJsonObject(jsonString);
    
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            
            Call<Post> call = jsonPlaceHolderApi.post(post);
            call.enqueue(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
