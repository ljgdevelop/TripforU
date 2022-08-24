package kr.ac.kopo.tripforu;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
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
}
