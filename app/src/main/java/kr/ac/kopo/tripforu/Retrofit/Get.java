package kr.ac.kopo.tripforu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Get {
    @SerializedName("body")
    private String jsonString;
    
    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
    public String getJsonString() {
        return this.jsonString;
    }
}
