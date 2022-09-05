package kr.ac.kopo.tripforu;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class WaypointSearch extends AppCompatActivity {

    RatingBar ratingBar;
    TextView score;

    private static final String TAG = "MAIN";

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
}
