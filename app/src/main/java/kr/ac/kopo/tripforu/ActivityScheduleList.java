package kr.ac.kopo.tripforu;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityScheduleList extends PageController {
    private RecyclerView mRecyclerView_Schedule;
    private ScheduleContentAdapter mScheduleContentAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedulelist);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayout layout_SharedMark = findViewById(R.id.LAYOUT_SharedMark);
        LinearLayout layout_SharedContents = findViewById(R.id.LAYOUT_SharedContents);
        mScheduleContentAdapter = new ScheduleContentAdapter();
        mRecyclerView_Schedule = (RecyclerView) findViewById(R.id.RECYCLEVIEW_Schedule);
        mRecyclerView_Schedule.setLayoutManager(manager);
        mRecyclerView_Schedule.setAdapter(mScheduleContentAdapter);

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mScheduleContentAdapter));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView_Schedule);

        ArrayList<ScheduleContent> scheduleContentArrayList = new ArrayList<>();
        ScheduleContent scheduleContent = new ScheduleContent();
        scheduleContent.thisApplication = getApplication();
        String json = scheduleContent.getJsonString();

        ArrayList<ScheduleContent> scheduleList = scheduleContent.jsonParsing(json);

        for (ScheduleContent schContent:scheduleList) {
            scheduleContentArrayList.add(schContent);
        }

        // isShared 상태에 맞게 공유표시 레이아웃 생기고 끄는 기능 구현해야됨

        mScheduleContentAdapter.setItems(scheduleContentArrayList);
    }
}


