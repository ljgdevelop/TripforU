package kr.ac.kopo.tripforu;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.kopo.tripforu.Service.MyJobService;

import kr.ac.kopo.tripforu.Retrofit.INetTask;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PageController extends AppCompatActivity implements OnBackPressedListener{
    public boolean isLoggedIn = false;
    final static byte TYPE_ACTIVITY = 0;
    final static byte TYPE_VIEW = 1;
    byte TYPE_HIDEANDSHOW = 2;
    public SharedPreferences prefs;
    public static Settings settings = new Settings();
    public static Context context;
    
    protected static final int JOB_ID = 0x1000;
    
    protected static FrameLayout fullView = null;
    
    /***
     * @author 이제경
     * @return 툴바 사용 여부
     * <p>
     * 다른 액티비티에서 앱바를 보여줄지를 정할 수 있게해주는 함수
     * 해당 액티비티에서 오버라이드 해서 사용
     * 모든 액티비티와 연결된 자바파일에 적용
     * </p>
     * <p>
     * 사용법 : 해당 자바파일에서 Class 영역 안에 
     * @Override protected boolean useToolbar(){ return true; } 추가
     * 이때 true면 화면 상단에 앱바가 나타나고 false면 안 나타남
     * </p>
     */
    protected boolean useToolbar(){
        return true;
    }
    
    @Override
    public void startActivity(Intent i){
        super.startActivity(i);
    }
    
    @Override
    public void setContentView(int layoutResID){
        fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        View child = getLayoutInflater().inflate(layoutResID, activityContainer, true);
        
        super.setContentView(fullView);
    
        context = getApplicationContext();
        
        ResetAppBar();
        Toolbar toolbar = fullView.findViewById(R.id.LAYOUT_AppBar);
    
        setToolBarSearchAction();
        
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) activityContainer.getLayoutParams();
        if(useToolbar()){
            setSupportActionBar(toolbar);
            lp.topMargin = ConvertDPtoPX(getApplicationContext(), 56);
            activityContainer.setLayoutParams(lp);
        } else {
            toolbar.setVisibility(View.GONE);
            lp.topMargin = 0;
            activityContainer.setLayoutParams(lp);
        }
    }
    
    private void setToolBarSearchAction(){
        fullView.findViewById(R.id.IMG_AppBarGoBack).setOnClickListener(v -> {
            ViewGroup layout_TABBUTTON = fullView.findViewById(R.id.LAYOUT_TABBUTTON);
            for (int i = 0; i < layout_TABBUTTON.getChildCount(); i++){
                if (((TextView)layout_TABBUTTON.getChildAt(i).findViewById(R.id.IMG_NAVtext)).getCurrentTextColor() == getColor(R.color.APP_Main)){
                    TabHorizontalScroll(fullView.findViewById(R.id.VIEW_MainPageTabPage),i);
                }
            }
            ResetAppBar();
        });
        fullView.findViewById(R.id.IMG_AppBarErase).setOnClickListener(v ->
            ((TextView)fullView.findViewById(R.id.TEXT_AppBarSearchText)).setText(""));
    
        EditText editText = (EditText) fullView.findViewById(R.id.TEXT_AppBarSearchText);
        editText.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE){
                onAppBarSearchListener(editText.getText().toString());
                return true;
            }
            return false;
        });
    }

    /**
     * 앱바 좌, 우에 원하는 텍스트 버튼 배치
     *
     * @author 이제경
     * @param type - 텍스트뷰의 색 지정(0:검정, 1:positive, 2:negative)
     * @param isLeft - 텍스트뷰의 위치 지정(true:왼쪽, false:오른쪽)
     * @param text - 텍스트뷰의 내용
     * @return 설정된 텍스트뷰(View 클래스) 반환
     */
    protected View SetAppBarAction(int type, boolean isLeft, String text){
        String color;
        int id;
        switch (type){
            case 0://default black
                color = "#5E5E5E";
                break;
            case 1://positive
                color = "#43992a";
                break;
            case 2://negative
                color = "#ff8888";
                break;
            case 3://inactivated
                color = "#868E96";
                break;
            default:
                return null;
        }
        
        if(isLeft){
            id = R.id.TEXT_AppBarLeft;
            fullView.findViewById(R.id.TEXT_AppBarTittle).setVisibility(View.GONE);
        }
        else{
            id = R.id.TEXT_AppBarRight;
            fullView.findViewById(R.id.IMG_AppBarRight).setVisibility(View.GONE);
        }
    
        TextView view = fullView.findViewById(id);
        view.setTextColor(Color.parseColor(color));
        view.setVisibility(View.VISIBLE);
        view.setText(text);
        return view;
    }
    
    /**
     * @author 이제경
     *
     *      앱바를 기본 상태로 초기화
     */
    protected void ResetAppBar(){
        //키보드가 올라와있으면 내리기
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullView.findViewById(R.id.TEXT_AppBarSearchText).getWindowToken(), 0);
        
        fullView.findViewById(R.id.TEXT_AppBarLeft).setVisibility(View.GONE);
        fullView.findViewById(R.id.TEXT_AppBarRight).setVisibility(View.GONE);
        fullView.findViewById(R.id.IMG_AppBarGoBack).setVisibility(View.VISIBLE);
        fullView.findViewById(R.id.TEXT_AppBarTittle).setVisibility(View.VISIBLE);
        fullView.findViewById(R.id.IMG_AppBarGoBack).setOnClickListener(v -> {
            ViewGroup layout_TABBUTTON = fullView.findViewById(R.id.LAYOUT_TABBUTTON);
            for (int i = 0; i < layout_TABBUTTON.getChildCount(); i++){
                if (((TextView)layout_TABBUTTON.getChildAt(i).findViewById(R.id.IMG_NAVtext)).getCurrentTextColor() == getColor(R.color.APP_Main)){
                    TabHorizontalScroll(fullView.findViewById(R.id.VIEW_MainPageTabPage),i);
                    ResetAppBar();
                    break;
                }
            }
        });
        fullView.findViewById(R.id.IMG_AppBarRight).setVisibility(View.VISIBLE);
        fullView.findViewById(R.id.LAYOUT_AppBarSearch).setVisibility(View.GONE);
        fullView.findViewById(R.id.IMG_AppBarRight).setOnClickListener(v -> {
            fullView.findViewById(R.id.LAYOUT_AppBarSearch).setVisibility(View.VISIBLE);
            EditText editText = (EditText)fullView.findViewById(R.id.TEXT_AppBarSearchText);
            editText.requestFocus();
            imm.showSoftInput(editText, 0);
            fullView.findViewById(R.id.LAYOUT_SearchView).setVisibility(View.VISIBLE);
            TabHorizontalScroll(fullView.findViewById(R.id.VIEW_MainPageTabPage),3);
            LayoutScheduleSearch layoutScheduleSearch = new LayoutScheduleSearch();
            layoutScheduleSearch.searchLayoutBtn();
            //정다빈은 보아라
        });
    }
    
    /**
     * @author 이제경
     *
     *      앱바에서 검색시의 리스너
     *      오버라이드 하여 사용
     */
    protected void onAppBarSearchListener(String text){
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullView.findViewById(R.id.TEXT_AppBarSearchText).getWindowToken(), 0);
    }
    
    protected void startJobService(){
        JobScheduler jobScheduler =
            (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(JOB_ID,
            new ComponentName(this, kr.ac.kopo.tripforu.Service.MyJobService.class))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPeriodic(DateUtils.HOUR_IN_MILLIS)
            .setPersisted(true)
            .build());
    }

    public void cancelJobService(){
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
        Log.d("TAG", "cancelJob: 작업취소");
    }
    ObjectAnimator scrollAnimator;
    static int scrollTime = 0;
    /**
     * @author 이제경
     * @param horizontalScrollView - HorizontalScrollView
     * @param index - 스크롤 할 위치(뷰의 순서 기준)
     */
    protected void TabHorizontalScroll(HorizontalScrollView horizontalScrollView, int index){
        scrollTime = 0;
        int count = 0;
        ArrayList<Integer> posX = new ArrayList<>();
        LinearLayout parent = ((LinearLayout)horizontalScrollView.getChildAt(0));
        for(int i = 0; i < parent.getChildCount(); i++){
            if(parent.getChildAt(i).getVisibility() == View.VISIBLE) {
                parent.getChildAt(i).setTag(count++);
                posX.add(parent.getChildAt(i).getLeft());
            }
        }
        horizontalScrollView.setTag(index);
        if(scrollAnimator != null && scrollAnimator.isRunning())
            scrollAnimator.end();
        scrollAnimator = ObjectAnimator.ofInt(horizontalScrollView, "ScrollX", posX.get((int)horizontalScrollView.getTag()));
        scrollAnimator.setDuration(400);
        scrollAnimator.start();
    }
    
    /***
     * @author 이제경
     *
     *      카카오톡으로 로그인 되어있는지 확인한 후,
     *      로그인 상태라면 사용자 설정 화면에 로그인 정보 표시
     */
    protected void checkClientHasToken(){
        if (AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().me((user, throwable) -> {
                if(throwable != null){
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.VISIBLE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.GONE);
                    this.isLoggedIn = false;
                }
                else if (user != null) {
                    this.isLoggedIn = true;
                    /*Log.i("TAG", "사용자 정보 요청 성공");
                    Log.i("TAG", "회원번호: " + user.getId());
                    Log.i("TAG", "이메일: " + user.getKakaoAccount().getEmail());
                    Log.i("TAG", "닉네임: " + user.getKakaoAccount().getProfile().getNickname());
                    Log.i("TAG", "프로필사진: " + user.getKakaoAccount().getProfile().getProfileImageUrl());*/
                    Glide.with(this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into((ImageView) findViewById(R.id.IMG_Profile));
                    ((TextView)findViewById(R.id.TEXT_AccountName)).setText(user.getKakaoAccount().getProfile().getNickname());
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.GONE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.BTN_GoLogin).setVisibility(View.VISIBLE);
                    findViewById(R.id.BTN_GoLogout).setVisibility(View.GONE);
                    this.isLoggedIn = false;
                }
                return null;
            });
        }
        else {
            this.isLoggedIn = false;
        }
    }
    
    protected void logoutKakao(){
        UserApiClient.getInstance().logout(throwable -> {
            if(throwable == null){
                Intent i = new Intent(this, ActivityMain.class);
                finish();
                startActivity(i);
            }
            return null;
        });
    }
    
    /***
     * @author 이제경
     *
     *      앱을 처음 실행하는지 확인.
     *      처음 실행시 권한 동의화면으로 이동
     */
    public void checkFirstRun(){
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun){
            Intent i = new Intent(getApplicationContext(), ActivityPermissionCheck.class);
            startActivityForResult(i, 0);
            JsonController.saveJsonFromAssets(getApplicationContext());
    
            ArrayList<Settings> tempArray = new ArrayList<>();
            tempArray.add(settings);
            JsonController.saveJson(tempArray, "settings", getApplicationContext());
    
            //서버에서 관광지 목록 가져오기
            new Thread(() -> {
                for (Waypoint wp : INetTask.getInstance().getWayPoints("")) {
                    ScheduleController.getInstance().addWaypointToList(wp);
                }
                ScheduleController.getInstance().saveWaypoint();
            }).start();
        }
        else{
            //Json형식의 데이터 동기화
            ScheduleController.syncJsonToObject(JsonController.readJson("member", getApplicationContext()),
                Member.class.toString());
            ScheduleController.syncJsonToObject(JsonController.readJson("waypoints", getApplicationContext()),
                Waypoint.class.toString());
            ScheduleController.syncJsonToObject(JsonController.readJson("schedule", getApplicationContext()),
                Schedule.class.toString());
        }
    }
    
    public static Settings saveSettings(){
        ArrayList<Settings> tempArray = new ArrayList<>();
        tempArray.add(settings);
        JsonController.saveJson(tempArray, "settings", context);
        return settings;
    }
    
    public static Settings syncSetting(){
        ArrayList<Settings> settingList =
            new Gson()
                .fromJson(JsonController.readJson("settings", context).toJSONString(), new TypeToken<ArrayList<Settings>>() {}.getType());
        settings = settingList.get(0);
        return settings;
    }
    
    public static int ConvertSPtoPX(@NonNull Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    
    public static int ConvertDPtoPX(@NonNull Context context, int dp) {
        return Math.round((float) dp * context.getResources().getDisplayMetrics().density);
    }
    
    public static int ConvertPXtoDP(@NonNull Context context, int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = px * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(dp);
    }
    
    /***
     * @author 이제경
     * @param view - 태그를 적용 할 view
     * @param tag - 태그의 이름
     * @param value - 저장할 값
     *
     *      <p>view에 key-value 쌍으로 되어있는 Json형태의 데이터로 tag-value 저장</p>
     *      <p/>
     *      <p>HashMap처럼 여러개의 태그 저장 가능, 이미 있는 태그 이름으로 시도할 시 value를 덮어씌웁니다.</p>
     */
    public static void setTagToView(Object view, String tag, Object value){
        View v = ((View) view);
        if(v.getTag() == null || v.getTag().toString().isEmpty())
            v.setTag("\"" + tag + "\":" + value);
        else if(!v.getTag().toString().contains("\"" + tag + "\":")){
            v.setTag(v.getTag() + ", \"" + tag + "\":" + value);
        }
        else{
            String afterTag = v.getTag().toString().split("\"" + tag + "\":")[1];
            StringBuilder result = new StringBuilder();
            result.append(v.getTag().toString().split("\"" + tag + "\":")[0]);
            result.append("\"");
            result.append(tag);
            result.append("\":");
            result.append(value);
            if(afterTag.contains(",")) {
                result.append(",");
                result.append(afterTag.split(",")[1]);
            }
            v.setTag(result);
        }
    }
    
    /***
     * @author 이제경
     * @param view - 태그를 적용 할 view
     * @param tag - 태그의 이름
     * @return (String) 저장된 값
     *
     *      <p>view에 저장된 tag를 key로 갖는 value를 가져옵니다.</p>
     */
    public static String getTagFromView(Object view, String tag){
        View v = ((View) view);
        if(v.getTag() == null || !v.getTag().toString().contains(tag))
            return "";
        return v.getTag().toString().split("\"" + tag + "\":")[1].split(",")[0];
    }

    /***
     * @author 이제경
     * @param pastSchContainer - 지난 일정 컨테이너
     * @param remainSchContainer - 남은 일정 컨테이너
     *      목록 화면에 여행 일정들을 표시합니다.
     */
    boolean isSelectMode = false;
    protected void showScheduleList(LinearLayout pastSchContainer, LinearLayout remainSchContainer, int check){
        ArrayList<Schedule> scheduleList = ScheduleController.getSortedScheduleByDate();

        if(scheduleList.size() < 1)
            return;
        if(isPassed(scheduleList.get(0).getStartDate()) && check == 0) {
            remainSchContainer.removeAllViewsInLayout();
            ((TextView)findViewById(R.id.TEXT_MainRemain)).setText("남은 여행 일정");
        }
        
        pastSchContainer.removeAllViewsInLayout();
        int thisYear = 9999;
        int mostCloseScheduleId = 0;
        long mostCloseTime = 0;
        for (Schedule sch : scheduleList) {
            LayoutScheduleTicket newTicket = new LayoutScheduleTicket(ActivityMain.context);
            newTicket.setScheduleId(sch.getId());
            remainSchedule(sch, pastSchContainer, remainSchContainer, newTicket, check);

            //년도가 바뀔때마나 표시
            int scheduleYear = Integer.parseInt(sch.getStartDate().split("-")[0]);
            if(thisYear > scheduleYear){
                thisYear = scheduleYear;
                newTicket.findViewById(R.id.TEXT_TicketDateHeader).setVisibility(View.VISIBLE);
                ((TextView)newTicket.findViewById(R.id.TEXT_TicketDateHeader)).setText(thisYear + "년");
            }

            try {
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = transFormat.parse(sch.getStartDate());
                Date now = new Date();

                if(now.getTime() - date.getTime() < mostCloseTime){
                    mostCloseTime = now.getTime() - date.getTime();
                    mostCloseScheduleId = sch.getId();
                }

            }catch (Exception e){
                Log.e("TAG", "showScheduleList: ", e);
            }
        }
    }
    
    /***
     * @author 이제경
     * @param pastSchContainer - 지난 일정 담는 컨테이너 LinearLayout
     * @param remainSchContainer - 남은 일정 담는 컨테이너 LinearLayout
     * @param newTicket - 추가될 뷰
     *
     *      선택모드를 활성화합니다.
     */
    private void setSelectMode(LinearLayout pastSchContainer, LinearLayout remainSchContainer,
                               LayoutScheduleTicket newTicket, int check){
        //길게 터치시 선택모드 진입
        newTicket.setOnLongClickListener(view -> {
            if(!isSelectMode) {
                isSelectMode = true;
                setTagToView(pastSchContainer, "isSelectMode", true);
                setTagToView(newTicket, "isSelected", true);
                newTicket.findViewById(R.id.LAYOUT_TicketBG).setBackground(getResources().getDrawable(R.drawable.background_ticket_selected));
                
                //선택모드 진입 시 취소, 삭제 버튼 출력
                SetAppBarAction(0, true, "취소").setOnClickListener(v -> cancelSelectMode());
                SetAppBarAction(2, false, "삭제").setOnClickListener(v -> {
                    //삭제 확인 출력
                    LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                    dialog.setDialogTitle("삭제하시겠습니까?");
                    dialog.setDialogMessage("이 작업은 되돌릴 수 없습니다.");
                    dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v2 -> dialog.closeDialog());
                    dialog.addButton(R.color.TEXT_Red, "삭제").setOnClickListener(v2 -> {
                        setTagToView(pastSchContainer, "isSelectMode", false);
                        isSelectMode = false;
                        for (int i = pastSchContainer.getChildCount() - 1; i >= 0; i--) {
                            if(getTagFromView(pastSchContainer.getChildAt(i), "isSelected").equals("true")){
                                ScheduleController.getInstance().removeScheduleById(((LayoutScheduleTicket)pastSchContainer.getChildAt(i)).getScheduleId());
                            }
                        }
                        pastSchContainer.removeAllViewsInLayout();
                        showScheduleList(pastSchContainer, remainSchContainer, check);
                        ResetAppBar();
                        dialog.closeDialog();
                    });
                });
            }
            return true;
        });
    }

    /***
     * @author 이제경
     *
     *      선택 모드를 해제합니다.
     */
    public void cancelSelectMode(){
        LinearLayout container = findViewById(R.id.LAYOUT_SchListContainer);
        setTagToView(container, "isSelectMode", false);
        isSelectMode = false;
        for (int i = 0; i < container.getChildCount(); i ++) {
            setTagToView(container.getChildAt(i), "isSelected", false);
            container.getChildAt(i).findViewById(R.id.LAYOUT_TicketBG).setBackground(getResources().getDrawable(R.drawable.background_ticket_new));
        }
        ResetAppBar();
    }

    /***
     * @author 정다빈
     * @param schedule - 생성할 뷰의 스케쥴
     * @param pastSchContainer - 지난 일정 담는 컨테이너 LinearLayout
     * @param remainSchContainer - 남은 일정 담는 컨테이너 LinearLayout
     * @param newTicket - 추가될 뷰
     */
    private void remainSchedule(Schedule schedule, LinearLayout pastSchContainer,
                                LinearLayout remainSchContainer, LayoutScheduleTicket newTicket,
                                int check){
        //메인 화면에 남은 일정 표시
        if (isPassed(schedule.getStartDate())){
            remainSchContainer.addView(newTicket);
            newTicket.setOnLongClickListener(v -> {
                LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                dialog.setDialogTitle("삭제하시겠습니까?");
                dialog.setDialogMessage("이 작업은 되돌릴 수 없습니다.");
                dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v2 -> dialog.closeDialog());
                dialog.addButton(R.color.TEXT_Red, "삭제").setOnClickListener(v2 -> {
                    ScheduleController.getInstance().removeScheduleById(newTicket.getScheduleId());
                    ((TextView)findViewById(R.id.TEXT_MainRemain)).setText("여행 일정이 아직 없어요!");
                    newTicket.removeAllViews();
                    dialog.closeDialog();
                });
                return true;
            });
        }else {
            pastSchContainer.addView(newTicket);
            setSelectMode(pastSchContainer, remainSchContainer, newTicket, check);
        }
    }
    
    private boolean isPassed(String date){
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        int intDate = Integer.parseInt(dateFormat.format(now));
        
        return intDate < Integer.parseInt(date.replaceAll("-",""));
    }
    
    /***
     * @author 이제경
     * @param cls - 새로고침 할 액티비티의 클래스
     * <p>
     *      액티비티를 새로고침합니다.
     * </p>
     */
    public void refreshActivity(Class<?> cls){
        finish();//인텐트 종료
        overridePendingTransition(0, 0);//인텐트 효과 없애기
        Intent intent = new Intent(this, cls); //인텐트
        startActivity(intent); //액티비티 열기
    }
    public void refreshActivity(){
        finish();//인텐트 종료
        overridePendingTransition(0, 0);//인텐트 효과 없애기
        Intent intent = getIntent(); //인텐트
        startActivity(intent); //액티비티 열기
    }
}
