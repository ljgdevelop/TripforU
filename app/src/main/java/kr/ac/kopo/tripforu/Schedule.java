package kr.ac.kopo.tripforu;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

class Schedule implements Serializable {
    private int id;
    private String name;
    private String destination;
    private int days;
    private String startDate;
    private ArrayList<Waypoint> wayPointList = new ArrayList<>();
    private int memberGroupId;
    private boolean isShared;
    
    //생성자 : wayPointList 포함
    public Schedule(int id, String name, String destination, int days, String startDate,
                    ArrayList<Waypoint> wayPointList){
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.days = days;
        this.startDate = startDate;
        this.wayPointList = wayPointList;
        this.isShared = false;
    }
    //생성자 : wayPointList 미포함
    public Schedule(int id, String name, String destination, int days, String startDate){
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.days = days;
        this.startDate = startDate;
        this.isShared = false;
    }
    //생성자 : JSON Object 입력
    public Schedule(Object id, Object name, Object destination, Object days, Object startDate,
                    Object wayPointString){
        this.id = (int)id;
        this.name = (String)name;
        this.destination = (String)destination;
        this.days = (int)days;
        this.startDate = (String)startDate;
        String[] wpStrSplitted = wayPointString.toString().split("/");
        for (String wpId: wpStrSplitted) {
            for (Waypoint wp:ScheduleController.waypointList) {
                if(wp.GetId() == Integer.parseInt(wpId)){
                    this.wayPointList.add(wp);
                }
            }
        }
        this.isShared = false;
    }
    
    //id 변수의 getter setter
    public int GetId(){
        return this.id;
    }
    
    //name 변수의 getter setter
    public String GetName(){
        return this.name;
    }
    public void SetName(String name){
        this.name = name;
    }
    
    //destination 변수의 getter setter
    public String GetDestination(){
        return this.destination;
    }
    public void SetDestination(String destination){
        this.destination = destination;
    }
    
    //days 변수의 getter setter
    public int GetDays(){
        return this.days;
    }
    public void SetDays(byte days){
        this.days = days;
    }
    public void SetDays(int days){
        this.days = (byte)days;
    }
    
    //startDate 변수의 getter setter
    public String GetStartDate(){
        return this.startDate;
    }
    public void SetStartDate(){
        this.startDate = startDate;
    }
    
    //wayPointId 변수의 getter setter adder remover
    public ArrayList<Waypoint> GetWayPointList(){
        return this.wayPointList;
    }
    public Waypoint GetWayPointFromId(int i){
        return this.wayPointList.get(i);
    }
    public void AddWayPoint(Waypoint waypoint){
        this.wayPointList.add(waypoint);
    }
    public void RemoveWayPointById(int id){
        for(int i = 0; i < wayPointList.size(); i ++){
            if(this.wayPointList.get(i).GetId() == id)
                this.wayPointList.remove(i);
        }
    }
    public void ClearWayPoint(){
        this.wayPointList.clear();
    }
    
    //isShared 변수의 getter setter
    public boolean CheckIsShared(){ return this.isShared; }
    public void SetSharedState(boolean isShared){ this.isShared = isShared; }
    
    public int getMemberGroupId() {
        return memberGroupId;
    }
    public void setMemberGroupId(int memberGroupId) {
        this.memberGroupId = memberGroupId;
    }
}