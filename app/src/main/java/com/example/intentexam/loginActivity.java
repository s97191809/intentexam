package com.example.intentexam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class loginActivity extends AppCompatActivity {//로그인 클래스

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    EditText input_id;
    EditText input_pw;
    String name;
    String weight;
    String coin;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_button = findViewById(R.id.login_button);
        input_id = (EditText) findViewById(R.id.login_id);
        input_pw = (EditText) findViewById(R.id.login_password);

        Button joinButton = (Button) findViewById(R.id.join);//회원 가입 버튼
        joinButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getApplicationContext(), joinActivity.class);
                startActivity(intent);
            }
        });
        login_button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {//로그인 버튼
                initDatabase();
                mReference = mDatabase.getReference("user"); 
                String id = input_id.getText().toString().trim();
                String pw = input_pw.getText().toString().trim();

                checkEmpty(id, pw);
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String f_id = "";
                        String f_pw = "";
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String db_id = messageData.child("userId").getValue().toString().trim();
                            String db_pw = messageData.child("password").getValue().toString().trim();

                            Log.d("input_id: ", id + ", " +
                                    "input_pw: " + pw + ", " +
                                    "db_id: " + db_id + ", " +
                                    "db_pw: " + db_pw);
                            if (db_id.equals(id) && db_pw.equals(pw)) {
                                name = messageData.child("userName").getValue().toString().trim();
                                weight = messageData.child("weight").getValue().toString().trim();
                                coin = messageData.child("coin").getValue().toString().trim();
                                f_id = id;
                                f_pw = pw;
                                break;
                            }
                        }
                        if (f_id.equals(id) && f_pw.equals(pw)) {
                            if (f_id != "" && f_pw != "") {

                                startLoading();
                                loading();
                            }
                        } else {
                            nonInfo();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReference.addChildEventListener(mChild);
    }


    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }

    private void checkEmpty(String id, String pw) {//공백 확인
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }
    
    //Toast화면 출력
    private void nonInfo() {
        Toast.makeText(this, "입력 정보를 확인해 주세요.", Toast.LENGTH_SHORT).show();
    }
    private void loading() {
        Toast.makeText(this, "로그인 중입니다.", Toast.LENGTH_SHORT).show();
    }

    private void startLoading() {//회원 정보
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //키-값 데이터 저장
                SharedPreferences auto = getSharedPreferences("info", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.putString("inputId", input_id.getText().toString());
                autoLogin.putString("inputPwd", input_pw.getText().toString());
                autoLogin.putString("name", name);
                autoLogin.putString("weight", weight);
                autoLogin.putString("coin", coin);
                autoLogin.commit();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
    public abstract class OnSingleClickListener implements View.OnClickListener {// 싱글 클릭 리스너

        //중복 클릭 방지 시간 설정 ( 해당 시간 이후에 다시 클릭 가능 )
        private static final long MIN_CLICK_INTERVAL = 600;
        private long mLastClickTime = 0;

        public abstract void onSingleClick(View v);

        @Override
        public final void onClick(View v) {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;

            // 중복클릭 아닌 경우
            if (elapsedTime > MIN_CLICK_INTERVAL) {
                onSingleClick(v);
            }
        }
    }

}

