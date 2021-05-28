package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class writeBoardActivity extends AppCompatActivity {
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    EditText a_title;
    EditText board_content;
    Button board_w_Button;
    Button board_cancelButton;
    SharedPreferences sf;
    final ArrayList<Integer> boardNum = new ArrayList<>();
    private int maxNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_board);

        mDatabase = FirebaseDatabase.getInstance();

        a_title = (EditText) findViewById(R.id.a_title);
        board_content = (EditText) findViewById(R.id.board_content);


        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        sf = getSharedPreferences("info", MODE_PRIVATE);
        String id = sf.getString("inputId", "");

        Date time = new Date();

        String time1 = format1.format(time);

        mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String key = messageData.child("num").getValue(String.class);
                    boardNum.add(Integer.parseInt(key));

                    maxNum=Collections.max(boardNum);
                    Log.d("번호확인 : ", key);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        board_w_Button = findViewById(R.id.board_w_Button);
        board_w_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = a_title.getText().toString().trim();
                String content = board_content.getText().toString().trim();
                writeBoard(content, id, String.valueOf(time1), title, String.valueOf(maxNum+1));
                finish();
            }
        });


        board_cancelButton = findViewById(R.id.board_cancelButton);
        board_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void writeBoard(String content, String writer, String date, String title, String num) {
        User user = new User(content, writer, date, title, num);



        mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
        mReference.child(content).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                toString();


                finish();
                // 리스트뷰 참조 및 Adapter달기


                // 데이터 1000개 생성--------------------------------.
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed


                    }
                });

    }

    public class User {
        public String content;
        public String id;
        public String date;
        public String title;
        public String num;


        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String content, String writer, String date, String title, String num) {
            this.content = content;
            this.id = writer;
            this.date = date;
            this.title = title;
            this.num = num;

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }


        @Override
        public String toString() {
            return "User{" +
                    "content='" + content + '\'' +
                    "id='" + id + '\'' +
                    ", date='" + date + '\'' +
                    ", title='" + title + '\'' +
                    ", num='" + num + '\'' +
                    '}';
        }
    }
}