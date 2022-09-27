package kr.ac.kopo.tripforu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class ActivityUserShare extends PageController {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usershare);

        ExtendedFloatingActionButton acbtn_AddContent = findViewById(R.id.ACBTN_AddContent);
        View v1 = pageAdepter.SetAppBarAction(2, false, "완료");
        View v2 = pageAdepter.SetAppBarAction(0, true, "취소");
        v1.setOnClickListener(v3 -> {

        });
        v2.setOnClickListener(v4 -> {
            finish();
        });
        acbtn_AddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityUserShareContents.class);
                Intent subIntent = getIntent();
                Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");
                intent.putExtra("putSchedule", putSchedule);
                view.getContext().startActivity(intent);
            }
        });
    }
}
