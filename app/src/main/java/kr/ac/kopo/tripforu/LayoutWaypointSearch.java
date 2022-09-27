package kr.ac.kopo.tripforu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;



public class LayoutWaypointSearch extends AppCompatActivity {

    RatingBar ratingBar;
    TextView score;

    private static final String TAG = "MAIN";


    protected static FrameLayout fullView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_waypointsearch);



        score = findViewById(R.id.score);

        score.setText("0점");

        ratingBar = findViewById(R.id.ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                score.setText(rating + "점");
            }
        });

    }

    protected boolean useToolbar(){
        return true;
    }
    @Override
    public void setContentView(int layoutResID){
        fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        View child = getLayoutInflater().inflate(layoutResID, activityContainer, true);
        ResetAppBar();

        super.setContentView(fullView);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) fullView.findViewById(R.id.LAYOUT_AppBar);
        if(useToolbar()){
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    protected View SetAppBarAction(int type, boolean isLeft, String text){
        String color = "";
        int id = 0;
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

        TextView view = (TextView) findViewById(id);
        view.setTextColor(Color.parseColor(color));
        view.setVisibility(View.VISIBLE);
        view.setText(text);
        return view;
    }

    protected void ResetAppBar(){
        fullView.findViewById(R.id.TEXT_AppBarLeft).setVisibility(View.GONE);
        fullView.findViewById(R.id.TEXT_AppBarRight).setVisibility(View.VISIBLE);
        fullView.findViewById(R.id.TEXT_AppBarTittle).setVisibility(View.VISIBLE);
        fullView.findViewById(R.id.IMG_AppBarRight).setVisibility(View.VISIBLE);
    }

}
