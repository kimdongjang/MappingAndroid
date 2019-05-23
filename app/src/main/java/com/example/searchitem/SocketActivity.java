package com.example.searchitem;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SocketActivity implements Runnable {
    //서버주소
    public static String sIP = "61.81.99.194";
    //public static final String sIP = "14.5.219.180";
    //사용할 통신 포트
    public static int sPORT = 9999;

    public String data; // 서버로 보낼 데이터
    public String send_kinds; // 데이터를 보내는 패킷 종류
    public String recv_kinds; // 데이터를 받은 패킷 종류
    public MainActivity mainActivity; // 로그인 액티비티
    public CreateAccountActivity accountActivity; // 회원가입 액티비티
    public MapActivity mapActivity; // 맵 액티비티
    private boolean loop = false;
    public SocketActivity(String data, String server_ip){ this.data = data; this.sIP = server_ip;  }

    public void run() {
        try {
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
            while(loop){
                try{
                    //데이터 수신 대기
                    socket.receive(packet);
                    if(packet.getData()!=null) {
                        //데이터 수신되었다면 문자열로 변환
                        String msg = new String(packet.getData());
                        System.out.println("수신된 데이터 : "+ msg);

                        if(accountActivity != null){
                            accountActivity.RecvCreateAccount(msg);
                        }
                        else if(mainActivity != null){
                            mainActivity.RecvLogin(msg);
                        }
                        else if(mapActivity != null){
                            mapActivity.RecvAccess(msg);
                        }
                        // 수신했으면 일단 닫음
                        loop = false;
                        socket.close();
                    }
                    /*
                    if(packet.getData()!=null){
                        String str = new String(packet.getData()).trim();
                        System.out.println("수신된 데이터 : "+str);

                        InetAddress ia = packet.getAddress();
                        int port = packet.getPort();

                        byte[] s = str.getBytes();
                        packet = new DatagramPacket(s, s.length, ia, port);
                        socket.send(packet);
                    }
                    */
                    //buffer을 재사용하기 위해 리셋
                    //Arrays.fill(buffer, (byte)0);

                }catch(SocketTimeoutException e){
                }catch(SocketException e){
                }catch(Exception e){
                    System.out.printf("S: Error\n",e);
                    e.printStackTrace();
                }
            }

            //txtView에 표시
            //txtView.setText(msg);
        } catch (Exception e) {
            System.out.println(e.toString());
            loop = false;
        }
    }

}