package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class hospitalWrite extends AppCompatActivity {

    EditText r_content;
    Button review_Button;
    Button r_cancelButton;
    SharedPreferences sf;
    Spinner select_hospital;
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    private searchAdapter adapter;
    final ArrayList<String> hpName = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_write);
        r_content = findViewById(R.id.r_content);
        r_cancelButton = findViewById(R.id.r_cancelButton);

        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd");

        Date time = new Date();

        String time1 = format1.format(time);
        sf =getSharedPreferences("info", MODE_PRIVATE);

        review_Button = findViewById(R.id.review_Button);
        review_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = r_content.getText().toString().trim();


                String writer = sf.getString("inputId", "");
                writeNewUser(content, writer, String.valueOf(time1));




            }
        });
        r_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void writeNewUser(String content, String writer, String date) {
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
        public String writer;
        public String date;


        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String content, String writer, String date) {
            this.content = content;
            this.writer = writer;
            this.date = date;

        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getWriter() {
            return writer;
        }

        public void setWriter(String id) {
            this.writer = id;
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
                    "writer='" + writer + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }
}