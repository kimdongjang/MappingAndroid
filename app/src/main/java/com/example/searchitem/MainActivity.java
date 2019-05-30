package com.example.searchitem;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    String ID = "";
    String PW = "";
    MainActivity mainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
    }
    private void Init(){
        // 객체 초기화
        mainActivity = this;
        ID = ((EditText)findViewById(R.id.Entry_ID)).getText().toString();

        // 버튼 이벤트 초기화
        Button BtnLogin = findViewById(R.id.Btn_Login);
        BtnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /*
                // 로그인 버튼 클릭
                ID = ((EditText)findViewById(R.id.Entry_ID)).getText().toString();
                PW = ((EditText)findViewById(R.id.Entry_PW)).getText().toString();
                SocketActivity Socket = new SocketActivity();
                Socket.send_kinds = "로그인";
                Socket.data = "로그인" + "#" + ID + "#" + PW;
                Socket.mainActivity = mainActivity;

                Socket.start();
                */
                // 지울 예정
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면 제어
                        MapActivity.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("ip",ID); /*송신*/
                startActivity(intent); // 다음 화면으로 넘어감
            }
        });

        Button BtnCreateAccount = findViewById(R.id.Btn_CreateAccount);
        BtnCreateAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                OnIntentActivity(); // 회원가입 모달 호출
            }
        });

    }
    public void RecvLogin(String msg){
        if(msg == "로그인성공"){
            Intent intent = new Intent(
                    getApplicationContext(), // 현재 화면 제어
                    MapActivity.class); // 다음 넘어갈 클래스 지정
            startActivity(intent); // 다음 화면으로 넘어감
        }
        else if(msg == "로그인실패"){

        }
        else{

        }
    }
    private void OnIntentActivity(){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra("data", "Test Popup");
        startActivityForResult(intent, 1);
    }

}
