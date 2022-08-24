package kr.ac.kopo.tripforu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class ActivityMain extends ControllerPage implements OnBackPressedListener{
    //this context
    static Context context;
    
    ControllerSchedule activityController = new ControllerSchedule();
    
    //네비게이션 바
    Button btn_NavBarPrt_Schedule, btn_NavbarChd_NewSch, btn_NavbarChd_AllSch, btn_NavbarChd_RecSch,
           btn_login;
    LinearLayout layout_UserProfile;
    
    //남은 여행 일정 티켓의 레이아웃들
    ObjectAnimator scrollAnimator;
    View scheduleInfo;
    int[] scheduleTickets = {R.id.LAYOUT_SchTicket1, R.id.LAYOUT_SchTicket2, R.id.LAYOUT_SchTicket3};
    static int scrollTime = 0;
    int oldX = 0;
    int posX[] = new int[0];
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 환경변수 초기화
        btn_NavBarPrt_Schedule = findViewById(R.id.BTN_NavBarPrt_Schedule);
        btn_NavbarChd_NewSch = findViewById(R.id.BTN_NavbarChd_NewSch);
        btn_NavbarChd_AllSch = findViewById(R.id.BTN_NavbarChd_AllSch);
        btn_NavbarChd_RecSch = findViewById(R.id.BTN_NavbarChd_RecSch);
        layout_UserProfile = findViewById(R.id.LAYOUT_UserProfile);
        btn_login = findViewById(R.id.BTN_Login);
        btn_NavBarPrt_Schedule.setTag("closed");
        context = this.getApplicationContext();
        HorizontalScrollView[] horizontalScroll = {findViewById(R.id.VIEW_HorizontalScroll1), findViewById(R.id.VIEW_HorizontalScroll2)};
        horizontalScroll[0].setTag(0);
        horizontalScroll[1].setTag(0);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        ControllerSchedule.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //카카오 SDK 초기화
        String kakao_app_key = getResources().getString(R.string.kakao_app_key);
        KakaoSdk.init(this, kakao_app_key);
    
        //로그인 상태에 따라 로그인 메뉴 표시
        CheckClientHasToken();
        
        //여행일정 티켓 표시
        ControllerSchedule.AddWaypointFromJSON(ReadJson("json/waypoints.json"));
        ControllerSchedule.AddScheduleFromJSON(ReadJson("json/schedule.json"));
        ControllerSchedule.AddMemberFromJSON(ReadJson("json/member.json"));
        ShowScheduleTickets();
        
        
        //
        AddPage(new Page(ActivityMain.this, TYPE_ACTIVITY));
    
        
        //스크롤 뷰에 스냅 효과 및 애니메이션 적용
        for (HorizontalScrollView v : horizontalScroll) {
            v.setOnTouchListener((view, motionEvent) -> {
                if(motionEvent.getAction() == MotionEvent.ACTION_MOVE ){
                    scrollTime++;
                    if(scrollTime < 2)
                        oldX = (int)motionEvent.getX();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    //스크롤 뷰 속 컨테이너의 X좌표 가져오기
                    scrollTime = 0;
                    int count = 0;
                    LinearLayout parent = ((LinearLayout)v.getChildAt(0));
                    for(int i = 0; i < parent.getChildCount(); i++){
                        if(parent.getChildAt(i).getVisibility() == View.VISIBLE) {
                            count++;
                        }
                    }
                    posX = new int[count];
                    for (int c = 0; c < posX.length; c++) {
                        int id = getResources().getIdentifier("LAYOUT_SchTicket" + (c + 1),
                            "id", getApplicationContext().getPackageName());
                        posX[c] = v.findViewById(id).getLeft();
                    }
                    if(oldX - (int)motionEvent.getX() > 50 &&
                        (int)v.getTag() < count - 1){//오른쪽으로 이동
                        v.setTag((int)v.getTag() + 1);
                    }
                    else if(oldX - (int)motionEvent.getX() < -50 &&
                        (int)v.getTag() > 0){//왼쪽으로 이동
                        v.setTag((int)v.getTag() - 1);
                    }
                    Log.d("TAG", "onTouch: " + count);
                    if(scrollAnimator != null && scrollAnimator.isRunning())
                        scrollAnimator.end();
                    scrollAnimator = ObjectAnimator.ofInt(v, "ScrollX", posX[(int)v.getTag()]);
                    scrollAnimator.setDuration(400);
                    scrollAnimator.start();
                }
                return true;
            });
        }
    
        UserApiClient.getInstance().accessTokenInfo((accessTokenInfo, error) -> {
            if (error != null) {
                Log.e("TAG", "토큰 정보 보기 실패", error);
            }
            else if (accessTokenInfo != null) {
                Log.i("TAG", "토큰 정보 보기 성공" +
                    "\n회원번호: " + accessTokenInfo.getId() +
                    "\n만료시간: " + accessTokenInfo.getExpiresIn() + " 초");
            }
            return null;
        });
        
        UserApiClient.getInstance().me( true, (user, error) -> {
            if (error != null) {
                Log.e("TAG", "사용자 정보 요청 실패", error);
            }
            else if (user != null) {
                Log.i("TAG", "사용자 정보 요청 성공");
                Log.i("TAG", "회원번호: " + user.getId());
                Log.i("TAG", "이메일: " + user.getKakaoAccount().getEmail());
                Log.i("TAG", "닉네임: " + user.getKakaoAccount().getProfile().getNickname());
                Log.i("TAG", "프로필사진: " + user.getKakaoAccount().getProfile().getProfileImageUrl());
                
                int layoutHeight = ConvertSPtoPX(findViewById(R.id.IMG_KakaoProfile).getContext(), 50) +
                    ConvertDPtoPX((findViewById(R.id.LAYOUT_UserProfile).getContext()), 42);
                
                ((TextView)findViewById(R.id.TEXT_KakaoName)).setText(user.getKakaoAccount().getProfile().getNickname());
                ((TextView)findViewById(R.id.TEXT_KakaoAdress)).setText(user.getKakaoAccount().getEmail());
                //Glide.with(this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into((ImageView) findViewById(R.id.IMG_KakaoProfile));
                findViewById(R.id.LAYOUT_UserProfile).getLayoutParams().height = layoutHeight;
                LayerDrawable drawable=
                    (LayerDrawable) getApplicationContext().getDrawable(R.drawable.background_white_rcorner_15);
                findViewById(R.id.IMG_KakaoProfile).setBackground(drawable);
                findViewById(R.id.IMG_KakaoProfile).setClipToOutline(true);
//                findViewById(R.id.IMG_KakaoProfile).setOutlineProvider(new ViewOutlineProvider() {
//                    @Override
//                    public void getOutline(View view, Outline outline) {
//                        //outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), ConvertSPtoPX(findViewById(R.id.IMG_KakaoProfile).getContext(), 15));
//
//                    }
//                });
            }
            return null;
        });
    
        
        
        // 상단바의 색 변경
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        
        ControllerAppBar appBarController = new ControllerAppBar(this);
        appBarController.settingSideNavBar();
        // 네비게이션 바
        Button[] navBarChd = {btn_NavbarChd_NewSch, btn_NavbarChd_AllSch, btn_NavbarChd_RecSch};
        ((DrawerLayout)findViewById(R.id.LAYOUT_Drawer)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        findViewById(R.id.LAYOUT_NavView).setClickable(false);
        btn_NavBarPrt_Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_NavBarPrt_Schedule.getTag().toString() == "closed") {
                    btn_NavBarPrt_Schedule.setTag("opend");
                    btn_NavBarPrt_Schedule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
                    for(Button btn : navBarChd){
                        btn.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    btn_NavBarPrt_Schedule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                    btn_NavBarPrt_Schedule.setTag("closed");
                    for(Button btn : navBarChd){
                        btn.setVisibility(View.GONE);
                    }
                }
            }
        });
        
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(login);
            }
        });
    
    
        //서버와 연결
        new Thread(() -> {
            try {
                Socket sk = new Socket("192.168.34.35" , 6060) ;
                Log.d("ServerConnection", "onCreate: 연결 성공");
                OutputStream ous = sk.getOutputStream();
                DataOutputStream dous = new DataOutputStream(ous);
    
                String cmd = "<LOGINCHECK/><ID>admin</ID><PW>1234</PW>";
                byte[] buff = cmd.getBytes();
                dous.write(buff);
                
                while(sk.getKeepAlive()){
                    InputStream ins = sk.getInputStream();
                    DataInputStream dins = new DataInputStream(ins);
    
                    byte[] buf = new byte[1024];
                    dins.read(buf);
                    String response = new String(buf).trim();
                    
                    Log.d("ServerConnection", "heartbeat: " + response);
                }
                //서버가 종료되었을 경우, 인터넷 접속이 끊긴 경우
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : json확장자 파일을 url을 통해 접근하여 내용을 String으로 반환
     * -> 인자 : fileUrl = JSON파일이 있는 경로
     */
    private String ReadJson(String fileUrl){
        try{
            InputStream is = getAssets().open(fileUrl);
            int fileSize = is.available();
        
            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();
        
            String json = new String(buffer, "UTF-8");
            Log.d("--  json = ", json);
            
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 메인 페이지에서 남은 여행 일정의 티켓을 보여주는 함수
     * - 클릭시 해당 여행 일정의 상세 내용을 표시
     */
    private void ShowScheduleTickets(){
        for(int i = 0; i < ControllerSchedule.remainingSchedule.size(); i ++){
            if(i > 2)
                break;
            Schedule thisSchedule =  ControllerSchedule.remainingSchedule.get(i);
            View ticket = findViewById(scheduleTickets[i]);
            Member member = ControllerSchedule.GetMemberByID(thisSchedule.GetMemberGroupId());
            
            ticket.setVisibility(View.VISIBLE);
            
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Title)).setText(thisSchedule.GetName());
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Course)).setText("여행 코스");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Count)).setText(member.GetUserIdList().size() + "명");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Days)).setText(thisSchedule.GetDays() + "일");
            ((TextView)ticket.findViewById(R.id.TEXT_SchTicket_Date)).setText(thisSchedule.GetStartDate() + "일 출발");
            
            ticket.findViewById(R.id.BTN_SchTicket).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    ControllerSchedule.ShowScheduleInfo(findViewById(R.id.LAYOUT_MainContainer), thisSchedule);
                }
            });
        }
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
            Intent intent = getIntent();
            finish();
            startActivity(intent);
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
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 사용자가 로그인 상태인지, 또 사용자의 정보를 가져올 수 있는 상태인지 확인
     */
    private void CheckClientHasToken(){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if(throwable != null){
                    //로그인 필요
                    btn_login.setVisibility(View.VISIBLE);
                    layout_UserProfile.setVisibility(View.GONE);
                }
                else{
                    //로그인 상태
                    btn_login.setVisibility(View.GONE);
                    layout_UserProfile.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                }
                return null;
            });
        }
        else {
            //로그인 필요
            btn_login.setVisibility(View.VISIBLE);
            layout_UserProfile.setVisibility(View.GONE);
        }
    }
    
    public static int ConvertSPtoPX(@NonNull Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    
    public static int ConvertDPtoPX(@NonNull Context context, int dp) {
        return Math.round((float) dp * context.getResources().getDisplayMetrics().density);
    }
}
