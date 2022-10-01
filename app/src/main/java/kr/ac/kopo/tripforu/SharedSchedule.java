package kr.ac.kopo.tripforu;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class SharedSchedule {
    public int scheduleID;
    public double rating;
    public byte likes;
    public int sharedCount;
    public Bitmap titleIMG;
    public String titleText;
    public String desctiptionText;
    public ArrayList<WaypointDescription> descriptionList = new ArrayList<>();

    public SharedSchedule(int scheduleID, double rating, int likes, int sharedCount,
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

    private class WaypointDescription{
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

