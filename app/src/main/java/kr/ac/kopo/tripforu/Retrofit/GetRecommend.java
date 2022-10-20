package kr.ac.kopo.tripforu.Retrofit;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;

import kr.ac.kopo.tripforu.SharedSchedule;

public class GetRecommend {
    @SerializedName("ScheduleId")
    private int scheduleId;
    @SerializedName("OwnerId")
    private long ownerId;
    @SerializedName("Rating")
    private double rating;
    @SerializedName("Likes")
    private byte likes;
    @SerializedName("SharedCount")
    private int sharedCount;
    @SerializedName("TitleImgId")
    private int titleImgId;
    @SerializedName("TitleText")
    private String titleText;
    @SerializedName("DescriptionText")
    private String descriptionText;
    @SerializedName("DescriptionList")
    private ArrayList<Description> descriptionList = new ArrayList<>();
    
    public SharedSchedule getRecommendSchedule(){
        ArrayList<SharedSchedule.WaypointDescription> descriptionList = new  ArrayList<SharedSchedule.WaypointDescription>();
        for (Description des : this.descriptionList) {
            SharedSchedule.WaypointDescription waypoint = new SharedSchedule().new WaypointDescription();
            waypoint.setWaypointId(des.getWaypointId());
            waypoint.setWaypointImgId(des.getWaypointImgId());
            waypoint.setWaypointContent(des.getWaypointContent());
            descriptionList.add(waypoint);
        }
        SharedSchedule sch = new SharedSchedule(scheduleId, ownerId, rating, likes, sharedCount, titleImgId, titleText, descriptionText, descriptionList);
        return sch;
    }
    
    private class Description{
        @SerializedName("WaypointId")
        private int waypointId;
        @SerializedName("WaypointImgId")
        private int[] waypointImgId = new int[3];
        @SerializedName("WaypointContent")
        private String waypointContent;
    
        public int getWaypointId() {
            return waypointId;
        }
    
        public int[] getWaypointImgId() {
            return waypointImgId;
        }
    
        public String getWaypointContent() {
            return waypointContent;
        }
    }
}
