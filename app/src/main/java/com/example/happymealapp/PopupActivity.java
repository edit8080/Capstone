package com.example.happymealapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends Activity {

    TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);
        txtText.setMovementMethod(new ScrollingMovementMethod());
        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        txtText.setText(data);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        finish();
    }


    //안드로이드 백버튼 막기
    @Override
    public void onBackPressed() {
        return;
    }
}
