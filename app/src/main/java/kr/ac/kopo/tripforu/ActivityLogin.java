package kr.ac.kopo.tripforu;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;

public class ActivityLogin extends ActivityMain {
    
    ImageButton btn_startWithKakao;
    Button btn_startWithoutLogin;
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // 상단바의 색 변경
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.parseColor("#364a59"));
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        
        
        //카카오로 시작하기
        btn_startWithKakao = (ImageButton) findViewById(R.id.BTN_StartWithKakao);
        btn_startWithKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryKakaoLogin();
            }
        });
        
        //로그인 없이 시작하기
        btn_startWithoutLogin = findViewById(R.id.BTN_StartWithoutLogin);
        btn_startWithoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            
            }
        });
    }
}