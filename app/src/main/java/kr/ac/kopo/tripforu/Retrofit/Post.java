package kr.ac.kopo.tripforu.Retrofit;

import com.google.gson.annotations.SerializedName;

import retrofit2.http.Field;


public class Post {
    @SerializedName("body")
    private String jsonString;
    
    public void setJsonObject(String jsonString) {
        this.jsonString = jsonString;
    }
    public String getJsonObject() {
        return this.jsonString;
    }
}