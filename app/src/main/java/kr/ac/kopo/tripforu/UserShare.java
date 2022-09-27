package kr.ac.kopo.tripforu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class UserShare extends PageController {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usershare);

        ExtendedFloatingActionButton acbtn_AddContent = findViewById(R.id.ACBTN_AddContent);
        AddPage(this);

        View v1 = SetAppBarAction(2, false, "완료");
        View v2 = SetAppBarAction(0, true, "취소");
        v1.setOnClickListener(v3 -> {

        });
        v2.setOnClickListener(v4 -> {
            finish();
        });

        acbtn_AddContent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                ScrollView layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContents);
                Intent subIntent = getIntent();
                Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");
                layout_UserShareContent.setVisibility(View.VISIBLE);
                View layout_usershare = findViewById(R.id.LAYOUT_UserShareContents);
                ShareAddWaypointInfo(layout_usershare, putSchedule);
                View v1 = SetAppBarAction(1, false, "확인");
                View v2 = SetAppBarAction(0, true, "이전");
                v1.setOnClickListener(v3 -> {

                });
                v2.setOnClickListener(v4 -> {
                    layout_UserShareContent.setVisibility(View.GONE);
                    View v5 = SetAppBarAction(2, false, "완료");
                    View v6 = SetAppBarAction(0, true, "취소");
                    v5.setOnClickListener(v7 -> {

                    });
                    v6.setOnClickListener(v8 -> {
                        finish();
                    });
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void ShareAddWaypointInfo(View view, Schedule schedule){
        final LinearLayout wpContainer = view.findViewById(R.id.LAYOUT_UserShareContent);
        wpContainer.removeAllViewsInLayout();
        for(Waypoint waypoint : schedule.GetWayPointList()){

            View layoutWP = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_disc, null);
            LinearLayout layout_UserShare = layoutWP.findViewById(R.id.LAYOUT_UserShare);
            layout_UserShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
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
        }
    }
}
