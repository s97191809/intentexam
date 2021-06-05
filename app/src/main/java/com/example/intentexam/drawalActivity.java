package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class drawalActivity extends AppCompatActivity {
    Button withdrawal_ok_Button;
    Button withdrawal_cancelButton;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdrawal);

        sf = getSharedPreferences("info", MODE_PRIVATE);
        String id = sf.getString("inputId", "");
        mDatabase = FirebaseDatabase.getInstance();

        withdrawal_ok_Button = findViewById(R.id.withdrawal_ok_Button);// 회원 탈퇴 버튼
        withdrawal_ok_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delUser(id);
                delHospitalReview(id);
                delCalender(id);
            }
        });
        withdrawal_cancelButton = findViewById(R.id.withdrawal_cancelButton);//회원 탈퇴 취소
        withdrawal_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void delCalender(String id) {// 회원 캘린더 정보 삭제
        mReference = mDatabase.getReference("calender"); // 캘린더 정보 불러오기
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_content = messageData.child("content").getValue().toString();
                    String db_id = messageData.child("id").getValue().toString();

                    if (db_id.equals(id)) {
                        mReference = mDatabase.getReference().child("calender");
                        mReference.child(db_content).setValue(null)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
            }
        });

    }

    public void delHospitalReview(String id) {//회원 병원별 리뷰 정보 삭제
        mReference = mDatabase.getReference("hospitalreview");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String dbHosList = messageData.getKey();

                    mReference = mDatabase.getReference("hospitalreview").child(dbHosList);
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                                String db_content = messageData.child("content").getValue().toString();
                                String db_id = messageData.child("id").getValue().toString();
                                if (db_id.equals(id)) {

                                    mReference = mDatabase.getReference().child("hospitalreview").child(dbHosList);
                                    mReference.child(db_content).setValue(null)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                           
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delUser(String id) {//회원 정보 삭제
        mReference = mDatabase.getReference().child("user");
        mReference.child(id).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(drawalActivity.this, loginActivity.class);
                        startActivity(intent);
                        Toast.makeText(drawalActivity.this, "회원 탈퇴 완료.", Toast.LENGTH_SHORT);
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(drawalActivity.this, "회원 탈퇴 실패.", Toast.LENGTH_SHORT);

            }
        });
    }
}