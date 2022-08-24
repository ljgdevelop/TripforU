package kr.ac.kopo.tripforu;

import android.app.Activity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AppBarController extends AppCompatActivity {
        public Activity activity;
        ImageButton btn_Menu;
        Toolbar myToolbar;
        DrawerLayout drawLayout;
        NavigationView navigationView;
        
        public AppBarController(Activity activity){
                this.activity = activity;
        }
        
        
        
        
        
        /***
         *  -> 사이드 네브바 세팅
         *   - 클릭 아이콘 설정
         *   - 아이템 클릭 이벤트 설정
         */
        public void settingSideNavBar() {
                btn_Menu = (ImageButton) activity.findViewById(R.id.BTN_menu);
                myToolbar = activity.findViewById(R.id.LAYOUT_AppBar);
                drawLayout = (DrawerLayout) activity.findViewById(R.id.LAYOUT_Drawer);
                navigationView = (NavigationView) activity.findViewById(R.id.LAYOUT_NavView);
                
                ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawLayout,
                    myToolbar,
                    R.string.open,
                    R.string.closed
                );
        
                btn_Menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                drawLayout.openDrawer(Gravity.LEFT);
                        }
                });
        
                // 사이드 네브바 클릭 리스너
                drawLayout.addDrawerListener(actionBarDrawerToggle);
        
        
                // -> 사이드 네브바 아이템 클릭 이벤트 설정
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        
                                DrawerLayout drawer = findViewById(R.id.LAYOUT_Drawer);
                                drawer.closeDrawer(GravityCompat.START);
                                return true;
                        }
                });
        }
        
        
        /***
         *  -> 뒤로가기시, 사이드 네브바 닫는 기능
         */
        @Override
        public void onBackPressed() {
                DrawerLayout drawer = findViewById(R.id.LAYOUT_Drawer);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                } else {
                        super.onBackPressed();
                }
        }
        
        public View findMyViewById(int id){
                return findViewById(id);
        }
}
