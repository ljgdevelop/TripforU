package kr.ac.kopo.tripforu.Retrofit;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {
    @POST("posts")
    Call<Post> post(@Body Post post);
    
    @POST("posts")
    Call<List<Post>> posts();
    
    @GET("get")
    Call<List<Get>> get();
    
    @GET("get")
    Call<GetImageId> getImage();
}
