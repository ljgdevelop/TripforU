package kr.ac.kopo.tripforu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;

public class ActivityWaypoint extends PageController {

    @Override
    protected boolean useToolbar(){
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waypoint);

//        Intent intent = new Intent(this, LayoutNaverMap.class);
//        startActivity(intent);

       FrameLayout contentFrame = findViewById(R.id.FrameLayout_content);
       LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       inflater.inflate(R.layout.layout_naver_map,contentFrame,true);




    }
}