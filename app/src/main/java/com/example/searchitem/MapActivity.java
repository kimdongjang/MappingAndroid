package com.example.searchitem;

import android.app.FragmentManager;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public double w_value = 37.56; // 위도
    public double s_value = 126.97; // 경도
    public String server_ip = "";
    FragmentManager fragmentManager = null;
    MapFragment mapFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Map View Api 사용을 위한 초기화
        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 메인->맵 액티비티로 넘어올때 데이터전달
        Intent intent = getIntent(); /*데이터 수신*/
        server_ip = intent.getExtras().getString("ip"); /*String형*/
        Init();
    }


    private void Init(){
        // ModuleList로 액티비티 전환
        Button button = findViewById(R.id.Btn_SearchModule);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면 제어
                        ModuleListActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어감
            }
        });
        /*
        SocketActivity Socket = new SocketActivity();
        String idByANDROID_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Socket.data = "AccessAndorid" + "#" + idByANDROID_ID;
        Socket.mapActivity = this;

        Socket.start();
        */

        String idByANDROID_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Thread t= new Thread(new SocketActivity("AccessAndorid" + "#" + idByANDROID_ID, server_ip));
        t.start();
        try{
            //메인 스레드가 종료되지 않도록 join을 호출
            //t.join();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void RecvAccess(String msg){
        String [] sArray = msg.split("#");

        if(sArray[0] == "AccessAndroidSucess"){
            Toast.makeText(this, "접속 성공" , Toast.LENGTH_SHORT).show();
        }
        else if(sArray[0] == "AccessAndroidFail") {
            Toast.makeText(this, "접속 실패" , Toast.LENGTH_SHORT).show();
        }
        else if(sArray[0] == "SendGpsToAndroid") {
            Toast.makeText(this, "송신" , Toast.LENGTH_SHORT).show();
            // 위도 경도 초기화
            w_value = Double.parseDouble(sArray[1]);
            s_value = Double.parseDouble(sArray[2]);
            // 맵 갱신
            mapFragment.getMapAsync(this);
        }

        else{
            Toast.makeText(this, "Error" , Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        // 위도, 경도
        LatLng SEOUL = new LatLng(w_value, s_value);
        //LatLng SEOUL = new LatLng(37.56, 25.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        /*
        // camera 좌쵸를 서울역 근처로 옮겨 봅니다.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(37.555744, 126.970431)   // 위도, 경도
        ));
        */
        /*// 마커클릭 이벤트 처리
        // GoogleMap 에 마커클릭 이벤트 설정 가능.
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 클릭시 호출되는 콜백 메서드
                Toast.makeText(getApplicationContext(),
                        marker.getTitle() + " 클릭했음"
                        , Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
