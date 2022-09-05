package kr.ac.kopo.tripforu;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ScheduleContent extends AppCompatActivity {
    public Application thisApplication;
    private String id;
    private String name;
    private String destination;
    private String memberGroupId;
    private double ratingBar;
    private String grade;
    private String startDate;
    private String likes;
    private String sharedCount;
    private String isShared;
    private int Number;
    public ArrayList<ScheduleContent> scheduleList = new ArrayList();

    // json 파일 읽어오기
    public String getJsonString() {
        try {
            InputStream is = thisApplication.getAssets().open("jsons/UserScheduleList.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "x-windows-949");

            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    //id 변수의 getter setter
    protected String GetId() {
        return id;
    }


    //name 변수의 getter setter

    protected String GetName() {
        return name;
    }

    protected void SetName(String name) {
        this.name = name;
    }


    //destination 변수의 getter setter

    protected String GetDestination() {
        return destination;
    }

    protected void SetDestination(String destination) {
        this.destination = destination;
    }


    //memberGroupID 변수의 getter setter

    protected String GetMemberGroupId() {
        return memberGroupId;
    }

    protected void SetMemberGroupId(String memberGroupId) {
        this.memberGroupId = memberGroupId;
    }


    //ratimgBar 변수의 getter setter

    protected double GetRatingBar() {
        return ratingBar;
    }

    protected void SetRatingBar(double ratingBar) {
        this.ratingBar = ratingBar;
    }


    //grade 변수의 getter setter

    protected String GetGrade() {
        return grade;
    }

    protected void SetGrade(String grade) {
        this.grade = grade;
    }


    //startDate 변수의 getter setter

    protected String GetStartDate() {
        return startDate;
    }

    protected void SetStartDate(String startDate) {
        this.startDate = startDate;
    }


    //likes 변수의 getter setter

    protected String GetLikes() {
        return likes;
    }

    protected void SetLikes(String likes) {
        this.likes = likes;
    }


    //isShared 변수의 getter

    protected String GetIsShared() {
        return isShared;
    }

    protected void SetIsShared(String isShared) {
        this.isShared = isShared;
    }

    //sharedCount 변수의 getter setter

    protected String GetSharedCount() {
        return sharedCount;
    }

    protected void SetSharedCount(String sharedCount) {
        this.sharedCount = sharedCount;
    }


    protected int GetNumber() {
        return Number;
    }

    protected void SetNumber(int Number) {
        this.Number = Number;
    }

    public ArrayList jsonParsing(String json) {
        try {
            ArrayList<ScheduleContent> scheduleList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);

            JSONArray scheduleContentArray = jsonObject.getJSONArray("UserSchedule");

            for (int i = 0; i < scheduleContentArray.length(); i++) {
                JSONObject scheduleContentObject = scheduleContentArray.getJSONObject(i);

                ScheduleContent scheduleContent = new ScheduleContent();
                scheduleContent.SetName(scheduleContentObject.getString("name"));
                scheduleContent.SetDestination(scheduleContentObject.getString("destination"));
                scheduleContent.SetMemberGroupId(scheduleContentObject.getString("memberGroupId"));
                scheduleContent.SetRatingBar(scheduleContentObject.getDouble("ratingBar"));
                scheduleContent.SetGrade(scheduleContentObject.getString("grade"));
                scheduleContent.SetStartDate(scheduleContentObject.getString("startDate"));
                scheduleContent.SetLikes(scheduleContentObject.getString("likes"));
                scheduleContent.SetSharedCount(scheduleContentObject.getString("sharedCount"));
                scheduleContent.SetIsShared(scheduleContentObject.getString("isShared"));

                scheduleList.add(scheduleContent);
            }
            return scheduleList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
