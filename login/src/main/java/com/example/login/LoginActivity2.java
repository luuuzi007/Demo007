package com.example.login;

import android.os.Bundle;
import android.view.View;

import com.example.annotion.BindPath;
import com.example.arouter.Arouter;

import androidx.appcompat.app.AppCompatActivity;

//登录模块
@BindPath("login2/login2")
public class LoginActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arouter.newInstance().jumpActivity("mine",null);
            }
        });
    }
}
