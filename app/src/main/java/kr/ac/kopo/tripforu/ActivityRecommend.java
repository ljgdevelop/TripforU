package kr.ac.kopo.tripforu;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.kopo.tripforu.Retrofit.INetTask;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityRecommend extends PageController {
    FrameLayout recommendDetail;
    String baseImgUrl = "";
    
    @Override protected boolean useToolbar(){ return true; }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
    
        //추천 일정 목록 받아오기
        
        
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
        
        //추천 여행 일정 받아오기
        /*ScheduleController.syncJsonToObject(JsonController.readJsonArrayFromAssets("json/sharedSchedule.json", getApplicationContext()),
            SharedSchedule.class.toString());*/
        
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
        //LayoutRecommendBanner 객체와 SharedSchedule 가져오기
        LayoutRecommendBanner banner = (LayoutRecommendBanner) view;
        SharedSchedule sharedSchedule = banner.getSharedSchedule();
        
        //머릿글
        ImageView titleImg = recommendDetail.findViewById(R.id.IMG_Recommend_Detail_TitleImg);
        ImageView titleProfile = recommendDetail.findViewById(R.id.IMG_Recommend_Detail_TitleProfile);
        TextView titleText = recommendDetail.findViewById(R.id.TEXT_Recommend_Detail_TitleText);
        TextView titleDesc = recommendDetail.findViewById(R.id.TEXT_Recommend_Detail_TitleDesc);
        TextView titleName = recommendDetail.findViewById(R.id.TEXT_Recommend_Detail_TitleName);
    
        //메인 이미지 설정
        StringBuilder url = new StringBuilder(baseImgUrl);
        url.append(sharedSchedule.getTitleImgId());
        url.append(".jpg");
        Glide.with(getApplicationContext()).load(url.toString()).into(titleImg);
        
        //텍스트 입력
        titleText.setText(sharedSchedule.getTitleText());
        titleDesc.setText(sharedSchedule.getDescriptionText());
        /*여기에 프로필 이미지와 이름 추가할 것.*/
    
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
}