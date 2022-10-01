package kr.ac.kopo.tripforu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * @author 이제경
 *
 *      <p>다이얼로그 창을 보여주는 커슽텀 뷰</p>
 *      <p/>
 *      <p>사용법 : </p>
 *      LayoutDialog dialog = new LayoutDialog(getApplicationContext());
 *      dialog.setDialogTitle("제목 영역");
 *      dialog.setDialogMessage("본문 영역");
 *      dialog.addButton(R.color.TEXT_Gray, "왼쪽").setOnClickListener(view -> dialog.closeDialog());
 *      dialog.addButton(R.color.TEXT_Black, "가운데");
 *      dialog.addButton(R.color.APP_Main, "오른쪽");
 */
public class LayoutDialog extends LinearLayout {
    private View fullView = null;
    private Context context = null;
    private String title = "";
    private String message = "";
    private ArrayList<TextView> buttonList = new ArrayList<>();
    
    
    /**
     * @author 이제경
     * @param context
     * @param attrs
     * @param defStyleAttr
     *
     *      커스텀 뷰의 생성자
     */
    public LayoutDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public LayoutDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LayoutDialog(Context context){
        this(context, null);
    }
    
    private void init(Context context){
        this.context = context;
        fullView = inflate(getContext(), R.layout.layout_dialog, this);
        PageController.fullView.addView(fullView);
        fullView.post(this::onShow);
    }
    
    private void onShow(){
        syncContext();
        ValueAnimator va = ValueAnimator.ofInt(0, 50);
        va.setDuration(100);
        va.addUpdateListener(anim -> {
            fullView.findViewById(R.id.LAYOUT_DialogContainer).setTranslationY(-1 * (Integer) anim.getAnimatedValue());
        });
        va.start();
        PageController.AddPage(this);
    }
    
    private void syncContext(){
        TextView titleView = findViewById(R.id.TEXT_DialogTitle);
        TextView messageView = findViewById(R.id.TEXT_DialogMessage);
        LinearLayout buttonContainer = findViewById(R.id.LAYOUT_DialogButtonContainer);
        
        titleView.setText(title);
        messageView.setText(message);
        buttonContainer.removeAllViewsInLayout();
        for (TextView button:buttonList){
            buttonContainer.addView(button);
        }
    }
    
    public void closeDialog(){
        ValueAnimator va = ValueAnimator.ofInt(0, 50);
        va.setDuration(100);
        va.addUpdateListener(anim -> {
            if(fullView.findViewById(R.id.LAYOUT_DialogContainer) != null) {
                fullView.findViewById(R.id.LAYOUT_DialogContainer).setTranslationY((Integer) anim.getAnimatedValue());
                if ((Integer) anim.getAnimatedValue() > 48)
                    ((ViewGroup) this).removeAllViews();
            }
        });
        va.start();
    }
    
    public void setDialogTitle(String title){
        this.title = title;
    }
    
    public void setDialogMessage(String message){
        this.message = message;
    }
    
    public View addButton(int color, String text){
        if(buttonList.size() < 3){
            TextView button = new TextView(context);
            button.setText(text);
            button.setTextColor(getResources().getColor(color));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = ConvertDPtoPX(context,8);
            lp.gravity = Gravity.BOTTOM;
            button.setLayoutParams(lp);
            button.setPadding(ConvertDPtoPX(context,16),ConvertDPtoPX(context,8),ConvertDPtoPX(context,16),0);
            button.setTextSize(16);
            buttonList.add(button);
            return button;
        }else {
            return null;
        }
    }
    
    public static int ConvertSPtoPX(@NonNull Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    
    public static int ConvertDPtoPX(@NonNull Context context, int dp) {
        return Math.round((float) dp * context.getResources().getDisplayMetrics().density);
    }
}
