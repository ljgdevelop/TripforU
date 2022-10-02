package kr.ac.kopo.tripforu;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LayoutCalendar extends LinearLayout {
    public static ArrayList<LayoutCalendar> calendarList = new ArrayList<>();
    private LinearLayout fullView = null;
    private Context context = null;
    private LocalDate localDate;
    private ArrayList<LayoutCalendarDate> dateList = new ArrayList<>();
    
    /**
     * @author 이제경
     * @param context
     * @param attrs
     * @param defStyleAttr
     *
     *      커스텀 뷰의 생성자
     */
    public LayoutCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        fullView = (LinearLayout) inflate(getContext(), R.layout.layout_calendar, this);
        calendarList.add((LayoutCalendar) fullView);
        fullView.post(this::init);
    }
    public LayoutCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LayoutCalendar(Context context){
        this(context, null);
    }
    
    private void init(){
        showCalendarDate();
    }
    
    private void showCalendarDate(){
        //상단 년 월 표시
        StringBuilder currDate = new StringBuilder();
        currDate.append(localDate.getYear());
        currDate.append("년 ");
        currDate.append(localDate.getMonth().getValue());
        currDate.append("월");
        ((TextView)fullView.findViewById(R.id.TEXT_Calendar_YearMonth)).setText(currDate);
        
        //달력에 1~31일 추가
        int weeks = 1;
        LocalDate lastDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        for(int i = 1; i <= lastDate.getDayOfMonth(); i ++){
            LocalDate thisDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), i);
            if(thisDate.getDayOfWeek().getValue() % 7 == 0)
                weeks ++;
            LayoutCalendarDate dateBlock = new LayoutCalendarDate(context);
            dateBlock.setCalendarDate(localDate.withDayOfMonth(i), weeks,
                                        thisDate.getDayOfWeek().getValue() % 7,
                                        fullView);
            dateList.add(dateBlock);
        }
    }
    
    public void setDate(LocalDate localDate){
        this.localDate = localDate;
    }
    
    public class LayoutCalendarDate extends LinearLayout {
        private int row;
        private int column;
        private LocalDate date;
        private View thisView;
        
        public LayoutCalendarDate(Context context){
            super(context);
        }
        
        public void setCalendarDate(LocalDate date, int row, int column, LinearLayout fullView){
            View v = inflate(getContext(), R.layout.layout_calendar_date, null);
            
            this.row = row;
            this.column = column;
            this.date = date;
            this.thisView = v;
    
            GridLayout grid = fullView.findViewById(R.id.LAYOUT_Calendar_Grid);
            grid.setRowCount(8);
            grid.setColumnCount(7);
            
            TextView dateText = ((TextView)v.findViewById(R.id.TEXT_Calendar_DateText));
            TextView dateMemo = ((TextView)v.findViewById(R.id.TEXT_Calendar_DateMemo));
            
            dateText.setText(String.format("%d", this.date.getDayOfMonth()));
            if(date.isEqual(LocalDate.now()))
                dateMemo.setText("오늘");
            
            if(column == 0) {
                dateText.setTextColor(getResources().getColor(R.color.TEXT_Red));
                dateMemo.setTextColor(getResources().getColor(R.color.TEXT_Red));
            }
            else if(column == 6){
                dateText.setTextColor(getResources().getColor(R.color.APP_Main));
                dateMemo.setTextColor(getResources().getColor(R.color.APP_Main));
            }
    
            GridLayout.LayoutParams gl = new GridLayout.LayoutParams();
            gl.rowSpec = GridLayout.spec(row + 1);
            gl.columnSpec = GridLayout.spec(column);
            gl.setGravity(Gravity.CENTER_HORIZONTAL);
            gl.bottomMargin = PageController.ConvertDPtoPX(context,16);
            v.setLayoutParams(gl);
            v.setPadding(0,PageController.ConvertDPtoPX(context,16),0,PageController.ConvertDPtoPX(context,8));
            
            
            v.setOnClickListener(view -> setSelectedDate(v, date));
    
            grid.addView(v);
        }
        
        public void setSelectedDate(View view, LocalDate date){
            LocalDate firstDate = null, secondDate = null;
            View container = calendarList.get(0);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            switch (PageController.getTagFromView(container, "selectedCount")){
                case "":
                    PageController.setTagToView(container, "selectedCount", 1);
                    PageController.setTagToView(container, "firstDate", formattedDate);
                    break;
                case "1":
                    PageController.setTagToView(container, "selectedCount", 2);
                    PageController.setTagToView(container, "secondDate", formattedDate);
                    break;
                case "2":
                    //이미 처음과 끝점이 선택되어있을경우 더 가까운쪽의 날짜를 변경
                    firstDate = LocalDate.parse(PageController.getTagFromView(container, "firstDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    secondDate = LocalDate.parse(PageController.getTagFromView(container, "secondDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    Period period = Period.between(date, firstDate);
                    int firstDiff = period.getDays() + period.getMonths() * 31 + period.getYears() * 365;
                    period = Period.between(date, secondDate);
                    int secondDiff = period.getDays() + period.getMonths() * 31 + period.getYears() * 365;
                    Log.d("TT", "setSelectedDate: " + firstDiff);
                    Log.d("TT", "setSelectedDate: " + secondDiff);
                    if(Math.abs(firstDiff) < Math.abs(secondDiff)){
                        PageController.setTagToView(container, "firstDate", formattedDate);
                    }
                    else {
                        PageController.setTagToView(container, "secondDate", formattedDate);
                    }
                    break;
            }
            for (LayoutCalendar lc:calendarList) {
                lc.updateCalendarSelection();
            }
        }
        
        
    }
    
    public void updateCalendarSelection(){
        LocalDate firstDate = null, secondDate = null;
        View container = calendarList.get(0);
        
        //처음과 끝이 설정 되었는지 확인
        if(!PageController.getTagFromView(container, "secondDate").isEmpty()){
            firstDate = LocalDate.parse(PageController.getTagFromView(container, "firstDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            secondDate = LocalDate.parse(PageController.getTagFromView(container, "secondDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        else if(!PageController.getTagFromView(container, "firstDate").isEmpty()){
            firstDate = LocalDate.parse(PageController.getTagFromView(container, "firstDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if(firstDate != null && secondDate != null){
            for (LayoutCalendarDate thisDate:dateList) {
                TextView dateText = ((TextView)thisDate.thisView.findViewById(R.id.TEXT_Calendar_DateText));
                TextView dateMemo = ((TextView)thisDate.thisView.findViewById(R.id.TEXT_Calendar_DateMemo));
                if(firstDate.isEqual(thisDate.date) || secondDate.isEqual(thisDate.date)){
                    thisDate.thisView.setBackground(getResources().getDrawable(R.drawable.background_white_rcorner_15));
                    thisDate.thisView.setBackgroundTintList(context.getResources().getColorStateList(R.color.APP_Main));
                    dateText.setTextColor(getResources().getColor(R.color.white));
                    dateMemo.setTextColor(getResources().getColor(R.color.white));
                }
                else if(firstDate.isBefore(thisDate.date) && secondDate.isAfter(thisDate.date)){
                    thisDate.thisView.setBackground(null);
                    thisDate.thisView.setBackgroundColor(getResources().getColor(R.color.APP_Shade));
                    thisDate.thisView.setBackgroundTintList(null);
                    dateText.setTextColor(getResources().getColor(R.color.TEXT_Gray));
                    dateMemo.setTextColor(getResources().getColor(R.color.TEXT_Gray));
                }
                else{
                    thisDate.thisView.setBackground(null);
                    thisDate.thisView.setBackgroundColor(getResources().getColor(R.color.white));
                    thisDate.thisView.setBackgroundTintList(null);
                    dateText.setTextColor(getResources().getColor(R.color.TEXT_Black));
                    dateMemo.setTextColor(getResources().getColor(R.color.TEXT_Black));
                    if(thisDate.column == 0) {
                        dateText.setTextColor(getResources().getColor(R.color.TEXT_Red));
                        dateMemo.setTextColor(getResources().getColor(R.color.TEXT_Red));
                    }
                    else if(thisDate.column == 6){
                        dateText.setTextColor(getResources().getColor(R.color.APP_Main));
                        dateMemo.setTextColor(getResources().getColor(R.color.APP_Main));
                    }
                }
            }
        }
        else if(firstDate != null){
            for (LayoutCalendarDate thisDate:dateList) {
                if(firstDate.isEqual(thisDate.date)){
                    thisDate.thisView.setBackground(getResources().getDrawable(R.drawable.background_white_rcorner_15));
                    thisDate.thisView.setBackgroundTintList(context.getResources().getColorStateList(R.color.APP_Main));
                    ((TextView)thisDate.thisView.findViewById(R.id.TEXT_Calendar_DateText)).
                                setTextColor(getResources().getColor(R.color.white));
                    ((TextView)thisDate.thisView.findViewById(R.id.TEXT_Calendar_DateMemo)).
                                setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
    }
}
