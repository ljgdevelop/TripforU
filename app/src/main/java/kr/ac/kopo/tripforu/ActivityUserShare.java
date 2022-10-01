package kr.ac.kopo.tripforu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ActivityUserShare extends PageController {

    @Override protected boolean useToolbar(){ return true; }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usershare);
        AddPage(this);

        // 첫 페이지 앱바
        SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 1));
        SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickHandler(view1, 0));

        ExtendedFloatingActionButton acbtn_AddContent = findViewById(R.id.ACBTN_AddContent);
        acbtn_AddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScrollView layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContents);
                Intent subIntent = getIntent();
                Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");
                layout_UserShareContent.setVisibility(View.VISIBLE);
                View layout_UserShare = findViewById(R.id.LAYOUT_UserShareContents);

                // 스케쥴 waypoint 리스트 생성
                ShareWaypointClickListner(layout_UserShare, putSchedule);

                // 두번째 페이지 앱바
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2));
                SetAppBarAction(0, true, "");
            }
        });

        ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
        imgbtn_TitleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,0);
            }
        });

        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
        imgbtn_ContentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,1);
            }
        });
    }

    // 갤러리에서 이미지 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1 || requestCode == 0){
            if (resultCode == RESULT_OK){
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    if (requestCode == 0){
                        ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
                        imgbtn_TitleImage.setImageBitmap(img);
                    }else if(requestCode == 1){
                        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
                        imgbtn_ContentImage.setImageBitmap(img);
                    }
                }catch (Exception e){ }
            }
            else if (resultCode == RESULT_CANCELED){ }
        }
    }

    // 앱바 터치 기능문
    private void onClickHandler(View v, int i){
        LinearLayout layout_UserShareContentDetail = findViewById(R.id.LAYOUT_UserShareContentDetail);
        View layout_UserShare = findViewById(R.id.LAYOUT_UserShareContents);
        ScrollView layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContents);
        EditText edt_ContentDetailText = findViewById(R.id.EDT_ContentDetailText);
        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
        Intent subIntent = getIntent();
        Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");
        switch (i){
            case 0:
                //1페이지 취소ㅓ
                finish();
                break;
            case 1:
                //1페이지 확인
                finish();
                // 첫 페이지 사진, 제목, 머릿글 값 가져오기
                ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
                EditText edt_TitleText = findViewById(R.id.EDT_TitleText);
                EditText edt_ContentText = findViewById(R.id.EDT_ContentText);


                String titleText = edt_TitleText.getText().toString();
                String ContentText = edt_ContentText.getText().toString();
                break;
            case 2:
                //2페이지 확인
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 1));
                SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickHandler(view1, 0));
                layout_UserShareContent.setVisibility(View.GONE);

                break;
            case 3:
                //3페이지 확인
                layout_UserShareContentDetail.setVisibility(View.GONE);
                ShareWaypointClickListner(layout_UserShare, putSchedule);
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2));
                SetAppBarAction(0, true, "");
                String ContentDetailText = edt_ContentDetailText.getText().toString();
                edt_ContentDetailText.setText("");
                imgbtn_ContentImage.setImageResource(R.drawable.ic_tempimagesize);
                break;
            case 4:
                //3페이지 이전
                layout_UserShareContentDetail.setVisibility(View.GONE);
                ShareWaypointClickListner(layout_UserShare, putSchedule);
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2));
                SetAppBarAction(0, true, "");
                edt_ContentDetailText.setText("");
                imgbtn_ContentImage.setImageResource(R.drawable.ic_tempimagesize);
                break;
        }
    }

    //여행지 정보 생성
    private static ArrayList ShareWaypointInfo(View view, Schedule schedule){
        final LinearLayout wpContainer = view.findViewById(R.id.LAYOUT_UserShareContent);
        wpContainer.removeAllViewsInLayout();
        ArrayList<View> arrayList = new ArrayList<View>();
        for(Waypoint waypoint : schedule.GetWayPointList()){

            View layoutWP = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_disc, null);
            arrayList.add(layoutWP);

            final TextView wpName = (TextView) layoutWP.findViewById(R.id.TEXT_ShareDestination);
            ImageView wpIcon = (ImageView) layoutWP.findViewById(R.id.IMG_ShareIcon);
            wpName.setText(waypoint.GetName());
            int imgSrc = -1;
            switch (waypoint.GetType()){
                case 1:
                    imgSrc = R.drawable.ic_waypoint_home;
                    break;
                case 2:
                    imgSrc = R.drawable.ic_waypoint_circle;
                    break;
                case 3:
                    imgSrc = R.drawable.ic_waypoint_train;
                    break;
                case 4:
                    imgSrc = R.drawable.ic_waypoint_subway;
                    break;
                case 5:
                    imgSrc = R.drawable.ic_waypoint_car;
                    break;
                case 6:
                    imgSrc = R.drawable.ic_waypoint_hotel;
                    break;
                case 7:
                    imgSrc = R.drawable.ic_waypoint_restaurant;
                    break;
                case 8:
                    imgSrc = R.drawable.ic_waypoint_pin;
                    break;
                default:
                    imgSrc = R.drawable.ic_waypoint_pin;
                    break;
            }
            wpIcon.setImageResource(imgSrc);

            wpContainer.addView(layoutWP);
        }
        return arrayList;
    }

    //여행지 정보 적용 및 클릭 리스너 적용
    private void ShareWaypointClickListner(View view , Schedule schedule){
        ArrayList<View> wpViewList = ShareWaypointInfo(view, schedule);
        for (int i = 0; i < wpViewList.size(); i++){
            View wpView = wpViewList.get(i);
            wpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout wpContainer = findViewById(R.id.LAYOUT_UserShareContent);
                    wpContainer.removeAllViewsInLayout();
                    LinearLayout layout_UserShareContentDetail = findViewById(R.id.LAYOUT_UserShareContentDetail);
                    layout_UserShareContentDetail.setVisibility(View.VISIBLE);
                    LinearLayout layout_UserShareWP = findViewById(R.id.LAYOUT_UserShareWP);
                    layout_UserShareWP.removeAllViewsInLayout();
                    layout_UserShareWP.addView(wpView);
                    SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 3));
                    SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickHandler(view1, 4));

                }
            });
        }
    }
}
