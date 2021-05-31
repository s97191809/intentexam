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
    final ArrayList<String> reviewList = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdrawal);


        sf = getSharedPreferences("info", MODE_PRIVATE);
        String id = sf.getString("inputId", "");
        //탈퇴할 때 지울건 병원 리뷰, 유저 정보, 일정
        //함수로 만들어서 지울 child는 리스트에 넣어두고 for문
        // 지울 아디를 인자로 받아와서 삭제 키는id로 지정해서 remove
        mDatabase = FirebaseDatabase.getInstance();

        Log.d("확인 : ", String.valueOf(reviewList.size()));

        //가져온 동물병원들 리스트를 차일드에 넣어서 id값을 포함한 녀석들을 발견하면 그자식들을 null로 바꿔버리는거지!


        withdrawal_ok_Button = findViewById(R.id.withdrawal_ok_Button);
        withdrawal_ok_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delUser(id);
                delHospitalReview(id);
                delCalender(id);
            }
        });
        withdrawal_cancelButton =findViewById(R.id.withdrawal_cancelButton);
        withdrawal_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void delCalender(String id) {
        mReference = mDatabase.getReference("calender"); // 캘린더 정보 불러오기
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_content = messageData.child("content").getValue().toString();
                    String db_id = messageData.child("id").getValue().toString();
                    Log.d("컨텐츠 : ", db_content);
                    if (db_id.equals(id)) {
                        mReference = mDatabase.getReference().child("calender"); // 지워야할 내용에 해당되는 부분 지우기
                        mReference.child(db_content).setValue(null)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("이계정이 가진 글목록", db_content);

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

    public void delHospitalReview(String id) {
        mReference = mDatabase.getReference("hospitalreview"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String dbHosList = messageData.getKey();//병원이름받고
                    //Log.d("이계정이 가진 글목록 를! 불러올 병원들", db_content);
                    mReference = mDatabase.getReference("hospitalreview").child(dbHosList); // 병원이름들 하나하나 넣음
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                                String db_content = messageData.child("content").getValue().toString();
                                String db_id = messageData.child("id").getValue().toString();
                                if (db_id.equals(id)) {//로그인한 id와 내용의 id가 같으면
                                    Log.d("이계정이 가진 글목록", db_content);

                                    mReference = mDatabase.getReference().child("hospitalreview").child(dbHosList); // 지워야할 병원에 해당되는 병원
                                    mReference.child(db_content).setValue(null)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("이계정이 가진 글목록", dbHosList);

                                                }
                                                // id를 포함한 값들 삭제 먼저 다 읽어지는지 보기;
                                                //회원탈퇴하면 병원리뷰나, 작성글, 정보 지우기

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

    public void delUser(String id) {
        mReference = mDatabase.getReference().child("user"); // 변경값을 확인할 child 이름
        // user는 그냥 id로 바로하면 되는데 캘린더랑 병원 리뷰는 한번 더 내려가서 포함하는 값을 찾아야함ㅋㅋ
        mReference.child(id).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(drawalActivity.this, loginActivity.class);
                        startActivity(intent);
                        Toast.makeText(drawalActivity.this, "회원 탈퇴 완료.", Toast.LENGTH_SHORT);
                        finish();
                    }
                    // id를 포함한 값들 삭제 먼저 다 읽어지는지 보기;
                    //회원탈퇴하면 병원리뷰나, 작성글, 정보 지우기

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(drawalActivity.this, "회원 탈퇴 실패.", Toast.LENGTH_SHORT);

            }
        });
    }
}