package kr.ac.kopo.tripforu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Get {
    @SerializedName("body")
    private String jsonString;
    
    public void setJsonObject(String jsonString) {
        this.jsonString = jsonString;
    }
    public String getJsonObject() {
        return this.jsonString;
    }
}
