package kr.ac.kopo.tripforu;

import java.util.ArrayList;

public class SharedSchedule {
    private int scheduleId;
    private long ownerId;
    private double rating;
    private byte likes;
    private int sharedCount;
    private int titleImgId;
    private String titleText;
    private String descriptionText;
    private ArrayList<WaypointDescription> descriptionList = new ArrayList<>();
    
    public SharedSchedule(){}
    
    public SharedSchedule(int scheduleId, long ownerId, double rating, int likes, int sharedCount,
                          int titleImgId, String titleText, String desctriptionText,
                          ArrayList<WaypointDescription> descriptionList){

        this.scheduleId = scheduleId;
        this.ownerId = ownerId;
        this.rating = rating;
        if (likes < Byte.MAX_VALUE)
            this.likes = Byte.parseByte(likes + "");
        this.sharedCount = sharedCount;
        this.titleImgId = titleImgId;
        this.titleText = titleText;
        this.descriptionText = desctriptionText;
        this.descriptionList = descriptionList;
    }

    public void addWaypoint(int waypoint_id, int[] waypoint_img, String waypoint_content){
        WaypointDescription w = new WaypointDescription(waypoint_id, waypoint_img, waypoint_content);
        descriptionList.add(w);
    }

    public int getScheduleId() {
        return scheduleId;
    }
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public byte getLikes() {
        return likes;
    }
    public void setLikes(byte likes) {
        this.likes = likes;
    }

    public int getSharedCount() {
        return sharedCount;
    }
    public void setSharedCount(int sharedCount) {
        this.sharedCount = sharedCount;
    }

    public int getTitleImgId() {
        return titleImgId;
    }
    public void setTitleImgId(int titleImgId) {
        this.titleImgId = titleImgId;
    }

    public String getTitleText() {
        return titleText;
    }
    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }
    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public ArrayList<WaypointDescription> getDescriptionList() {return descriptionList;}
    public void setDescriptionList(ArrayList<WaypointDescription> descriptionList){
        this.descriptionList = descriptionList;
    }


    public class WaypointDescription{
        private int waypointId;
        private int[] waypointImgId = new int[3];
        private String waypointContent;
    
        public WaypointDescription(){}
        
        public WaypointDescription(int waypointId, int[] waypointImgId, String waypointContent){
            this.waypointId = waypointId;
            this.waypointImgId = waypointImgId;
            this.waypointContent = waypointContent;
        }

        public int getWaypointId() {
            return waypointId;
        }
        public void setWaypointId(int waypointId) {
            this.waypointId = waypointId;
        }

        public int[] getWaypointImgId() {
            return waypointImgId;
        }
        public void setWaypointImgId(int[] waypointImgId) {
            this.waypointImgId = waypointImgId;
        }

        public String getWaypointContent() {
            return waypointContent;
        }
        public void setWaypointContent(String waypointContent) {
            this.waypointContent = waypointContent;
        }
    }
}

