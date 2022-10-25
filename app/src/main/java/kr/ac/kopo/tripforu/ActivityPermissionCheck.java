package kr.ac.kopo.tripforu;

import androidx.annotation.RequiresApi;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityPermissionCheck extends PageController {
    Button btn_permissionCheck;
    @Override protected boolean useToolbar(){ return false; }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_check);
        
        btn_permissionCheck = findViewById(R.id.BTN_permissionCheck);
        btn_permissionCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WAKE_LOCK},
                    1000);
                
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
    
                prefs = getSharedPreferences("Pref", MODE_PRIVATE);
                prefs.edit().putBoolean("isFirstRun",false).apply();
                finish();
            }
        });
    }
}