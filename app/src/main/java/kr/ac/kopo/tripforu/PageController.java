package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

interface OnBackPressedListener {
    void onBackPressed();
}



public class PageController extends AppCompatActivity implements OnBackPressedListener{
    private static ArrayList<Page> pageStack = new ArrayList<>();
    byte TYPE_ACTIVITY = 0;
    byte TYPE_INFLATE = 1;
    byte TYPE_HIDEANDSHOW = 2;
    byte TYPE_NAVDRAWER = 3;
    
    public static void AddPage(Page page){
        pageStack.add(page);
    }
    public static void PopPage(){
        pageStack.remove(pageStack.size() - 1);
    }
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        DrawerLayout drawer = findViewById(R.id.LAYOUT_Drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(pageStack.size() > 0) {
                Page lastPage = pageStack.get(pageStack.size() - 1);
                switch (lastPage.GetPageType()) {
                    case 0:
                        ((Activity) lastPage.GetPageObject()).finish();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        ((DrawerLayout)lastPage.GetPageObject()).closeDrawers();
                        break;
                }
                PopPage();
            }
        }
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 설명 : 다른 액티비티에서 앱바를 보여줄지를 정할 수 있게해주는 함수
     * ->       해당 액티비티에서 오버라이드 해서 사용
     * ->       모든 액티비티와 연결된 자바파일에 적용
     * 
     * -> 사용법 : 해당 자바파일에서 Class 영역 안에 
     *            @Override protected boolean useToolbar(){ return true; } 추가
     *            이때 true면 화면 상단에 앱바가 나타나고 false면 안 나타남
     */
    protected boolean useToolbar(){
        return true;
    }
    
    /***
     * -> 작성자 : 이제경
     * -> 함수 : 다른 액티비티에서 앱바를 보여줄지를 정할 수 있게해주는 함수
     * ->       해당 액티비티에서 오버라이드 해서 사용
     * ->       모든 액티비티마다 설정해줄 것.
     */
    
    @Override
    public void setContentView(int layoutResID){
        LinearLayout fullView = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        
        Toolbar toolbar = (Toolbar) fullView.findViewById(R.id.LAYOUT_AppBar);
        if(useToolbar()){
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }
}
