package kr.ac.kopo.tripforu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class editwaypoint extends AppCompatActivity {

    Button btnScroll1, btnScroll2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editwaypoint);

        btnScroll1 = (Button) findViewById(R.id.btnScroll1);
        btnScroll2 = (Button) findViewById(R.id.btnScroll2);

        btnScroll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ActivityAddWaypoint.class);
                startActivity(intent);
            }
        });

    }
}