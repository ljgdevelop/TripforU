package kr.ac.kopo.tripforu;

import android.os.Bundle;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class LayoutWaypointTimeslt extends AppCompatActivity {

    TimePicker btnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_waypointtimeslt);

        btnTime = (TimePicker) findViewById(R.id.btnTime);

        TimePicker btnTime = (TimePicker) findViewById(R.id.btnTime);
        btnTime.setIs24HourView(true);

    }


}
