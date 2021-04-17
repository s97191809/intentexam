package com.example.intentexam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class loginActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_button);
        findViewById(R.id.login_id);
        findViewById(R.id.login_password);
        // id pw 받아서 디비에 있는지 보고 없으면 x 있으면 메인으로 가게
        Button imageButton = (Button) findViewById(R.id.join);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), joinActivity.class);
                startActivity(intent);
            }
        });
    }

}// MainActivity Class..

