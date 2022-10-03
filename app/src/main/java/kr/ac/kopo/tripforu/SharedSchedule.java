package kr.ac.kopo.tripforu;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class SharedSchedule {
    private int scheduleID;
    private int ownerID;
    private double rating;
    private byte likes;
    private int sharedCount;
    private Bitmap titleIMG;
    private String titleText;
    private String desctiptionText;
    private ArrayList<WaypointDescription> descriptionList = new ArrayList<>();

    public SharedSchedule(int scheduleID, int ownerID, double rating, int likes, int sharedCount,
                          Bitmap titleIMG, String titleText, String desctiptionText,
                          ArrayList<WaypointDescription> descriptionList){

        this.scheduleID = scheduleID;
        this.rating = rating;
        if (likes < Byte.MAX_VALUE)
            this.likes = Byte.parseByte(likes + "");
        this.sharedCount = sharedCount;
        this.titleIMG = titleIMG;
        this.titleText = titleText;
        this.desctiptionText = desctiptionText;
        this.descriptionList = descriptionList;
    }

    public void addWaypoint(int waypoint_id, String waypoint_name, int waypoint_type,
                            Bitmap waypoint_img, String waypoint_content){
        WaypointDescription w = new WaypointDescription(waypoint_id, waypoint_name, waypoint_type,
                                                        waypoint_img, waypoint_content);
        descriptionList.add(w);
    }

    public int GetScheduleID() {
        return scheduleID;
    }
    public void SetScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int GetOwnerID() {
        return ownerID;
    }
    public void SetOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public Double GetRating() {
        return rating;
    }
    public void SetRating(double rating) {
        this.rating = rating;
    }

    public int GetLikes() {
        return likes;
    }
    public void SetLikes(byte likes) {
        this.likes = likes;
    }

    public int GetSharedCount() {
        return sharedCount;
    }
    public void SetSharedCount(int sharedCount) {
        this.sharedCount = sharedCount;
    }

    public Bitmap GetTitleIMG() {
        return titleIMG;
    }
    public void SetTitleIMG(Bitmap titleIMG) {
        this.titleIMG = titleIMG;
    }

    public String GettitleText() {
        return titleText;
    }
    public void SetTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String GetDesctiptionText() {
        return desctiptionText;
    }
    public void SetDesctiptionText(String desctiptionText) {
        this.desctiptionText = desctiptionText;
    }

    public ArrayList<WaypointDescription> GetDescriptionList() {
        return descriptionList;
    }
    public void SetDescriptionList(ArrayList<WaypointDescription> descriptionList) {
        this.descriptionList = descriptionList;
    }

    public class WaypointDescription{
        private int waypoint_id;
        private String waypoint_name;
        private int waypoint_type;
        private Bitmap waypoint_img;
        private String waypoint_content;

        public WaypointDescription(int waypoint_id, String waypoint_name, int waypoint_type,
                                   Bitmap waypoint_img, String waypoint_content){
            this.waypoint_id = waypoint_id;
            this.waypoint_name = waypoint_name;
            this.waypoint_type = waypoint_type;
            this.waypoint_img = waypoint_img;
            this.waypoint_content = waypoint_content;
        }
    }

}

