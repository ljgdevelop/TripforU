package kr.ac.kopo.tripforu.Retrofit;

import com.google.gson.annotations.SerializedName;
import kr.ac.kopo.tripforu.Waypoint;

public class GetWaypoints {
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("PosX")
    private double posX;
    @SerializedName("PosY")
    private double posY;
    @SerializedName("Rating")
    private byte rating;
    @SerializedName("ReviewCount")
    private int reviewCount;
    @SerializedName("Type")
    private int type;
    @SerializedName("OriginLink")
    private String originLink;
    @SerializedName("Time")
    private int time;
    
    public Waypoint getWaypoint(){
        Waypoint waypoint = new Waypoint(id, name, posX, posY, rating, reviewCount, type, originLink, time);
        return waypoint;
    }
}
