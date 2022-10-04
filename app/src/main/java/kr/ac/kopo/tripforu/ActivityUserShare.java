package kr.ac.kopo.tripforu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ActivityUserShare extends PageController implements Cloneable{

    @Override protected boolean useToolbar(){ return true; }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usershare);
        AddPage(this);

        //시작 초기 설정
        StartSeting();

        // 첫 페이지 앱바
        SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 1,  null, null));
        SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickHandler(view1, 0,  null, null));

        onFABClick();

        ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);

        onImageClick(imgbtn_TitleImage, 0);
        onImageClick(imgbtn_ContentImage, 1);
    }

    //이미지 클릭 후 사진 추가 기능
    private void onImageClick(ImageButton imageButton, int i){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, i);
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
            else if (resultCode == RESULT_CANCELED){
                if (requestCode == 0){
                    ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
                    imgbtn_TitleImage.setImageBitmap(null);
                }else {
                    ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
                    imgbtn_ContentImage.setImageBitmap(null);
                }
            }
        }
    }

    //  floatingActionButton 터치 기능
    private void onFABClick(){
        ExtendedFloatingActionButton acbtn_AddContent = findViewById(R.id.ACBTN_AddContent);
        acbtn_AddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                ScrollView layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContents);

                if (getTagFromView(view, "layoutCheck").equals("true")){
                    layout_UserShareContent.setVisibility(View.VISIBLE);
                } else {
                    setTagToView(view, "layoutCheck", true);
                    Intent subIntent = getIntent();
                    Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");
                    layout_UserShareContent.setVisibility(View.VISIBLE);
                    View layout_UserShare = findViewById(R.id.LAYOUT_UserShareContents);

                    // 스케쥴 waypoint 리스트 생성

                    ShareWaypointInfo(layout_UserShare, putSchedule);
                    ShareWaypointClickListner(bitmapArrayList);
                }

                // 두번째 페이지 앱바
                SetAppBarAction(1, false, "완료").setOnClickListener(View -> onClickHandler(view, 2, null, null));
                SetAppBarAction(0, true, "");
            }
        });
    }

    // 앱바 터치 기능문
    private void onClickHandler(View v, int i, View v2, ArrayList<Bitmap> bitmapArrayList){
        LinearLayout layout_UserShareContentDetail = findViewById(R.id.LAYOUT_UserShareContentDetail);
        ViewGroup layout_SharedContent = findViewById(R.id.LAYOUT_SharedContent);
        LinearLayout layout_UserShare_WayPoint = findViewById(R.id.LAYOUT_UserShare_Waypoint);
        ScrollView layout_UserShareContents = findViewById(R.id.LAYOUT_UserShareContents);
        EditText edt_ContentDetailText = findViewById(R.id.EDT_ContentDetailText);
        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
        ArrayList<SharedSchedule.WaypointDescription> waypointDescriptions = new ArrayList<>();
        SharedSchedule sharedSchedule = new SharedSchedule(0,0,0,0,0,
                                                    0,"","",waypointDescriptions);

        switch (i){
            case 0:
                //1페이지 취소
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                        dialog.setDialogTitle("취소하시겠습니까?");
                        dialog.setDialogMessage("작성하신 내용은 삭제됩니다.");
                        dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v -> dialog.closeDialog());
                        dialog.addButton(R.color.Negative, "확인").setOnClickListener(v -> {
                            dialog.closeDialog();
                            finish();
                        });
                    }
                });
                break;
            case 1:
                //1페이지 확인
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                        dialog.setDialogTitle("공유하시겠습니까?");
                        dialog.setDialogMessage("작성하신 내용이 다른 사용자에게 공유됩니다.");
                        dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v -> dialog.closeDialog());
                        dialog.addButton(R.color.Negative, "확인").setOnClickListener(v -> {
                            dialog.closeDialog();
                            finish();
                        });
                    }
                });
                // 첫 페이지 사진, 제목, 머릿글 값 가져오기
                ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
                EditText edt_TitleText = findViewById(R.id.EDT_TitleText);
                EditText edt_ContentText = findViewById(R.id.EDT_ContentText);
                String titleText = edt_TitleText.getText().toString();
                String ContentText = edt_ContentText.getText().toString();
                /*이미지 서버로 보낸 후 그 서버에서 이미지 불러올 때 주는 id값 저장 밑에 waypoint_IMG 값에 넣기 **********/
                /*sharedSchedule.addWaypoint(
                        Integer.parseInt(getTagFromView(v, "layout_WaypointId")),
                        0,
                        edt_ContentDetailText.getText().toString());*/
                EditText editText;
                if (layout_SharedContent != null){
                    for (int a = 0; a < layout_SharedContent.getChildCount(); a++){
                        Integer.parseInt(getTagFromView(layout_SharedContent.getChildAt(a), "layout_WaypointId"));
                        // 이미지 서버에 보낸 후 Id값 가져오기
                        editText = layout_SharedContent.getChildAt(a).findViewById(R.id.EDT_ContentDetailText);
                        String contentText = editText.getText().toString();
                    }
                }
                break;
            case 2:
                //2페이지 확인
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 1, null, null));
                SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickHandler(view1, 0, null, null));
                layout_UserShareContents.setVisibility(View.GONE);
                break;
            case 3:
                //3페이지 이전
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2, null, null));
                SetAppBarAction(0, true, "");
                layout_UserShareContentDetail.setVisibility(View.GONE);

                layout_UserShare_WayPoint.removeAllViewsInLayout();
                ShareWaypointClickListner(bitmapArrayList);
                edt_ContentDetailText.setText("");

                imgbtn_ContentImage.setImageBitmap(null);

                break;
            case 4:
                //3페이지 확인
                layout_UserShareContentDetail.setVisibility(View.GONE);
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2, null, null));
                SetAppBarAction(0, true, "");

                v.findViewById(R.id.LAYOUT_UserShareStroke).setBackgroundResource(R.color.TEXT_Gray);

                BitmapDrawable tempBitmapIMG = (BitmapDrawable)((ImageButton) findViewById(R.id.IMGBTN_ContentImage)).getDrawable();
                Bitmap bitmapImg = tempBitmapIMG.getBitmap();
                bitmapArrayList.add(bitmapImg);

                layout_UserShare_WayPoint.removeAllViewsInLayout();

                imgbtn_ContentImage.setImageBitmap(null);
                setTagToView(v, "layout_Shared",true);

                v2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
                v2.setBackground(null);
                setTagToView(v2, "layout_WaypointId",getTagFromView(v,"layout_WaypointId"));
                ImageButton imgbtn_DeleteBtn = v2.findViewById(R.id.IMGBTN_Handle);
                imgbtn_DeleteBtn.setVisibility(View.VISIBLE);
                imgbtn_DeleteBtn.setImageResource(R.drawable.ic_cross);
                setTagToView(imgbtn_DeleteBtn, "viewId", getTagFromView(v,"layout_WaypointId"));
                DeleteButtonSet(imgbtn_DeleteBtn);

                ShareWaypointClickListner(bitmapArrayList);
                edt_ContentDetailText.setText("");
                layout_SharedContent.addView(v2);
                break;
        }
    }

    //여행지 정보 생성
    private static void ShareWaypointInfo(View view, Schedule schedule){
        final ViewGroup waypointContainer = view.findViewById(R.id.LAYOUT_UserShareContent);
        waypointContainer.removeAllViewsInLayout();
        for(Waypoint waypoint : schedule.getWayPointList()){
            View layoutWP = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_desc, null);

            final TextView wpName = (TextView) layoutWP.findViewById(R.id.TEXT_ShareDestination);
            ImageView wpIcon = (ImageView) layoutWP.findViewById(R.id.IMG_ShareIcon);

            setTagToView(layoutWP, "layout_WaypointId", waypoint.GetId());
            setTagToView(layoutWP, "layout_Shared", false);

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

            waypointContainer.addView(layoutWP);
        }
    }

    //여행지 정보 적용 및 뷰 클릭 리스너 적용
    private void ShareWaypointClickListner(ArrayList<Bitmap> bitmapArrayList){
        ViewGroup layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContent);
        for (int i = 0; i < layout_UserShareContent.getChildCount(); i++){
            View wpView = layout_UserShareContent.getChildAt(i);

            switch (getTagFromView(wpView, "layout_Shared")){
                case "true":
                    wpView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                default:
                    onClickView(wpView, bitmapArrayList);
            }
        }
    }

    // 뷰 클릭 리스너
    private void onClickView(View view, ArrayList<Bitmap> bitmapArrayList){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout_UserShareContentDetail = findViewById(R.id.LAYOUT_UserShareContentDetail);
                layout_UserShareContentDetail.setVisibility(View.VISIBLE);
                LinearLayout layout_UserShare_WayPoint = findViewById(R.id.LAYOUT_UserShare_Waypoint);
                layout_UserShare_WayPoint.removeAllViewsInLayout();
                View layoutWaypoint = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_desc, null);
                TextView wpName = (TextView) layoutWaypoint.findViewById(R.id.TEXT_ShareDestination);
                ImageView wpIcon = (ImageView) layoutWaypoint.findViewById(R.id.IMG_ShareIcon);
                TextView textView = view.findViewById(R.id.TEXT_ShareDestination);
                ImageView imageView = view.findViewById(R.id.IMG_ShareIcon);
                wpName.setText(textView.getText());
                wpIcon.setImageDrawable(imageView.getDrawable());

                layoutWaypoint.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                layout_UserShare_WayPoint.addView(layoutWaypoint);
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view, 4,layoutWaypoint, bitmapArrayList));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickHandler(view, 3,layoutWaypoint, null));
            }
        });
    }

    // 시작 셋팅
    private void StartSeting(){
        ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
        FrameLayout frameLayout_TitleImage = findViewById(R.id.FRAMELAYOUT_TitleImage);
        FrameLayout framelayout_ContentImage = findViewById(R.id.FRAMELAYOUT_ContentImage);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout_TitleImage.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = params.width / 16 * 9;
        params = (LinearLayout.LayoutParams) framelayout_ContentImage.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = params.width / 16 * 9;

        imgbtn_TitleImage.setImageBitmap(null);
        imgbtn_TitleImage.setBackgroundColor(00000000);
        imgbtn_ContentImage.setImageBitmap(null);
        imgbtn_ContentImage.setBackgroundColor(00000000);
    }

    // 삭제 버튼
    private void DeleteButtonSet(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup layout_SharedContent = findViewById(R.id.LAYOUT_SharedContent);
                ViewGroup layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContent);
                LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                dialog.setDialogTitle("삭제하시겠습니까?");
                dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v -> dialog.closeDialog());
                dialog.addButton(R.color.Negative, "삭제").setOnClickListener(v -> {
                    for (int a = 0; a < layout_SharedContent.getChildCount(); a++){
                        if (getTagFromView(layout_SharedContent.getChildAt(a),"layout_WaypointId").
                                equals(getTagFromView(view, "viewId"))){
                            layout_SharedContent.removeViewAt(a);
                        }
                        if (getTagFromView(layout_UserShareContent.getChildAt(a), "layout_WaypointId").
                                equals(getTagFromView(view, "viewId"))){
                            setTagToView(layout_UserShareContent.getChildAt(a), "layout_Shared", false);
                            layout_UserShareContent.getChildAt(a).setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return false;
                                }
                            });
                            layout_UserShareContent.getChildAt(a).findViewById(R.id.LAYOUT_UserShareStroke).setBackground(null);
                        }
                    }
                    dialog.closeDialog();});
            }
        });
    }
}