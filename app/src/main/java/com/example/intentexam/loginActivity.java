package com.example.intentexam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

public class loginActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    EditText input_id;
    EditText input_pw;
    String name;
    String weight;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_button = findViewById(R.id.login_button);
        input_id = (EditText) findViewById(R.id.login_id);
        input_pw = (EditText) findViewById(R.id.login_password);

        Button joinButton = (Button) findViewById(R.id.join);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), joinActivity.class);
                startActivity(intent);
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatabase();
                mReference = mDatabase.getReference("user"); // 변경값을 확인할 child 이름
                String id = input_id.getText().toString().trim();
                String pw = input_pw.getText().toString().trim();

                checkEmpty(id, pw);
                mReference.addValueEventListener(new ValueEventListener() {
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

    private void checkEmpty(String id, String pw) {
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    private void nonInfo() {
        Toast.makeText(this, "입력 정보를 확인해 주세요.", Toast.LENGTH_SHORT).show();
    }

    private void loading() {
        Toast.makeText(this, "로그인 중입니다.", Toast.LENGTH_SHORT).show();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //값 저장해주는 부분
                SharedPreferences auto = getSharedPreferences("info", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();
                autoLogin.putString("inputId", input_id.getText().toString());
                autoLogin.putString("inputPwd", input_pw.getText().toString());
                autoLogin.putString("name", name);
                autoLogin.putString("weight", weight);
                autoLogin.commit();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

}

