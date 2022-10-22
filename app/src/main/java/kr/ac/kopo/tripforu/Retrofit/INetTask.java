package kr.ac.kopo.tripforu.Retrofit;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.kakao.sdk.user.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import kr.ac.kopo.tripforu.Schedule;
import kr.ac.kopo.tripforu.ScheduleController;
import kr.ac.kopo.tripforu.SharedSchedule;
import kr.ac.kopo.tripforu.Waypoint;
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
    public void uploadUserInfo(User user){
        try {
            StringBuilder jsonString = new StringBuilder("{");
            jsonString.append("\"uid\": ");
            jsonString.append(user.getId());
            jsonString.append(",\"name\": \"");
            jsonString.append(user.getKakaoAccount().getProfile().getNickname());
            jsonString.append("\",\"profileUrl\": \"");
            jsonString.append(user.getKakaoAccount().getProfile().getProfileImageUrl());
            jsonString.append("\"}");
        
            Post post = new Post();
            post.setJsonObject(jsonString.toString());
        
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/user/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            
            Call<Post> call = jsonPlaceHolderApi.post(post);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
        
                }
    
                @Override
                public void onFailure(Call<Post> call, Throwable t) {
        
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void uploadSchedule(Schedule schedule){
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(schedule);
    
            Log.d("TAG", "uploadSchedule: "+ jsonString);
            
            Post post = new Post();
            post.setJsonObject(jsonString);
            
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/schedule/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            
            Call<Post> call = jsonPlaceHolderApi.post(post);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
            
                }
        
                @Override
                public void onFailure(Call<Post> call, Throwable t) {
            
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void uploadSharedSchedule(SharedSchedule schedule){
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(schedule);
    
            Post post = new Post();
            post.setJsonObject(jsonString);
    
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/sharedschedule/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            
            Call<Post> call = jsonPlaceHolderApi.post(post);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
            
                }
        
                @Override
                public void onFailure(Call<Post> call, Throwable t) {
            
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SharedSchedule> getRecommendScheduleList(){
        CompletableFuture<List<GetRecommend>> future = CompletableFuture.supplyAsync(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/recommend/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            
                Call<List<GetRecommend>> call = jsonPlaceHolderApi.getRecommend();
            
                return call.execute().body();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });
        try {
            ArrayList<SharedSchedule> newObj = new ArrayList<>();
            
            List<GetRecommend> jsonList = future.get();
            if(jsonList != null)
                for (GetRecommend recommend:jsonList) {
                    SharedSchedule sch = recommend.getRecommendSchedule();
                    newObj.add(sch);
                    ScheduleController.getInstance().addOwnerList(sch.getOwnerId() ,recommend.getOwnerInfo());
                }
            
            return newObj;
            
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SharedSchedule> getRecommendScheduleList(String keyword){
        CompletableFuture<List<GetRecommend>> future = CompletableFuture.supplyAsync(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/recommend/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
                
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                
                Call<List<GetRecommend>> call = jsonPlaceHolderApi.getRecommend(keyword);
                
                return call.execute().body();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });
        try {
            ArrayList<SharedSchedule> newObj = new ArrayList<>();
            
            List<GetRecommend> jsonList = future.get();
            if(jsonList != null)
                for (GetRecommend recommend:jsonList) {
                    SharedSchedule sch = recommend.getRecommendSchedule();
                    newObj.add(sch);
                    ScheduleController.getInstance().addOwnerList(sch.getOwnerId() ,recommend.getOwnerInfo());
                }
            
            return newObj;
            
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SharedSchedule> getRecommendScheduleList(long ownerId){
        CompletableFuture<List<GetRecommend>> future = CompletableFuture.supplyAsync(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/recommend/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
                
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                
                Call<List<GetRecommend>> call = jsonPlaceHolderApi.getRecommend(ownerId);
                
                return call.execute().body();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });
        try {
            ArrayList<SharedSchedule> newObj = new ArrayList<>();
            
            List<GetRecommend> jsonList = future.get();
            if(jsonList != null)
                for (GetRecommend recommend:jsonList) {
                    SharedSchedule sch = recommend.getRecommendSchedule();
                    newObj.add(sch);
                    ScheduleController.getInstance().addOwnerList(sch.getOwnerId() ,recommend.getOwnerInfo());
                }
            
            return newObj;
            
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule getSchedule(int id){
        CompletableFuture<GetSchedule> future = CompletableFuture.supplyAsync(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/schedule/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
                
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                
                Call<GetSchedule> call = jsonPlaceHolderApi.getSchedule(id + "");
                
                return call.execute().body();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });
        try {
            Schedule newObj;
            
            GetSchedule jsonList = future.get();
            if(jsonList != null){
                Schedule sch = jsonList.getSchedule();
                newObj = sch;
                ScheduleController.getInstance().addScheduleToDictionary(sch);
                return newObj;
            }
            
            return null;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Waypoint> getWayPoints(String keyword){
        CompletableFuture<List<GetWaypoints>> future = CompletableFuture.supplyAsync(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-34-196-61.ap-northeast-2.compute.amazonaws.com:6059/waypoints/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
                
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                
                Call<List<GetWaypoints>> call = jsonPlaceHolderApi.getWaypoints(keyword);
                
                return call.execute().body();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        });
        try {
            Gson gson = new Gson();
            
            ArrayList<Waypoint> newObj = new ArrayList<>();
            StringBuilder json = new StringBuilder();
            List<GetWaypoints> jsonList = future.get();
            for (GetWaypoints waypoint:jsonList) {
                json.append(waypoint.getWaypoint());
                newObj.add(waypoint.getWaypoint());
            }
            
            return newObj;
            
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
