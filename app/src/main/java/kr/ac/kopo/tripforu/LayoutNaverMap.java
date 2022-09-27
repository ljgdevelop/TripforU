package kr.ac.kopo.tripforu;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class LayoutNaverMap extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private static NaverMap naverMap;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout_naver_map);

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3l42j6ptcl")
        );

        mapView = (MapView) findViewById(R.id.map_View);
        mapView.onCreate(saveInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        this.naverMap = naverMap;

        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(127, 37), 8
        );
        naverMap.setCameraPosition(cameraPosition);

        Marker marker = new Marker();
        marker.setPosition(new LatLng(123,37));
        marker.setMap(naverMap);
    }
}
