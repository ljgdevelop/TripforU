package kr.ac.kopo.tripforu;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class WaypointTimeslt extends AppCompatActivity {

    TimePicker btnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_waypointtimeslt);

        btnTime = (TimePicker) findViewById(R.id.btnTime);

        TimePicker btnTime = (TimePicker) findViewById(R.id.btnTime);
        btnTime.setIs24HourView(true);

    }


}
