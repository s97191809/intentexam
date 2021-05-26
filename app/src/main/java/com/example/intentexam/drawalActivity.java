package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class drawalActivity extends AppCompatActivity {
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
        mReference = mDatabase.getReference().child("user"); // 변경값을 확인할 child 이름
        
        // user는 그냥 id로 바로하면 되는데 캘린더랑 병원 리뷰는 한번 더 내려가서 포함하는 값을 찾아야함
        mReference.setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
/*                        Intent intent = new Intent(drawalActivity.this, loginActivity.class);
                        startActivity(intent);*/
                        Toast.makeText(drawalActivity.this,"회원 탈퇴 완료.",Toast.LENGTH_SHORT);
                        finish();
                    }
                    // id를 포함한 값들 삭제 먼저 다 읽어지는지 보기;
                    //회원탈퇴하면 병원리뷰나, 작성글, 정보 지우기

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(drawalActivity.this,"회원 탈퇴 실패.",Toast.LENGTH_SHORT);

            }
        });
    }
}