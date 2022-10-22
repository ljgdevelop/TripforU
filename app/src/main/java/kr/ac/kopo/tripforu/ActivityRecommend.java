package kr.ac.kopo.tripforu;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.kopo.tripforu.Retrofit.INetTask;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityRecommend extends PageController {
    private FrameLayout recommendDetail;
    private SharedSchedule selectedSchedule;
    private String baseImgUrl = "";
    
    @Override protected boolean useToolbar(){ return true; }
    @Override
    public void onBackPressed() {
        if(((HorizontalScrollView)findViewById(R.id.LAYOUT_Recommend)).getScrollX() > 10) {
            TabHorizontalScroll(findViewById(R.id.LAYOUT_Recommend), 0);
            ResetAppBar();
        }
        else
            super.onBackPressed();
        
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        
        //서버의 이미지를 가져오기 위한 baseUrl
        baseImgUrl = JsonController.readJsonObjFromAssets("json/awsS3.json", getApplicationContext()).get("baseUrl").toString();
        
        //스크롤 뷰를 터치를 통해 이동되지 않도록 오버라이드
        findViewById(R.id.LAYOUT_Recommend).setOnTouchListener(((view, motionEvent) -> {return true;}));
        
        //페이지 너비 조정
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size.x, ViewGroup.LayoutParams.WRAP_CONTENT);
        ((View)findViewById(R.id.LAYOUT_Recommend_Container).getParent()).setLayoutParams(lp);
        ((View)findViewById(R.id.LAYOUT_Recommend_WPContainer).getParent()).setLayoutParams(lp);
        
        //추천 여행 일정 보여주기
        for (SharedSchedule sharedSchedule : INetTask.getInstance().getRecommendScheduleList()) {
            LayoutRecommendBanner banner = new LayoutRecommendBanner(getApplicationContext(), 0, sharedSchedule);
            ((ViewGroup) findViewById(R.id.LAYOUT_Recommend_Container)).addView(banner);
            banner.setOnClickListener(this::onClick);
        }
        
        //추천 일정 상세보기 페이지 가져오기
        recommendDetail = findViewById(R.id.LAYOUT_Recommend_Detail);
    }
    
    /***
     * @author 이제경
     *
     *      추천 일정 클릭 시 사용자가 업로드 한 정보를 토대로 상세보기 페이지 로드
     *
     *      사용자 정보 DB 연동 후 프로필 이미지와 이름 추가할 것.
     */
    private void onClick(View view) {
        //기존 정보 지우기
        ViewGroup vg = findViewById(R.id.LAYOUT_Recommend_Detail_Container);
        vg.removeViews(1, vg.getChildCount() - 1);
        
        //LayoutRecommendBanner 객체와 SharedSchedule 가져오기
        LayoutRecommendBanner banner = (LayoutRecommendBanner) view;
        selectedSchedule = banner.getSharedSchedule();
        
        //앱 바 설정
        SetAppBarAction(0, true, "이전").setOnClickListener(v -> onBackPressed());
        SetAppBarAction(1, false, "가져오기").setOnClickListener(v -> {
            LayoutDialog dialog = new LayoutDialog(getApplicationContext());
            dialog.setDialogTitle("일정 가져오기");
            dialog.setDialogMessage("이 여행 일정을 다운로드 하고 저장하시겠습니까?");
            dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v2 -> dialog.closeDialog());
            dialog.addButton(R.color.TEXT_Black, "확인").setOnClickListener(v2 -> {
                donwloadSchedule();
            });
        });
        
        //머릿글
        ImageView titleImg = recommendDetail.findViewById(R.id.IMG_Recommend_Detail_TitleImg);
        ImageView titleProfile = recommendDetail.findViewById(R.id.IMG_Recommend_Detail_TitleProfile);
        TextView titleText = recommendDetail.findViewById(R.id.TEXT_Recommend_Detail_TitleText);
        TextView titleDesc = recommendDetail.findViewById(R.id.TEXT_Recommend_Detail_TitleDesc);
        TextView titleName = recommendDetail.findViewById(R.id.TEXT_Recommend_Detail_TitleName);
    
        //메인 이미지 설정
        StringBuilder url = new StringBuilder(baseImgUrl);
        url.append(selectedSchedule.getTitleImgId());
        url.append(".jpg");
        Glide.with(getApplicationContext()).load(url.toString()).into(titleImg);
        
        //텍스트 입력
        titleText.setText(selectedSchedule.getTitleText());
        titleDesc.setText(selectedSchedule.getDescriptionText());
        
        //프로필 이름 이미지 삽입
        titleName.setText(ScheduleController.getInstance().getOwnerList(selectedSchedule.getOwnerId())[0]);
        Glide.with(getApplicationContext()).load(ScheduleController.getInstance().getOwnerList(selectedSchedule.getOwnerId())[1])
            .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(60)))
            .into(titleProfile);
    
        //배경의 체인
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recommendDetail.findViewById(R.id.LAYOUT_Recommend_Detail_Chain1).getLayoutParams();
        
        //일정 정보
        LinearLayout detailWP = null;
        for (SharedSchedule.WaypointDescription description :banner.getSharedSchedule().getDescriptionList()) {
            detailWP = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_recommend_waypoint,
                                                                                        findViewById(R.id.LAYOUT_Recommend_Detail_Container), false);
            ImageView waypointIcon = detailWP.findViewById(R.id.IMG_Recommend_WP_Icon);
            ImageView waypointImg = detailWP.findViewById(R.id.IMG_Recommend_WP_Img);
            TextView waypointName = detailWP.findViewById(R.id.TEXT_Recommend_WP_Name);
            TextView waypointDesc = detailWP.findViewById(R.id.TEXT_Recommend_WP_Desc);
            
            int iconRes = ScheduleController.getInstance().getWayPointIcon(ScheduleController.getInstance().getWaypointById(description.getWaypointId()));
            waypointIcon.setImageResource(iconRes);
            
            url = new StringBuilder(baseImgUrl);
            url.append(description.getWaypointImgId()[0]);
            url.append(".jpg");
            Glide.with(getApplicationContext()).load(url.toString()).into(waypointImg);
            
            waypointName.setText(ScheduleController.getInstance().getWaypointById(description.getWaypointId()).GetName());
            waypointDesc.setText(description.getWaypointContent());
    
            ((ViewGroup)findViewById(R.id.LAYOUT_Recommend_Detail_Container)).addView(detailWP);
        }
        
        //배경의 체인 높이 적용
        LinearLayout finalDetailWP = detailWP;
        detailWP.post(() -> {
            lp.height = finalDetailWP.getTop();
            Log.d("TAG", "onClick: " + finalDetailWP.getTop());
            findViewById(R.id.LAYOUT_Recommend_Detail_Chain1).setLayoutParams(lp);
            findViewById(R.id.LAYOUT_Recommend_Detail_Chain2).setLayoutParams(lp);
        });
        
        //페이지 이동
        TabHorizontalScroll(findViewById(R.id.LAYOUT_Recommend), 1);
    }
    
    private void donwloadSchedule(){
        //서버에서 일정을 가져옵니다.
        Schedule schedule = INetTask.getInstance().getSchedule(selectedSchedule.getScheduleId());
        schedule.setSharedSchedule(selectedSchedule);
        if(ScheduleController.getInstance().getMemberByID(-1) == null)
            ScheduleController.getInstance().addMemberToList(new Member(-1, -1));
        schedule.setMemberGroupId(-1);
        ScheduleController.getInstance().addScheduleToDictionary(schedule);
        Log.d("TAG", "donwloadSchedule: " + schedule.getId());
        
        //다이얼로그 종료
        LayoutDialog.instance.closeDialog();
        
        //액티비티 종료 및 새로고침
        refreshActivity(ActivityMain.class);
    }
}