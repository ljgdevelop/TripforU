package kr.ac.kopo.tripforu;

import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LayoutWaypointSettime extends AppCompatActivity {

    TimePicker btnTime;
    View dialogView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_waypointsettime);

        btnTime = (TimePicker) findViewById(R.id.btnTime);

        TimePicker btnTime = (TimePicker) findViewById(R.id.btnTime);
        btnTime.setIs24HourView(true);

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogView = (View) View.inflate(LayoutWaypointSettime.this,
                        R.layout.layout_dialog_waypointtimeslt, null);
                AlertDialog.Builder dig = new AlertDialog.Builder(LayoutWaypointSettime.this);
                dig.setView(dialogView);
                dig.setPositiveButton("확인", null);
                dig.setNegativeButton("취소", null);
                dig.show();

            }
        });


    }


}

