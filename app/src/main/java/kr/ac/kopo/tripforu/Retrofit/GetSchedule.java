package kr.ac.kopo.tripforu.Retrofit;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

import kr.ac.kopo.tripforu.Schedule;
import kr.ac.kopo.tripforu.SharedSchedule;

public class GetSchedule {
    @SerializedName("ID")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Destination")
    private String destination;
    @SerializedName("Days")
    private int days;
    @SerializedName("StartDate")
    private String startDate;
    @SerializedName("Waypoints")
    private ArrayList<Waypoint> wayPointList = new ArrayList<>();
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule getSchedule(){
        ArrayList<kr.ac.kopo.tripforu.Waypoint> wayPointList = new  ArrayList<>();
        Log.d("TAG", "getSchedule: " + wayPointList.size());
        for (Waypoint wp : this.wayPointList) {
            kr.ac.kopo.tripforu.Waypoint waypoint = new kr.ac.kopo.tripforu.Waypoint();
            waypoint.SetId(wp.getId());
            waypoint.SetName(wp.getName());
            waypoint.SetPosX(wp.getPosX());
            waypoint.SetPosY(wp.getPosY());
            waypoint.SetRating(wp.getRating());
            waypoint.SetReviewCount(wp.getReviewCount());
            waypoint.SetType(wp.getType());
            waypoint.SetOriginalLink(wp.getOriginLink());
            waypoint.SetTime(0);
            wayPointList.add(waypoint);
        }
        Schedule sch = new Schedule(id, name, destination, days, startDate, wayPointList);
        return sch;
    }
    
    private class Waypoint{
        @SerializedName("ID")
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
    
        public int getId() {
            return id;
        }
    
        public String getName() {
            return name;
        }
    
        public double getPosX() {
            return posX;
        }
    
        public double getPosY() {
            return posY;
        }
    
        public byte getRating() {
            return rating;
        }
    
        public int getReviewCount() {
            return reviewCount;
        }
    
        public int getType() {
            return type;
        }
    
        public String getOriginLink() {
            return originLink;
        }
    }
}
