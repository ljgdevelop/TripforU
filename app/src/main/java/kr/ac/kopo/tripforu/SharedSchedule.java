package kr.ac.kopo.tripforu;

import java.util.ArrayList;

public class SharedSchedule {
    private int scheduleId;
    private int ownerId;
    private double rating;
    private byte likes;
    private int sharedCount;
    private int titleImgId;
    private String titleText;
    private String desctiptionText;
    private ArrayList<WaypointDescription> descriptionList = new ArrayList<>();

    public SharedSchedule(int scheduleId, int ownerId, double rating, int likes, int sharedCount,
                          int titleImgId, String titleText, String desctiptionText,
                          ArrayList<WaypointDescription> descriptionList){

        this.scheduleId = scheduleId;
        this.ownerId = ownerId;
        this.rating = rating;
        if (likes < Byte.MAX_VALUE)
            this.likes = Byte.parseByte(likes + "");
        this.sharedCount = sharedCount;
        this.titleImgId = titleImgId;
        this.titleText = titleText;
        this.desctiptionText = desctiptionText;
        this.descriptionList = descriptionList;
    }

    public void addWaypoint(int waypoint_id, int waypoint_img, String waypoint_content){
        WaypointDescription w = new WaypointDescription(waypoint_id, waypoint_img, waypoint_content);
        descriptionList.add(w);
    }

    public int GetScheduleID() {
        return scheduleId;
    }
    public void SetScheduleID(int scheduleID) {
        this.scheduleId = scheduleID;
    }

    public int GetOwnerID() {
        return ownerId;
    }
    public void SetOwnerID(int ownerID) {
        this.ownerId = ownerID;
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

    public int GetTitleImgID() {
        return titleImgId;
    }
    public void SetTitleImgID(int titleImgID) {
        this.titleImgId = titleImgID;
    }

    public String GetTitleText() {
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
        private int waypointId;
        private int waypointImgId;
        private String waypointContent;

        public WaypointDescription(int waypointId, int waypointImgId, String waypointContent){
            this.waypointId = waypointId;
            this.waypointImgId = waypointImgId;
            this.waypointContent = waypointContent;
        }

        public int GetWaypointId() {
            return waypointId;
        }
        public void SetWaypointId(int waypointId) {
            this.waypointId = waypointId;
        }

        public int GetWaypointImgId() {
            return waypointImgId;
        }
        public void SetWaypointImgId(int waypointImgId) {
            this.waypointImgId = waypointImgId;
        }

        public String GetWaypointContent() {
            return waypointContent;
        }
        public void SetWaypointContent(String waypointContent) {
            this.waypointContent = waypointContent;
        }
    }
}

