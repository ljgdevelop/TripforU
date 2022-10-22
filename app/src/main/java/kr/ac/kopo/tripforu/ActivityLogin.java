package kr.ac.kopo.tripforu;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class ActivityLogin extends PageController {
    ImageButton btn_startWithKakao;
    Button btn_startWithoutLogin;
    
    @Override protected boolean useToolbar(){ return false; }
    
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
                finish();
            }
        });
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 카카오톡 로그인 시도, 카카오톡 어플이 설치되어 있을경우 앱을 통해 로그인
     * - 설치되어 있지 않을경우 카카오톡 계정을 통해 로그인 할 수 있는 웹 페이지로 이동
     */
    public void TryKakaoLogin(){
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(getApplicationContext())) {
            UserApiClient.getInstance().loginWithKakaoTalk(this, callback);
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(this, callback);
        }
    }
    
    /***
     * -> 작성자 : Kakao Developers (https://developers.kakao.com/docs/latest/ko/kakaologin/android)
     */
    private Function2<OAuthToken, Throwable, Unit> callback = (oAuthToken, throwable) -> {
        if (oAuthToken != null) {
            Log.i("[카카오] 로그인", "성공");
            updateKakaoLogin();
            //checkClientHasToken();
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            finish();
            startActivity(intent);
            ServerController.getInstance().updateUserData();
            
        }
        if (throwable != null) {
            Log.i("[카카오] 로그인", "실패");
            Log.e("signInKakao()", throwable.getLocalizedMessage());
        }
        return null;
    };
    
    /***
     * -> 작성자 : Kakao Developers (https://developers.kakao.com/docs/latest/ko/kakaologin/android)
     */
    private void updateKakaoLogin() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null) {
                // @brief : 로그인 성공
                Log.i("[카카오] 로그인 정보", user.toString());
                // @brief : 로그인한 유저의 email주소와 token 값 가져오기. pw는 제공 X
                String email = user.getKakaoAccount().getEmail();
                Log.i("[카카오] 로그인 정보", email + "");
            }
            else {
                // @brief : 로그인 실패
            } return null;
        });
    }
}