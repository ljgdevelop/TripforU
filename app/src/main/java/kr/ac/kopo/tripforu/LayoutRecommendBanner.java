package kr.ac.kopo.tripforu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LayoutRecommendBanner extends LinearLayout {
    private Path path;
    private Context context;
    private int size = 0;
    private SharedSchedule sharedSchedule;
    
    /**
     * @author 이제경
     * @param context
     * @param size
     * @param sharedSchedule
     *
     *      커스텀 뷰의 생성자
     */
    public LayoutRecommendBanner(Context context, int size, SharedSchedule sharedSchedule){
        super(context, null, 0);
        this.context = context;
        this.size = size;
        this.sharedSchedule = sharedSchedule;
        init(context);
    }
    
    private void init(Context context){
        View fullView = null;
        if(size == 0)
            fullView = inflate(getContext(), R.layout.layout_recommend_banner, this);
        else
            fullView = inflate(getContext(), R.layout.layout_recommend_smallbanner, this);
        
        fullView.setClipToOutline(true);
        fullView.findViewById(R.id.IMG_RecommandBanner).setClipToOutline(true);
        
        //메인 이미지 설정
        StringBuilder url = new StringBuilder(JsonController.readJsonObjFromAssets("json/awsS3Key.json", context).get("baseUrl").toString());
        url.append(sharedSchedule.getTitleImgId());
        url.append(".jpg");
        Glide.with(context).load(url.toString()).into((ImageView) fullView.findViewById(R.id.IMG_RecommandBanner));
        
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerTitle)).setText(sharedSchedule.getTitleText());
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerDesc)).setText(sharedSchedule.getDescriptionText());
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerRating)).setText(sharedSchedule.getRating() + "");
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerSharedCount)).setText(sharedSchedule.getSharedCount() + "");
        ((TextView)fullView.findViewById(R.id.TEXT_RecommendBannerLikes)).setText(sharedSchedule.getLikes() + "");
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
    
    public SharedSchedule getSharedSchedule(){return this.sharedSchedule;}
}
