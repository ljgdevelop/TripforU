package kr.ac.kopo.tripforu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LayoutRecommendBanner extends LinearLayout {
    private Path path;
    private Context context;
    private int size = 0;
    private SharedSchedule sharedSchedule;
    
    /**
     * @author 이제경
     * @param context
     * @param attrs
     * @param defStyleAttr
     *
     *      커스텀 뷰의 생성자
     */
    public LayoutRecommendBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public LayoutRecommendBanner(Context context, int size, SharedSchedule sharedSchedule){
        super(context, null, 0);
        this.size = size;
        this.sharedSchedule = sharedSchedule;
        init(context);
    }
    public LayoutRecommendBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public LayoutRecommendBanner(Context context){
        this(context, null);
    }
    
    private void init(Context context){
        this.context = context;
        View fullView = null;
        if(size == 0)
            fullView = inflate(getContext(), R.layout.layout_recommend_banner, this);
        else
            fullView = inflate(getContext(), R.layout.layout_recommend_smallbanner, this);
        
        fullView.setClipToOutline(true);
        fullView.findViewById(R.id.IMG_RecommandBanner).setClipToOutline(true);
        
        ScheduleController.getInstance().getSharedSchedules();
        
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerTitle)).setText(sharedSchedule.getTitleText());
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerDesc)).setText(sharedSchedule.getDesctriptionText());
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerRating)).setText(sharedSchedule.getRating() + "");
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerSharedCount)).setText(sharedSchedule.getSharedCount());
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerLikes)).setText(sharedSchedule.getLikes());
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (path == null) {
            path = new Path();
            path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), 15, 15, Path.Direction.CW);
        }
        Log.d("TAG", "dispatchDraw: " + path);
        canvas.clipPath(path);
        findViewById(R.id.IMG_RecommandBanner).setClipToOutline(true);
        super.dispatchDraw(canvas);
    }
}
