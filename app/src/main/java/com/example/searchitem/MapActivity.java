package com.example.searchitem;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int AccessAndroidFail = -1;
    public static final int AccessAndroidSucess = 1;
    public static final int SendGpsToAndroid = 2;

    //서버주소
    public static String sIP = "14.5.219.180";
    public static int sPORT = 9999;

    public double w_value = 37.56; // 위도
    public double s_value = 126.97; // 경도
    public String server_ip = "";
    FragmentManager fragmentManager = null;
    MapFragment mapFragment = null;

    Handler handler; // 스레드 가동 중 리시브 데이터를 받을 핸들러
    GoogleMap h_googleMap;
    String idByANDROID_ID; // 안드로이드 아이디
    public String data;
    private boolean loop = false;
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
        Socket.mapActivity = this;

        Socket.start();
        */
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("대충", "핸들러 불려짐");
                String str;
                Log.d("대충", String.valueOf((msg.what)));
                Log.d("대충", String.valueOf((msg.arg1)));
                Log.d("대충", String.valueOf((msg.obj)));
                Log.d("대충", String.valueOf(w_value));
                Log.d("대충", String.valueOf(s_value));
                // 맵 갱신

                // 위도, 경도
                LatLng SEOUL = new LatLng(w_value, s_value);
                //LatLng SEOUL = new LatLng(37.56, 25.97);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(SEOUL);
                markerOptions.title("서울");
                markerOptions.snippet("한국의 수도");
                h_googleMap.addMarker(markerOptions);

                h_googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                h_googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                switch (msg.what){
                    case AccessAndroidSucess: // 메시지로 넘겨받은 파라미터, 이 값으로 어떤 처리를 할지 결정
                        str = (String)msg.obj;
                        break;
                    case SendGpsToAndroid:
                        /*
                        str = (String)msg.obj;
                        String [] sArray = str.split("#");

                        //Toast.makeText(this, "송신" , Toast.LENGTH_SHORT).show();

                        System.out.println("gps송신받음");
                        // 위도 경도 초기화
                        w_value = Double.parseDouble(sArray[1]);
                        s_value = Double.parseDouble(sArray[2]);
                        // 맵 갱신

                        // 위도, 경도
                        LatLng SEOUL = new LatLng(w_value, s_value);
                        //LatLng SEOUL = new LatLng(37.56, 25.97);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(SEOUL);
                        markerOptions.title("서울");
                        markerOptions.snippet("한국의 수도");
                        h_googleMap.addMarker(markerOptions);

                        h_googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                        h_googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            */
                        break;


                }
            }
        };
        // 안드로이드 아이디 초기화
        idByANDROID_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        data = "AccessAndorid" + "#" + idByANDROID_ID;


        // 스레드 생성
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("대충", "스레드 스타틋");
                    //UDP 통신용 소켓 생성
                    DatagramSocket socket = new DatagramSocket();
                    //서버 주소 변수
                    InetAddress serverAddr = InetAddress.getByName(sIP);

                    //보낼 데이터 생성
                    //byte[] buf = ("Hello World").getBytes();
                    byte[] buf = data.getBytes();

                    //패킷으로 변경
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, sPORT);
                    System.out.println("접속 성공.....");
                    loop = true;

                    //패킷 전송!
                    socket.send(packet);
                    while (loop) {
                        try {
                            //데이터 수신 대기
                            socket.receive(packet);
                            if (packet.getData() != null) {
                                //데이터 수신되었다면 문자열로 변환
                                String msg = new String(packet.getData());
                                System.out.println("수신된 데이터 : " + msg);

                                // 핸들러를 통해 어떤 처리를 할지 확인인
                                Message h_msg = handler.obtainMessage();

                                String[] sArray = msg.split("#");

                                System.out.println("수신된 데이터 : " + sArray[0]);
                                w_value = Double.parseDouble(sArray[1]);
                                s_value = Double.parseDouble(sArray[2]);

                                if (sArray[0] == "AccessAndroidSucess") {
                                    h_msg.what = AccessAndroidSucess;
                                    h_msg.obj = msg;
                                } else if (sArray[0] == "AccessAndroidFail") {
                                    h_msg.what = AccessAndroidFail;
                                    h_msg.obj = msg;
                                } else if (sArray[0] == "SendGpsToAndroid") {
                                    h_msg.what = SendGpsToAndroid;
                                    h_msg.obj = msg;
                                }
                                handler.sendMessage(h_msg); // 핸들러로 메세지 전송
                                Thread.sleep(1000);

                            }
                            //buffer을 재사용하기 위해 리셋
                            //Arrays.fill(buffer, (byte)0);

                        } catch (SocketTimeoutException e) {
                        } catch (SocketException e) {
                        } catch (Exception e) {
                            System.out.printf("S: Error\n", e);
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                    loop = false;
                }
            }

        });

        Log.d("대충", "스레드 스타트 전");
        th.start();

    }

    public void RecvAccess(String msg){
        System.out.println("데이터받음");
        String [] sArray = msg.split("#");

        if(sArray[0] == "AccessAndroidSucess"){
            Toast.makeText(this, "접속 성공" , Toast.LENGTH_SHORT).show();
        }
        else if(sArray[0] == "AccessAndroidFail") {
            Toast.makeText(this, "접속 실패" , Toast.LENGTH_SHORT).show();
        }
        else if(sArray[0] == "SendGpsToAndroid") {
            Toast.makeText(this, "송신" , Toast.LENGTH_SHORT).show();

            System.out.println("gps송신받음");
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
        h_googleMap = map;

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
