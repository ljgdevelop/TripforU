package kr.ac.kopo.tripforu.AWS;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.*;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;

import java.io.File;

import org.json.simple.JSONObject;

import kr.ac.kopo.tripforu.JsonController;

@RequiresApi(api = Build.VERSION_CODES.O)
public class S3 {
    // Amazon-s3-sdk
    private String accessKey = "-"; // 액세스키
    private String secretKey = "-"; // 스크릿 엑세스 키
    
    private Regions clientRegion = Regions.AP_NORTHEAST_2; // 한국
    private String bucket = "tripforu"; // 버킷 명
    
    String cacheDirl;
    
    //context
    private Context context;
    
    private S3(Context context) {
        init(context);
    }
    
    // singleton 으로 구현
    static private S3 instance = null;
    
    public static S3 getInstance(Context context) {
        return new S3(context);
    }
    
    public static S3 getInstance(){
        if(instance != null)
            return instance;
        return null;
    }
    
    // aws S3 client 생성
    private void init(Context context) {
        JSONObject jObj = JsonController.readJsonObjFromAssets("json/awsS3Key.json", context);
        this.accessKey = jObj.get("accessKey").toString();
        this.secretKey = jObj.get("secretAccessKey").toString();
        this.context = context;
        cacheDirl = context.getCacheDir().getPath();
    }
    
    public void uploadWithTransferUtilty(String fileName, File file) {
        Log.d("MYTAG", "UPLOAD - - START : " + accessKey + ":" + secretKey);
        try {
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(clientRegion));
            
            TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(context).build();
            TransferNetworkLossHandler.getInstance(context);
            
            TransferObserver uploadObserver = transferUtility.upload(bucket, fileName, file);
            
            uploadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        // Handle a completed upload
                        Log.d("MYTAG", "UPLOAD - - END");
                    }
                }
                
                @Override
                public void onProgressChanged(int id, long current, long total) {
                    int done = (int) (((double) current / total) * 100.0);
                    Log.d("MYTAG", "UPLOAD - - ID: $id, percent done = " + done);
                }
                
                @Override
                public void onError(int id, Exception ex) {
                    Log.d("MYTAG", "UPLOAD ERROR - - ID: $id - - EX:" + ex.toString());
                }
            });
        }catch (Exception e){
            Log.e("TAG", "uploadWithTransferUtilty: ", e);
        }
    }
}