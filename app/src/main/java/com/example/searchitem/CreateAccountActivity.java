package com.example.searchitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountActivity extends Activity {
    AlertDialog.Builder builder = null;
    CreateAccountActivity accountActivity = null;
    SocketActivity socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_account);

        Init();
        /*
        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        //txtText.setText(data);
        */
    }
    private void Init(){
        // 객체 초기화
        builder = new AlertDialog.Builder(this);
        accountActivity = this;

        // 버튼 이벤트 초기화
        Button BtnAccountConfirm = findViewById(R.id.btn_account_confirm);
        BtnAccountConfirm.setOnClickListener(new View.OnClickListener(){
            // 회원가입 버튼 클릭
            @Override
            public void onClick(View view){
                String ID = ((EditText)findViewById(R.id.edit_account_id)).getText().toString();
                String PW = ((EditText)findViewById(R.id.edit_account_pw)).getText().toString();
                String PW2 = ((EditText)findViewById(R.id.edit_account_pw2)).getText().toString();
                if(PW != PW2){
                    builder.setTitle("알림");
                    builder.setMessage("비밀번호 확인이 일치하지 않습니다!");
                    builder.setPositiveButton("확인", null);
                    builder.setNegativeButton("취소",null);
                    builder.show();
                    return;
                }
                else
                {
                    /*
                    // 소켓을 통해 회원가입 정보 전송
                    socket = new SocketActivity();
                    socket.send_kinds = "회원가입";
                    socket.data = "회원가입" + "#" + ID + "#" + PW;
                    socket.accountActivity = accountActivity;
                    socket.start();
                    */
                }
            }
        });
        Button BtnAccountCancel = findViewById(R.id.btn_account_cancel);
        BtnAccountCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
    }

    public void RecvCreateAccount(String msg){
        // 객체 초기화
        builder = new AlertDialog.Builder(this);
        // 회원가입에 성공할 경우 로그인 화면으로 전환
        if(msg == "회원가입성공"){
            builder.setTitle("알림");
            builder.setMessage("회원가입에 성공했습니다.");
            builder.setPositiveButton("확인", null);
            builder.setNegativeButton("취소",null);
            builder.show();
            //액티비티(팝업) 닫기
            finish();
        }
        else if(msg == "회원가입실패"){
            builder.setTitle("알림");
            builder.setMessage("회원가입에 실패했습니다.");
            builder.setPositiveButton("확인", null);
            builder.setNegativeButton("취소",null);
            builder.show();
            return;
        }
        else {
            builder.setTitle("알림");
            builder.setMessage("서버 오류");
            builder.setPositiveButton("확인", null);
            builder.setNegativeButton("취소",null);
            builder.show();
            return;
        }
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
