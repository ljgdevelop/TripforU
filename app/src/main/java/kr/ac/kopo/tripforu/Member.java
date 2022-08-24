package kr.ac.kopo.tripforu;

import java.util.ArrayList;

class Member{
    private int id;
    private ArrayList<Integer> userIdList = new ArrayList<>();
    private ArrayList<Integer> memoIdList = new ArrayList<>();
    private int scheduleId;
    
    //생성자
    public Member(int id, int scheduleId){
        this.id = id;
        this.userIdList = new ArrayList<Integer>();
        this.memoIdList = new ArrayList<Integer>();
        this.scheduleId = scheduleId;
    }
    //생성자 : JSON Object 입력
    public Member(Object id, Object scheduleId){
        this.id = (int)id;
        this.userIdList = new ArrayList<Integer>();
        this.memoIdList = new ArrayList<Integer>();
        this.scheduleId = (int)scheduleId;
    }
    
    public int GetId(){
        return id;
    }
    
    public ArrayList<Integer> GetUserIdList(){
        return userIdList;
    }
    public void AddUserIdInList(int id){
        this.userIdList.add(id);
    }
    public void RemoveUserIdInList(int id){
        for(int i = 0; i < userIdList.size(); i ++){
            if(this.userIdList.get(i) == id)
                this.userIdList.remove(i);
        }
    }
    
    public ArrayList<Integer> GetMemoList(){
        return memoIdList;
    }
    
    public int GetScheduleId(){
        return scheduleId;
    }
}