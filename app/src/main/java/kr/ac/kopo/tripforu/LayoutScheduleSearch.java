package kr.ac.kopo.tripforu;

import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LayoutScheduleSearch extends PageController{

    /***
     * @author 정다빈
     * @param scheduleArrayList - 뷰의 모든 스케쥴데이터
     * 검색기능
     * */
    private void textChangedListener(ArrayList<Schedule> scheduleArrayList){
        EditText editText = (EditText)fullView.findViewById(R.id.TEXT_AppBarSearchText);
        TextView text_TitleTextCriternion = fullView.findViewById(R.id.TEXT_TitleTextCriternion);
        TextView text_DestinationCriternion = fullView.findViewById(R.id.TEXT_DestinationCriternion);
        LinearLayout layout_NewSchedule_Container = fullView.findViewById(R.id.LAYOUT_NewSchedule_Container);
        LinearLayout layout_PastSch_Container = fullView.findViewById(R.id.LAYOUT_PastSch_Container);
        layout_NewSchedule_Container.removeAllViews();
        layout_PastSch_Container.removeAllViews();
        showScheduleList(layout_PastSch_Container, layout_NewSchedule_Container, 1);
        Log.d("TAG", "textChangedListener232323: ");
        editText.addTextChangedListener(new TextWatcher() {
            //입력 변화가 생길때마다 조치
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                int a = 0;
                for (int i = 0; i < layout_NewSchedule_Container.getChildCount(); i++){
                    if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                        ScheduleSearch(layout_NewSchedule_Container.getChildAt(i),scheduleArrayList.get(a), editText, 0);
                        a += 1;
                    }else if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                        ScheduleSearch(layout_NewSchedule_Container.getChildAt(i),scheduleArrayList.get(a), editText, 1);
                        a += 1;
                    }
                }
                for (int i = 0; i < layout_PastSch_Container.getChildCount(); i++){
                    if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i),scheduleArrayList.get(a), editText, 0);
                        a += 1;
                    }else if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i), scheduleArrayList.get(a), editText, 1);
                        a += 1;
                    }
                }
            }
            // 입력하기 전에 조치
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }
            // 입력이 끝났을 때 조치
            @Override
            public void afterTextChanged(Editable editable) {
                int a = 0;
                for (int i = 0; i < layout_NewSchedule_Container.getChildCount(); i++){
                    if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                        ScheduleSearch(layout_NewSchedule_Container.getChildAt(i),scheduleArrayList.get(a), editText, 0);
                        a += 1;
                    }else if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                        ScheduleSearch(layout_NewSchedule_Container.getChildAt(i),scheduleArrayList.get(a), editText, 1);
                        a += 1;
                    }
                }
                for (int i = 0; i < layout_PastSch_Container.getChildCount(); i++){
                    if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i),scheduleArrayList.get(a), editText, 0);
                        a += 1;
                    }else if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i), scheduleArrayList.get(a), editText, 1);
                        a += 1;
                    }
                }
            }
        });
    }

    /***
     * @author 정다빈
     * @param view - 스케쥴 뷰
     * @param schedule - 뷰의 스케쥴데이터
     * @param editText - 검색할 내용의 에디트텍스트
     * @param a - 0 = 제목 및 지역으로 검색 , 1 = 관광지 이름으로 검색
     * */
    private static void ScheduleSearch(View view,Schedule schedule, EditText editText, int a){
        switch (a){
            case 0:
                if (schedule.getName().replace(" ","").
                        contains(editText.getText().toString().replace(" ",""))){
                    view.setVisibility(View.VISIBLE);
                }else if (schedule.getDestination().replace(" ","").
                        contains(editText.getText().toString().replace(" ",""))){
                    view.setVisibility(View.VISIBLE);
                }else {
                    view.setVisibility(View.GONE);
                }
                break;
            case 1:
                ArrayList<Waypoint> waypointArrayList = schedule.getWayPointList();
                int[] check = new int[waypointArrayList.size()];
                for (int i = 0; i < waypointArrayList.size(); i++){
                    Waypoint waypoint = waypointArrayList.get(i);
                    if (waypoint.GetName().replace(" ","").
                            contains(editText.getText().toString().replace(" ",""))){
                        check[i] = 1;
                    }else {
                        check[i] = 0;
                    }
                }
                for (int i = 0; i < check.length; i++){
                    if (check[i] == 1){
                        view.setVisibility(View.VISIBLE);
                        break;
                    }
                    view.setVisibility(View.GONE);
                }
                break;
        }
    }
    
    //검색창 버튼 클릭 기능
    public void searchLayoutBtn(){
        textChangedListener(ScheduleController.getSortedScheduleByDate());
        TextView text_TitleTextCriternion = fullView.findViewById(R.id.TEXT_TitleTextCriternion);
        TextView text_DestinationCriternion = fullView.findViewById(R.id.TEXT_DestinationCriternion);
        setTagToView(text_TitleTextCriternion, "state", true);
        setTagToView(text_DestinationCriternion, "state", false);
        text_TitleTextCriternion.setOnClickListener(view -> {
            Log.d("TAG", "searchIconClick: 123");
            if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                setTagToView(text_TitleTextCriternion, "state", true);
                setTagToView(text_DestinationCriternion, "state", false);
                text_TitleTextCriternion.setTextColor(Color.parseColor("#527821"));
                text_DestinationCriternion.setTextColor(Color.parseColor("#5E5E5E"));
                fullView.findViewById(R.id.TEXT_TitleTextUnderline).setBackgroundResource(R.color.APP_Secondary);
                fullView.findViewById(R.id.TEXT_DestinationUnderline).setBackgroundColor(Color.parseColor("#4DD7DDE9"));
            }
        });
        text_DestinationCriternion.setOnClickListener(view -> {
            Log.d("TAG", "searchIconClick: 123123");
            if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                setTagToView(text_TitleTextCriternion, "state", false);
                setTagToView(text_DestinationCriternion, "state", true);
                text_TitleTextCriternion.setTextColor(Color.parseColor("#5E5E5E"));
                text_DestinationCriternion.setTextColor(Color.parseColor("#527821"));
                fullView.findViewById(R.id.TEXT_TitleTextUnderline).setBackgroundColor(Color.parseColor("#4DD7DDE9"));
                fullView.findViewById(R.id.TEXT_DestinationUnderline).setBackgroundResource(R.color.APP_Secondary);
            }
        });
    }
}