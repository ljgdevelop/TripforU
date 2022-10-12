package kr.ac.kopo.tripforu;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
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
    public void textChangedListener(ArrayList<Schedule> scheduleArrayList){
        EditText edt_SearchText = fullView.findViewById(R.id.EDT_SearchText);
        TextView text_TitleTextCriternion = fullView.findViewById(R.id.TEXT_TitleTextCriternion);
        TextView text_DestinationCriternion = fullView.findViewById(R.id.TEXT_DestinationCriternion);
        LinearLayout layout_NewSch_Container = fullView.findViewById(R.id.LAYOUT_NewSch_Container);
        LinearLayout layout_PastSch_Container = fullView.findViewById(R.id.LAYOUT_PastSch_Container);
        Schedule schedule;
        layout_NewSch_Container.removeAllViews();
        layout_PastSch_Container.removeAllViews();
        for (int i = 0 ; i < ScheduleController.getSortedScheduleByDate().size(); i++){
            LayoutScheduleTicket newTicket = new LayoutScheduleTicket(ActivityMain.context);
            schedule = scheduleArrayList.get(i);
            newTicket.setScheduleId(schedule.getId());
            showScheduleList(layout_PastSch_Container, layout_NewSch_Container);
        }

        edt_SearchText.addTextChangedListener(new TextWatcher() {
            //입력 변화가 생길때마다 조치
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                for (int i = 0; i < scheduleArrayList.size(); i++){
                    if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i),scheduleArrayList.get(i), edt_SearchText, 0);
                    }else if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i), scheduleArrayList.get(i), edt_SearchText, 1);
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
                for (int i = 0; i < scheduleArrayList.size(); i++){
                    if (getTagFromView(text_TitleTextCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i), scheduleArrayList.get(i), edt_SearchText, 0);
                    }else if (getTagFromView(text_DestinationCriternion, "state").equals("true")){
                        ScheduleSearch(layout_PastSch_Container.getChildAt(i), scheduleArrayList.get(i), edt_SearchText, 1);
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
    public static void ScheduleSearch(View view,Schedule schedule, EditText editText, int a){
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
}