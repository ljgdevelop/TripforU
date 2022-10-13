package kr.ac.kopo.tripforu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class GetImageId {
    @SerializedName("key")
    private int value;
    
    public int getResult(){return value;}
}
