package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showBoardDetail extends AppCompatActivity {
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    TextView boardTitleView;
    TextView boardDetailView;
    Button boardGpoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_more);

        Intent secondIntent = getIntent();
        String title = secondIntent.getStringExtra("title");
        String content = secondIntent.getStringExtra("content");

        boardTitleView = findViewById(R.id.a_title);
        boardTitleView.setText(title);

        boardDetailView = findViewById(R.id.board_content);
        boardDetailView.setText(content);

        boardGpoint = findViewById(R.id.board_good);
        boardGpoint.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View v) {
                mDatabase = FirebaseDatabase.getInstance();
                mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    String gp;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                            gp = messageData.child("gPoint").getValue().toString();
                            Log.d("받은 좋아요 수 : ", gp);

                        }
                        mReference = mDatabase.getReference().child("board"); // 지워야할 내용에 해당되는 부분 지우기
                        mReference.child(content).child("gPoint").setValue(String.valueOf(Integer.parseInt(gp)+1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                       finish();

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        
        //따봉 버튼을 누르면 해당 게시글에 추천수 증가
        //gPoint 값 가져와서 더해주고 저장
    }
    public abstract class OnSingleClickListener implements View.OnClickListener {

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