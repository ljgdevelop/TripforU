package kr.ac.kopo.tripforu;

import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LayoutNewSchSetting extends PageController{
    public void strengthMinus(){
        TextView text_NewSch_Strength = findViewById(R.id.TEXT_NewSch_Strength);
        findViewById(R.id.BTN_NewSch_Minus).setOnClickListener(view -> {
            int i = Integer.parseInt(text_NewSch_Strength.getText().toString());
            if (i > 2){
                i -= 1;
                text_NewSch_Strength.setText(Integer.toString(i));
            }
        });
    }

    public void strengthPlus(){
        TextView text_NewSch_Strength = findViewById(R.id.TEXT_NewSch_Strength);
        findViewById(R.id.BTN_NewSch_Plus).setOnClickListener(view -> {
            int i = Integer.parseInt(text_NewSch_Strength.getText().toString());
            if (i < 20){
                i += 1;
                text_NewSch_Strength.setText(Integer.toString(i));
            }
        });
    }

    public void appbarClick(Schedule schedule){
        TextView text_NewSch_Strength = findViewById(R.id.TEXT_NewSch_Strength);
        EditText edt_NewSch_Title = findViewById(R.id.EDT_NewSch_Title);
        SetAppBarAction(1, false, "다음").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedule.setName(edt_NewSch_Title.getText().toString());
                schedule.setMemberGroupId(Integer.parseInt(text_NewSch_Strength.getText().toString()));
            }
        });
        SetAppBarAction(0, true, "이전").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
