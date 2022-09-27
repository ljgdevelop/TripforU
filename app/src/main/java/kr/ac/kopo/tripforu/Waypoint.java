package kr.ac.kopo.tripforu;

import java.io.Serializable;

class Waypoint implements Serializable {
    private int id;
    private String name;
    private double posX;
    private double posY;
    private byte rating;
    private int reviewCount;
    private int type;
    private String originLink;
    private int time;
    
    public Waypoint(int id, String name, double posX, double posY, byte rating, int reviewCount,
                    int type, String originLink, int time){
        this.id = id;
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.type = type;
        this.originLink = originLink;
        this.time = time;
    }
    //생성자 : JSON Object 입력
    public Waypoint(Object id, Object name ,Object posX, Object posY, Object reviewCount, Object type,
                    Object rating, Object originalLink, Object time){
        this.id = (int)id;
        this.name = (String)name;
        this.posX = (double)posX;
        this.posY = (double)posY;
        this.reviewCount = (int)reviewCount;
        this.type = (int)type;
        this.rating = (byte) ((int)rating);
        this.originLink = (String)originalLink;
        this.time = (int)time;
    }
    
    public int GetId(){
        return this.id;
    }
    public void SetId(int id) {
        this.id = id;
    }
    
    public String GetName(){
        return this.name;
    }
    public void SetName(String name) {
        this.name = name;
    }
    
    public double GetPosX(){
        return this.posX;
    }
    public void SetPosX(double posX){
        this.posX = posX;
    }
    
    public double GetPosY(){
        return this.posY;
    }
    public void SetPosY(double posY){
        this.posY = posY;
    }
    
    public byte GetRating(){
        return rating;
    }
    public void SetRating(byte rating){
        this.rating = rating;
    }
    public void AddRating(byte rating){
        this.rating = (byte)((this.rating * this.reviewCount + rating) / this.reviewCount);
    }
    
    public int GetReviewCount(){
        return this.reviewCount;
    }
    public void SetReviewCount(int reviewCount){
        this.reviewCount = reviewCount;
    }
    
    public int GetType(){
        return this.type;
    }
    public void SetType(int type){
        this.type = type;
    }
    
    public String GetOriginalLink(){
        return originLink;
    }
    public void SetOriginalLink(String originLink){
        this.originLink = originLink;
    }
    
    public int GetTime(){
        return this.time;
    }
    public void SetTime(int minuteTime){
        this.time = minuteTime;
    }
}
