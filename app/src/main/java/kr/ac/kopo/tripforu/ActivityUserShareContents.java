package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityUserShareContents extends PageController {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersharecontents);
        Intent subIntent = getIntent();
        Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");

        View v1 = pageAdepter.SetAppBarAction(0, true, "이전");
        View v2 = pageAdepter.SetAppBarAction(1, false, "다음");
        v1.setOnClickListener(v3 -> {
            finish();
        });
        v2.setOnClickListener(v4 -> {

        });

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void ReadWaypointInfo(View view, Schedule schedule){
        final LinearLayout wpContainer = view.findViewById(R.id.LAYOUT_UserShareContent);
        schedule.GetWayPointList().forEach(waypoint -> {
            View layoutWP = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_disc, null);

            final TextView wpName = (TextView) layoutWP.findViewById(R.id.TEXT_ShareDestination);
            ImageView wpIcon = (ImageView) layoutWP.findViewById(R.id.IMG_ShareIcon);
            wpName.setText(waypoint.GetName());

            int imgSrc = -1;
            switch (waypoint.GetType()){
                case 1:
                    imgSrc = R.drawable.ic_waypoint_home;
                    break;
                case 2:
                    imgSrc = R.drawable.ic_waypoint_circle;
                    break;
                case 3:
                    imgSrc = R.drawable.ic_waypoint_train;
                    break;
                case 4:
                    imgSrc = R.drawable.ic_waypoint_subway;
                    break;
                case 5:
                    imgSrc = R.drawable.ic_waypoint_car;
                    break;
                case 6:
                    imgSrc = R.drawable.ic_waypoint_hotel;
                    break;
                case 7:
                    imgSrc = R.drawable.ic_waypoint_restaurant;
                    break;
                case 8:
                    imgSrc = R.drawable.ic_waypoint_pin;
                    break;
                default:
                    imgSrc = R.drawable.ic_waypoint_pin;
                    break;
            }
            wpIcon.setImageResource(imgSrc);

            wpContainer.addView(layoutWP);
        });
    }
}