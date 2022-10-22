package kr.ac.kopo.tripforu;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Schedule implements Serializable {
    private int id;
    private String name;
    private String destination;
    private int days;
    private String startDate;
    private ArrayList<Waypoint> wayPointList = new ArrayList<>();
    private int memberGroupId;
    private boolean isShared;
    private SharedSchedule sharedSchedule;
    
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
            for (Waypoint wp:ScheduleController.getInstance().getAllWaypointValues()) {
                if(wp.GetId() == Integer.parseInt(wpId)){
                    this.wayPointList.add(wp);
                }
            }
        }
        this.isShared = false;
    }
    
    public Schedule(){}
    
    //id 변수의 getter setter
    public int getId(){
        return this.id;
    }
    
    //name 변수의 getter setter
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    //destination 변수의 getter setter
    public String getDestination(){
        return this.destination;
    }
    public void setDestination(String destination){
        this.destination = destination;
    }
    
    //days 변수의 getter setter
    public int getDays(){
        return this.days;
    }
    public void setDays(byte days){
        this.days = days;
    }
    public void setDays(int days){
        this.days = (byte)days;
    }
    
    //startDate 변수의 getter setter
    public String getStartDate(){
        return this.startDate;
    }
    public void setStartDate(){
        this.startDate = startDate;
    }
    
    //wayPointId 변수의 getter setter adder remover
    public ArrayList<Waypoint> getWayPointList(){
        return this.wayPointList;
    }
    public Waypoint getWayPointFromId(int i){
        return this.wayPointList.get(i);
    }
    public void addWayPoint(Waypoint waypoint){
        this.wayPointList.add(waypoint);
    }
    public void removeWayPointById(int id){
        for(int i = 0; i < wayPointList.size(); i ++){
            if(this.wayPointList.get(i).GetId() == id)
                this.wayPointList.remove(i);
        }
    }
    public void clearWayPoint(){
        this.wayPointList.clear();
    }
    
    //isShared 변수의 getter setter
    public boolean checkIsShared(){ return this.isShared; }
    public void setSharedState(boolean isShared){ this.isShared = isShared; }
    
    public int getMemberGroupId() {
        return memberGroupId;
    }
    public void setMemberGroupId(int memberGroupId) {
        this.memberGroupId = memberGroupId;
    }
    
    public SharedSchedule getSharedSchedule() {
        return sharedSchedule;
    }
    
    public void setSharedSchedule(SharedSchedule sharedSchedule) {
        this.sharedSchedule = sharedSchedule;
    }
}