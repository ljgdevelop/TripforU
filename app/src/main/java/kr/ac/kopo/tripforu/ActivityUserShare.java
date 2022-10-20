package kr.ac.kopo.tripforu;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import kr.ac.kopo.tripforu.AWS.S3;
import kr.ac.kopo.tripforu.Retrofit.INetTask;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityUserShare extends PageController implements Cloneable{
    static S3 instance;

    @Override protected boolean useToolbar(){ return true; }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usershare);
        AddPage(this);

        //시작 초기 설정
        StartSeting();

        // 첫 페이지 앱바
        SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 1,  null));
        SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickHandler(view1, 0,  null));

        //플로팅 액션 버튼
        onFABClick();

        //갤러리 사진 가져오기
        onImageClick(findViewById(R.id.IMGBTN_TitleImage), 0);
        onImageClick(findViewById(R.id.IMGBTN_ContentImage), 1);

        //메인 스크롤 뷰 터치 막기
        findViewById(R.id.LAYOUT_UserShareContainer).setOnTouchListener(((view, motionEvent) -> { return true; }));
    }

    //이미지 클릭 후 사진 추가 기능
    private void onImageClick(ImageButton imageButton, int i){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTagFromView(view, "ImgBtnId").equals("0")){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(Intent.createChooser(intent, "사진 선택"), i);
                }else if(getTagFromView(view, "ImgBtnId").equals("1")){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(Intent.createChooser(intent, "사진 선택"), i);
                }
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
                    ClipData clipData = data.getClipData();
                    if (requestCode == 0 && data.getData() != null){
                        ImageView img_TitleImage = findViewById(R.id.IMG_TitleImage);
                        ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
                        Uri uri = data.getData();
                        img_TitleImage.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData())));
                        setTagToView(img_TitleImage, "uriId", uri);
                        imgbtn_TitleImage.setImageBitmap(null);
                        imgbtn_TitleImage.setBackgroundColor(00000000);
                        img_TitleImage.bringToFront();
                    }else if(requestCode == 1){
                        if (clipData.getItemCount() > 3){
                            Toast.makeText(ActivityMain.context, "사진은 3장 이하로만 선택 가능합니다.",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            findViewById(R.id.IMGBTN_ContentImageRight).setVisibility(View.GONE);
                            findViewById(R.id.IMGBTN_ContentImageLeft).setVisibility(View.GONE);
                            ContentImageReset();
                            
                            ImageView img_ContentImage1 = findViewById(R.id.IMG_ContentImage1);
                            ImageView img_ContentImage2 = findViewById(R.id.IMG_ContentImage2);
                            ImageView img_ContentImage3 = findViewById(R.id.IMG_ContentImage3);
                            for (int i = 0 ; i < clipData.getItemCount(); i++){
                                Uri uri = clipData.getItemAt(i).getUri();
                                Bitmap tempImg;
                                switch (i){
                                    case 0:
                                        tempImg = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                                        setTagToView(img_ContentImage1, "uriId", uri);
                                        img_ContentImage1.setImageBitmap(tempImg);
                                        img_ContentImage1.setVisibility(View.VISIBLE);
                                        break;
                                    case 1:
                                        tempImg = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                                        setTagToView(img_ContentImage2, "uriId", uri);
                                        img_ContentImage2.setImageBitmap(tempImg);
                                        findViewById(R.id.IMGBTN_ContentImageRight).setVisibility(View.VISIBLE);
                                        break;
                                    case 2:
                                        tempImg = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                                        setTagToView(img_ContentImage3, "uriId", uri);
                                        img_ContentImage3.setImageBitmap(tempImg);
                                        break;
                                }
                            }
                            ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
                            imgbtn_ContentImage.setImageBitmap(null);
                            imgbtn_ContentImage.setBackgroundColor(00000000);
                            img_ContentImage3.bringToFront();
                            img_ContentImage2.bringToFront();
                            img_ContentImage1.bringToFront();
                            img_ContentImage3.setVisibility(View.GONE);
                            img_ContentImage2.setVisibility(View.GONE);
                        }
                    }
                }catch (Exception e){ }
            }
            /*else if (resultCode == RESULT_CANCELED){
                if (requestCode == 0){
                    ImageView img_TitleImage = findViewById(R.id.IMG_TitleImage);
                    img_TitleImage.setImageBitmap(null);
                    ImageButton imgbtnTitleImage = findViewById(R.id.IMGBTN_TitleImage);
                    imgbtnTitleImage.bringToFront();
                    imgbtnTitleImage.setImageResource(R.drawable.ic_tempimage);
                    imgbtnTitleImage.setBackgroundColor(Color.parseColor("#E2E2E2"));
                }else if(requestCode == 1){
                    findViewById(R.id.IMGBTN_ContentImageRight).setVisibility(View.GONE);
                    findViewById(R.id.IMGBTN_ContentImageLeft).setVisibility(View.GONE);
                    ContentImageReset();
                }
            }*/
        }
    }

    //  floatingActionButton 터치 기능
    private void onFABClick(){
        ExtendedFloatingActionButton acbtn_AddContent = findViewById(R.id.ACBTN_AddContent);
        acbtn_AddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScrollView layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContents);
                
                TabHorizontalScroll(findViewById(R.id.LAYOUT_UserShareContainer), 1);
                
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
                    ShareWaypointClickListner();
                }

                // 두번째 페이지 앱바
                SetAppBarAction(1, false, "완료").setOnClickListener(View -> onClickHandler(view, 2, null));
                SetAppBarAction(0, true, "");
            }
        });
    }

    // 앱바 터치 기능문
    private void onClickHandler(View v, int i, View v2){
        ViewGroup layout_SharedContent = findViewById(R.id.LAYOUT_SharedContent);
        LinearLayout layout_UserShare_WayPoint = findViewById(R.id.LAYOUT_UserShare_Waypoint);
        EditText edt_ContentDetailText = findViewById(R.id.EDT_ContentDetailText);
        ImageView img_ContentImage1 = findViewById(R.id.IMG_ContentImage1);
        ArrayList<SharedSchedule.WaypointDescription> waypointDescriptions = new ArrayList<>();
        SharedSchedule sharedSchedule = new SharedSchedule(0,0,0,0,0,
                                                    0,"","",waypointDescriptions);
        ArrayList<File> fileArrayList = new ArrayList<>();

        switch (i){
            case 0:
                //1페이지 취소
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                        dialog.setDialogTitle("작성을 취소하시겠습니까?");
                        dialog.setDialogMessage("이 작업은 되돌릴 수 없습니다.");
                        dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v -> dialog.closeDialog());
                        dialog.addButton(R.color.Negative, "삭제").setOnClickListener(v -> {
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
                        dialog.addButton(R.color.APP_Main, "확인").setOnClickListener(v -> {
    
                            //스케쥴 값 생성
                            Intent subIntent = getIntent();
                            Schedule putSchedule = (Schedule)subIntent.getSerializableExtra("putSchedule");
    
                            // SharedSchedule 값 넣기
                            SharedSchedule sch = addSharedScheduleData(fileArrayList, sharedSchedule, putSchedule);
                            
                            //데이터 업로드
                            ServerController.getInstance().uploadSchedule(sch, putSchedule);
                            
                            dialog.closeDialog();
                            finish();
                        });
                    }
                });

                break;
            case 2:
                //2페이지 확인
                SetAppBarAction(1, false, "업로드").setOnClickListener(view1 -> onClickHandler(view1, 1, null));
                SetAppBarAction(2, true, "취소").setOnClickListener(view1 -> onClickHandler(view1, 0, null));
                TabHorizontalScroll(findViewById(R.id.LAYOUT_UserShareContainer), 0);
                break;
            case 3:
                //3페이지 이전
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2, null));
                SetAppBarAction(0, true, "");
                TabHorizontalScroll(findViewById(R.id.LAYOUT_UserShareContainer), 1);
                layout_UserShare_WayPoint.removeAllViewsInLayout();
                ShareWaypointClickListner();
                edt_ContentDetailText.setText(null);
                ContentImageReset();
                findViewById(R.id.IMGBTN_ContentImageRight).setVisibility(View.GONE);
                findViewById(R.id.IMGBTN_ContentImageLeft).setVisibility(View.GONE);
                break;
            case 4:
                //3페이지 확인
                //초기화
                if (((BitmapDrawable) img_ContentImage1.getDrawable()).getBitmap() == null &
                        edt_ContentDetailText.getText().length() == 0){
                    Toast.makeText(ActivityMain.context, "이미지 또는 텍스트를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view1, 2, null));
                    SetAppBarAction(0, true, "");
                    TabHorizontalScroll(findViewById(R.id.LAYOUT_UserShareContainer), 1);
                    layout_UserShare_WayPoint.removeAllViewsInLayout();
                    v.findViewById(R.id.LAYOUT_UserShareStroke).setBackgroundResource(R.color.APP_Main);

                    // 공유할 컨텐츠 내용 보기 및 삭제
                    contentClickEvent(v, v2);

                    //콘텐츠 텍스트, 이미지 확인 및 부여
                    shareContentCheck(v2);

                    //이미지 페이지 이동 버튼
                    imgPageMove(v2);

                    //콘텐츠 뷰 추가
                    layout_SharedContent.addView(v2);

                    //콘텐츠 공유 여부 확인
                    ShareWaypointClickListner();
                }
                break;
        }
    }

    //여행지 정보 생성
    private static void ShareWaypointInfo(View view, Schedule schedule){
        final ViewGroup waypointContainer = view.findViewById(R.id.LAYOUT_UserShareContent);
        waypointContainer.removeAllViewsInLayout();
        int a = 0;
        for(Waypoint waypoint : schedule.getWayPointList()){
            View layoutWP = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_desc, null);

            final TextView wpName = (TextView) layoutWP.findViewById(R.id.TEXT_ShareDestination);
            ImageView wpIcon = (ImageView) layoutWP.findViewById(R.id.IMG_ShareIcon);

            setTagToView(layoutWP, "layout_WaypointId", waypoint.GetId());
            setTagToView(layoutWP, "layout_Shared", false);

            wpName.setText(waypoint.GetName());
            int imgSrc = -1;
            imgSrc = ScheduleController.getInstance().getWayPointIcon(schedule, a);
            a += 1;
            wpIcon.setImageResource(imgSrc);

            waypointContainer.addView(layoutWP);
        }
    }

    //여행지 정보 적용 및 뷰 클릭 리스너 적용
    private void ShareWaypointClickListner(){
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
                    onClickView(wpView);
            }
        }
    }

    // 뷰 클릭 리스너
    private void onClickView(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.LAYOUT_UserShareContentDetail).setVisibility(View.VISIBLE);
                LinearLayout layout_UserShare_WayPoint = findViewById(R.id.LAYOUT_UserShare_Waypoint);
                layout_UserShare_WayPoint.removeAllViewsInLayout();
                View layoutWaypoint = View.inflate(ActivityMain.context, R.layout.layout_usershare_point_desc, null);
                TextView wpName = (TextView) layoutWaypoint.findViewById(R.id.TEXT_ShareDestination);
                ImageView wpIcon = (ImageView) layoutWaypoint.findViewById(R.id.IMG_ShareIcon);
                TextView textView = view.findViewById(R.id.TEXT_ShareDestination);
                ImageView imageView = view.findViewById(R.id.IMG_ShareIcon);
                wpName.setText(textView.getText());
                wpIcon.setImageDrawable(imageView.getDrawable());
                setTagToView(findViewById(R.id.FRAMELAYOUT_ContentImageGroup),"ImgStateNum", "0");
                ChangeImgPage(findViewById(R.id.IMGBTN_ContentImageLeft), findViewById(R.id.IMGBTN_ContentImageRight),
                        findViewById(R.id.IMG_ContentImage1),findViewById(R.id.IMG_ContentImage2),
                        findViewById(R.id.IMG_ContentImage3));
                layoutWaypoint.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                layout_UserShare_WayPoint.addView(layoutWaypoint);
                
                TabHorizontalScroll(findViewById(R.id.LAYOUT_UserShareContainer), 2);
    
                //이미지 리셋
                ContentImageReset();
    
                //초기화
                ((TextView)findViewById(R.id.EDT_ContentDetailText)).setText(null);
    
                //이미지 페이지 버튼 숨기기
                findViewById(R.id.IMGBTN_ContentImageRight).setVisibility(View.GONE);
                findViewById(R.id.IMGBTN_ContentImageLeft).setVisibility(View.GONE);
                
                SetAppBarAction(1, false, "완료").setOnClickListener(view1 -> onClickHandler(view, 4,layoutWaypoint));
                SetAppBarAction(0, true, "이전").setOnClickListener(view1 -> onClickHandler(view, 3,layoutWaypoint));
            }
        });
    }

    // 이미지 페이지 변경  버튼
    private void ChangeImgPage(ImageButton imageButtonLeft, ImageButton imageButtonRight,
                               ImageView imageView1, ImageView imageView2, ImageView imageView3) {
        Animation animationLeft_In = AnimationUtils.loadAnimation(ActivityMain.context, R.anim.anim_slide_left_in);
        animationLeft_In.setDuration(500);
        Animation animationLeft_Out = AnimationUtils.loadAnimation(ActivityMain.context, R.anim.anim_slide_left_out);
        animationLeft_Out.setDuration(500);
        Animation animationRight_In = AnimationUtils.loadAnimation(ActivityMain.context, R.anim.anim_slide_right_in);
        animationRight_In.setDuration(500);
        Animation animationRight_Out = AnimationUtils.loadAnimation(ActivityMain.context, R.anim.anim_slide_right_out);
        animationRight_Out.setDuration(500);
        imageButtonLeft.setOnClickListener(new View.OnClickListener() {
            private Long mLastClickTime = 0L;
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime > 500){
                    if (imageView2.getVisibility() == View.VISIBLE){
                        imageView1.startAnimation(animationRight_In);
                        imageView1.setVisibility(View.VISIBLE);
                        imageView2.startAnimation(animationRight_Out);
                        imageView2.setVisibility(View.GONE);
                        imageButtonLeft.setVisibility(View.GONE);
                        imageButtonRight.setVisibility(View.VISIBLE);

                    }else if (imageView3.getVisibility() == View.VISIBLE){
                        imageView2.startAnimation(animationRight_In);
                        imageView2.setVisibility(View.VISIBLE);
                        imageView3.startAnimation(animationRight_Out);
                        imageView3.setVisibility(View.GONE);
                        imageButtonRight.setVisibility(View.VISIBLE);
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
        imageButtonRight.setOnClickListener(new View.OnClickListener() {
            private Long mLastClickTime = 0L;
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime > 500){
                    if (imageView1.getVisibility() == View.VISIBLE) {
                        imageView1.startAnimation(animationLeft_Out);
                        imageView1.setVisibility(View.GONE);
                        imageView2.startAnimation(animationLeft_In);
                        imageView2.setVisibility(View.VISIBLE);
                        imageButtonLeft.setVisibility(View.VISIBLE);
                        if (((BitmapDrawable)(imageView3.getDrawable())).getBitmap() == null){
                            imageButtonRight.setVisibility(View.GONE);
                        }
                    }else if (imageView2.getVisibility() == View.VISIBLE){
                        imageView2.startAnimation(animationLeft_Out);
                        imageView2.setVisibility(View.GONE);
                        imageView3.startAnimation(animationLeft_In);
                        imageView3.setVisibility(View.VISIBLE);
                        imageButtonRight.setVisibility(View.GONE);
                    }
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }

    //imglayout의 사이즈 16:9로 변경
    private void ImgLayoutSize(View view){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = params.width / 16 * 9;
    }

    // 시작 셋팅
    private void StartSeting(){
        FrameLayout main = findViewById(R.id.LAYOUT_UserShareMain);
        LinearLayout waypoints = findViewById(R.id.LAYOUT_UserShareContent);
        LinearLayout desc = findViewById(R.id.LAYOUT_UserShareContentDetail);
    
        //화면 너비 가져오기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int swidth = size.x;
    
        //레이아웃의 너비를 화면 너비에 맞춤
        LinearLayout.LayoutParams mainP = new LinearLayout.LayoutParams(main.getLayoutParams());
        mainP.width = swidth;
        main.setLayoutParams(mainP);
    
        FrameLayout.LayoutParams waypointsP = new FrameLayout.LayoutParams(waypoints.getLayoutParams());
        waypointsP.width = swidth;
        waypoints.setLayoutParams(waypointsP);
    
        LinearLayout.LayoutParams descP = new LinearLayout.LayoutParams(desc.getLayoutParams());
        descP.width = swidth;
        desc.setLayoutParams(descP);
        
        ImageView img_TitleImage = findViewById(R.id.IMG_TitleImage);
        ImageButton imgbtn_TitleImage = findViewById(R.id.IMGBTN_TitleImage);
        ImgLayoutSize(findViewById(R.id.FRAMELAYOUT_TitleImage));
        ImgLayoutSize(findViewById(R.id.FRAMELAYOUT_ContentImageGroup));

        //이미지 초기화
        img_TitleImage.setImageBitmap(null);
        ContentImageReset();

        setTagToView(imgbtn_TitleImage, "ImgBtnId", "0");
        setTagToView(findViewById(R.id.IMGBTN_ContentImage), "ImgBtnId", "1");
    }

    // 공유 선택한 콘텐츠 삭제
    private void ContentDelete(View view){
        ViewGroup layout_SharedContent = findViewById(R.id.LAYOUT_SharedContent);
        ViewGroup layout_UserShareContent = findViewById(R.id.LAYOUT_UserShareContent);
        for (int a = 0; a < layout_SharedContent.getChildCount(); a++) {
            if (getTagFromView(layout_SharedContent.getChildAt(a), "layout_WaypointId").
                    equals(getTagFromView(view, "layout_WaypointId"))) {
                layout_SharedContent.removeViewAt(a);
            }
        }
        for (int a = 0; a < layout_UserShareContent.getChildCount(); a++){
            if (getTagFromView(layout_UserShareContent.getChildAt(a), "layout_WaypointId").
                    equals(getTagFromView(view, "layout_WaypointId"))) {
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
    }

    // 콘텐츠 이미지 리셋
    private void ContentImageReset(){
        ImageButton imgbtn_ContentImage = findViewById(R.id.IMGBTN_ContentImage);
        ImageView img_ContentImage1 = findViewById(R.id.IMG_ContentImage1);
        ImageView img_ContentImage2 = findViewById(R.id.IMG_ContentImage2);
        ImageView img_ContentImage3 = findViewById(R.id.IMG_ContentImage3);
        imgbtn_ContentImage.setImageResource(R.drawable.ic_tempimage);
        imgbtn_ContentImage.setBackgroundColor(Color.parseColor("#E2E2E2"));
        img_ContentImage1.setImageBitmap(null);
        img_ContentImage2.setImageBitmap(null);
        img_ContentImage3.setImageBitmap(null);
    }

    //비트맵 사진을 파일 객체로 변경
    private void BitmapConvertFile(Bitmap bitmap, String strFilePath)
    {
        File file = new File(strFilePath);

        OutputStream out = null;
        try {
            file.createNewFile();

            out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Bitmap 이미지 jpg변환
    private File changeJPG(ImageView imageView){
        File file = null;
        try {
            file = File.createTempFile("bmFile",".jpg", ActivityMain.context.getCacheDir());
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if (!drawable.equals(null)){
                Bitmap bitmap = drawable.getBitmap();
                //임시 디렉토리(캐쉬 디렉토리) 주소값 생성
                BitmapConvertFile(bitmap,  file.toString());
                file = new File(file.toString());
                Log.d("TAG", "changeJPG: " + file);
            }
        }catch (IOException e){

        }
        return file;
    }

    //이미지 페이지 이동 버튼
    private void imgPageMove(View v2){
        ChangeImgPage(v2.findViewById(R.id.IMGBTN_PointDescLeft),v2.findViewById(R.id.IMGBTN_PointDescRight),
                v2.findViewById(R.id.IMG_PointDescImg1), v2.findViewById(R.id.IMG_PointDescImg2),
                v2.findViewById(R.id.IMG_PointDescImg3));
        ImageView imageView2 = v2.findViewById(R.id.IMG_PointDescImg2);
        if (((BitmapDrawable)(imageView2.getDrawable())).getBitmap() != null){
            v2.findViewById(R.id.IMGBTN_PointDescRight).setVisibility(View.VISIBLE);
        }
    }

    //공유한 이미지, 텍스트 넣기 및 있는지 없는지 확인
    private void shareContentCheck(View v2){
        //공유한 이미지 넣기 및 이미지 있는지 없는지 확인
        ImageView img_PointDescImg1 = v2.findViewById(R.id.IMG_PointDescImg1);
        ImageView img_PointDescImg2 = v2.findViewById(R.id.IMG_PointDescImg2);
        ImageView img_PointDescImg3 = v2.findViewById(R.id.IMG_PointDescImg3);
        ImageView img_ContentImage1 = findViewById(R.id.IMG_ContentImage1);
        ImageView img_ContentImage2 = findViewById(R.id.IMG_ContentImage2);
        ImageView img_ContentImage3 = findViewById(R.id.IMG_ContentImage3);
        EditText edt_ContentDetailText = findViewById(R.id.EDT_ContentDetailText);
        FrameLayout framelayout_PointDescGroup = v2.findViewById(R.id.FRAMELAYOUT_PointDescGroup);
        ImgLayoutSize(framelayout_PointDescGroup);
        img_PointDescImg1.setImageBitmap(((BitmapDrawable) img_ContentImage1.getDrawable()).getBitmap());
        img_PointDescImg2.setImageBitmap(((BitmapDrawable) img_ContentImage2.getDrawable()).getBitmap());
        img_PointDescImg3.setImageBitmap(((BitmapDrawable) img_ContentImage3.getDrawable()).getBitmap());
        if ((((BitmapDrawable) img_ContentImage1.getDrawable()).getBitmap()) == null){
            img_PointDescImg1.setVisibility(View.GONE);
            framelayout_PointDescGroup.setVisibility(View.GONE);
        }else {
            img_PointDescImg1.setVisibility(View.VISIBLE);
            framelayout_PointDescGroup.setVisibility(View.VISIBLE);
        }
        //공유한 텍스트 넣기 및 있는지 없는지 확인
        TextView text_PointDescText = v2.findViewById(R.id.TEXT_PointDescText);
        text_PointDescText.setText(edt_ContentDetailText.getText());
        if (text_PointDescText.getText().length() == 0){
            text_PointDescText.setVisibility(View.GONE);
        }else{
            text_PointDescText.setVisibility(View.VISIBLE);
        }
    }

    // 공유할 컨텐츠 뷰 클릭 이벤트
    private void contentClickEvent(View v, View v2){
        setTagToView(v, "layout_Shared",true);
        setTagToView(v2, "layout_WaypointId",getTagFromView(v,"layout_WaypointId"));
        v2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        // 공유할 컨텐츠 내용 보기
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout_PointDescContent =  v2.findViewById(R.id.LAYOUT_PointDescContent);
                ImageView img_Handle = v2.findViewById(R.id.IMG_Handle);
                if (layout_PointDescContent.getVisibility() == View.GONE){
                    layout_PointDescContent.setVisibility(View.VISIBLE);
                    img_Handle.setImageResource(R.drawable.ic_expand_less);
                }else {
                    layout_PointDescContent.setVisibility(View.GONE);
                    img_Handle.setImageResource(R.drawable.ic_expand_more);
                }
            }
        });
        // 공유할 컨텐츠 롱클릭 삭제
        v2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LayoutDialog dialog = new LayoutDialog(getApplicationContext());
                dialog.setDialogTitle("삭제하시겠습니까?");
                dialog.setDialogMessage("작성하신 내용은 삭제됩니다.");
                dialog.addButton(R.color.TEXT_Gray, "취소").setOnClickListener(v -> dialog.closeDialog());
                dialog.addButton(R.color.Negative, "삭제").setOnClickListener(v -> {
                    ContentDelete(view);
                    dialog.closeDialog();
                });
                return true;
            }
        });
        ImageView img_Handle = v2.findViewById(R.id.IMG_Handle);
        img_Handle.setVisibility(View.VISIBLE);
        img_Handle.setImageResource(R.drawable.ic_expand_more);
    }

    //이미지 id 부여 및 웨이포인트 추가
    private void addIdAndWaypoint(SharedSchedule sharedSchedule,int a){
        int[] tempImgId = new int[3];
        tempImgId[0] = INetTask.getInstance().getAvailableImageId();
        tempImgId[1] = INetTask.getInstance().getAvailableImageId();
        tempImgId[2] = INetTask.getInstance().getAvailableImageId();
        ViewGroup layout_SharedContent = findViewById(R.id.LAYOUT_SharedContent);
        TextView text_PointDescText = layout_SharedContent.getChildAt(a).findViewById(R.id.TEXT_PointDescText);
        if (text_PointDescText.length() == 0){
            String contentText = "";
            sharedSchedule.addWaypoint(
                    Integer.parseInt(getTagFromView(layout_SharedContent.getChildAt(a), "layout_WaypointId")),
                    tempImgId, contentText);
        }else{
            String contentText = text_PointDescText.getText().toString();
            sharedSchedule.addWaypoint(
                    Integer.parseInt(getTagFromView(layout_SharedContent.getChildAt(a), "layout_WaypointId")),
                    tempImgId, contentText);
        }
    }

    //공유 스케쥴 데이터 추가
    private SharedSchedule addSharedScheduleData(ArrayList<File> fileArrayList,
                                   SharedSchedule sharedSchedule, Schedule schedule){
        EditText edt_TitleText = findViewById(R.id.EDT_TitleText);
        EditText edt_ContentText = findViewById(R.id.EDT_ContentText);
        ImageView img_TitleImage = findViewById(R.id.IMG_TitleImage);
        ViewGroup layout_SharedContent = findViewById(R.id.LAYOUT_SharedContent);
        File file;
        byte tempByte = 0;
        sharedSchedule.setScheduleId(schedule.getId());
        sharedSchedule.setLikes(tempByte);
        sharedSchedule.setTitleText(edt_TitleText.getText().toString());
        sharedSchedule.setDescriptionText(edt_ContentText.getText().toString());
        sharedSchedule.setTitleImgId(INetTask.getInstance().getAvailableImageId());
        file = changeJPG(img_TitleImage);
        fileArrayList.add(file);
        if (layout_SharedContent != null){
            for (int a = 0; a < layout_SharedContent.getChildCount(); a++){
                // 이미지 서버에 보낸 후 Id값 가져오기
                addIdAndWaypoint(sharedSchedule, a);
                ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
                imageViewArrayList.add(layout_SharedContent.getChildAt(a).findViewById(R.id.IMG_PointDescImg1));
                imageViewArrayList.add(layout_SharedContent.getChildAt(a).findViewById(R.id.IMG_PointDescImg2));
                imageViewArrayList.add(layout_SharedContent.getChildAt(a).findViewById(R.id.IMG_PointDescImg3));
                for (int b = 0; b < 3; b++){
                    file = changeJPG(imageViewArrayList.get(b));
                    fileArrayList.add(file);
                }
            }
        }
        File directory = new File(ActivityMain.context.getCacheDir()+"");
        File[] files = directory.listFiles();
        ArrayList<String> filesNameList = new ArrayList<>();
        for (int i = 0; i< files.length; i++) {
            filesNameList.add(files[i].getName());
        }
        for (int i = 0; i < fileArrayList.size(); i++){
            if (fileArrayList.get(i) != null){
                Log.d("TAG", "addSharedScheduleData: " + fileArrayList.get(i));
                Log.d("TAG", "addSharedScheduleData: " + filesNameList.get(i));
                S3.getInstance(getApplicationContext()).uploadWithTransferUtilty(filesNameList.get(i),fileArrayList.get(i));
            }
        }
        return sharedSchedule;
    }

    public boolean deleteCache(File dir){
        //File이 Null 인지 Null이 아니라면 폴더인지 체크
        if(dir != null && dir.isDirectory()){
            //Null도 아니고 폴더도 아니라면 폴더안 파일 리스트를 호출
            String[] children = dir.list();
            //파일 리스트를 반복문으로 호출
            for(String child : children){
                //파일 리스트중 폴더가 존재할 수 있기 때문에 재귀호출
                boolean isSuccess = deleteCache(new File(dir, child));
                if(!isSuccess){
                    return false;
                }
            }
        }
        return dir.delete();
    }
}