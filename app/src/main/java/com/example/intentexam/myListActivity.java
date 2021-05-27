package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myListActivity extends AppCompatActivity {
    Button rvListButton;
    ListView myList;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    SharedPreferences sf;

    final ArrayList<String> reviewContent = new ArrayList<>();
    final ArrayList<String> subContent = new ArrayList<>();
    final ArrayList<ItemData> oData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list);
        // 작성한 글 보여주기
        // 리뷰 버튼 누르면 리스트 나오고 길게 터치하면 삭제?
        // 버튼 누르면 디비에서 값을 슥 가지고옴 조건은 id 를 포함하면 걸러짐
        sf = getSharedPreferences("info", MODE_PRIVATE);
        String id = sf.getString("inputId", "");

        mDatabase = FirebaseDatabase.getInstance();

        myList = findViewById(R.id.my_list);

        rvListButton = findViewById(R.id.rv_list);
        rvListButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
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
                                        String db_date = messageData.child("date").getValue().toString();
                                        if (db_id.equals(id)) {//로그인한 id와 내용의 id가 같으면
                                            Log.d("이계정이 가진 글목록", db_content);
                                  /*          if (reviewContent.contains(db_content)) {
                                  //              reviewContent.remove(db_content);
                                                // 같은 내용도 생각하기
                                                // 같은날 같은제목 일수도
                                               // 리뷰 번호가 필요한가 필요하겠다//
                                            } else {*/
                                                reviewContent.add(db_content);
                                                subContent.add(db_id + "        " + db_date + "         " + dbHosList);
                                            /*}*/


                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });
                        }
                        Log.d("리스트 길이 : ", String.valueOf(reviewContent.size()));
                        for (int i = 0; i < reviewContent.size(); i++) {
                            ItemData oItem = new ItemData();
                            oItem.strTitle = reviewContent.get(i);
                            oItem.strDate = subContent.get(i);
                            Log.d("작성자 확인:", subContent.get(i));
                            oData.add(oItem);

                        }
// ListView, Adapter 생성 및 연결 ------------------------

                        ListViewAdapter oAdapter = new ListViewAdapter(oData);
                        myList.setAdapter(oAdapter);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


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