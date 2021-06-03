package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class hospitalWrite extends AppCompatActivity {//병원 리뷰 작성 클래스

    EditText r_content;
    Button review_Button;
    Button r_cancelButton;
    SharedPreferences sf;
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    final ArrayList<String> hpName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_write);
        r_content = findViewById(R.id.r_content);
        r_cancelButton = findViewById(R.id.r_cancelButton);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");//날짜 계산

        Date time = new Date();

        String time1 = format1.format(time);
        sf = getSharedPreferences("info", MODE_PRIVATE);

        review_Button = findViewById(R.id.review_Button);//리뷰 작성 버튼
        review_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = r_content.getText().toString().trim();


                String writer = sf.getString("inputId", "");
                writeNewUser(content, writer, String.valueOf(time1));


            }
        });
        r_cancelButton.setOnClickListener(new View.OnClickListener() {//리뷰 작성 취소 버튼
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void writeNewUser(String content, String writer, String date) {//디비 작성 메소드
        User user = new User(content, writer, date);


        Intent intent = getIntent(); /*데이터 수신*/

        String name = intent.getExtras().getString("hpName");
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("hospitalreview").child(name); // 변경값을 확인할 child 이름
        mReference.child(content).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                toString();

                finish();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed


                    }
                });

    }

    public class User {//디비 업로드를 위한 User클래스
        public String content;
        public String id;
        public String date;


        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String content, String writer, String date) {
            this.content = content;
            this.id = writer;
            this.date = date;

        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }


        @Override
        public String toString() {
            return "User{" +
                    "content='" + content + '\'' +
                    "id='" + id + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }
}