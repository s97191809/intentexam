package com.example.intentexam;

import android.content.Intent;
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



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login_button = findViewById(R.id.login_button);
        input_id = (EditText) findViewById(R.id.login_id);
        input_pw = (EditText) findViewById(R.id.login_password);



        // id pw 받아서 디비에 있는지 보고 없으면 x 있으면 메인으로 가게
        Button imageButton = (Button) findViewById(R.id.join);

        imageButton.setOnClickListener(new View.OnClickListener() {
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

        //checkEmpty(id, pw);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String db_id = messageData.child("userId").getValue().toString();
                    String db_pw = messageData.child("password").getValue().toString();
                    Log.d("input_id: ", id + ", " +
                            "input_pw: " + pw + ", " +
                            "db_id: " + db_id+", "+
                            "db_pw: "+db_pw);
                    //흠 바로 찾는 방법 ㅇ벗나.
                }
                startLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
});
    }
    private void initDatabase(){
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




    protected void onDestroy(){
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }
    private void checkEmpty(String id, String pw){
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

}// MainActivity Class..

