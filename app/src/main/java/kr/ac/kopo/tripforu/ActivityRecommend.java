package kr.ac.kopo.tripforu;

import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.time.LocalDate;

public class ActivityRecommend extends PageController {
    @Override protected boolean useToolbar(){ return true; }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        
        LayoutRecommendBanner banner = new LayoutRecommendBanner(getApplicationContext());
        ((ViewGroup)findViewById(R.id.LAYOUT_Recommend_Container)).addView(banner);
    
    }
}