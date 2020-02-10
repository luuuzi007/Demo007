package com.example.demo007;

import android.os.Bundle;
import android.view.View;

import com.example.annotion.BindPath;
import com.example.arouter.Arouter;

import androidx.appcompat.app.AppCompatActivity;

//app模块
@BindPath("main/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arouter.newInstance().jumpActivity("login/login",null);
            }
        });
    }
}
