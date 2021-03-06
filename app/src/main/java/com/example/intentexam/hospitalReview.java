package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;

public class hospitalReview extends AppCompatActivity {//병원 리뷰 클래스
    ListView review_list;
    Button write_r;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    final ArrayList<String> reviewContent = new ArrayList<>();
    final ArrayList<String> subContent = new ArrayList<>();
    final ArrayList<ItemData> oData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_board);

        

        Intent intent = getIntent();//병원이름 수신

        String name = intent.getExtras().getString("hpName");

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("hospitalreview").child(name);//병원 리뷰 리스트 생성을 위한 디비 로드
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                        String db_content = messageData.child("content").getValue().toString();
                        String db_writer = messageData.child("id").getValue().toString();
                        String db_date = messageData.child("date").getValue().toString();
                        if(reviewContent.contains(db_content)){
                            reviewContent.remove(db_content);
                        }else{
                        reviewContent.add(db_content);
                        subContent.add(db_writer+"        "+db_date);
                        }

                    // 리스트뷰 참조 및 Adapter달기
                  }

                for (int i = 0; i < reviewContent.size(); i++)
                {
                    ItemData oItem = new ItemData();
                    oItem.strTitle = reviewContent.get(i);
                    oItem.strDate = subContent.get(i);
                    Log.d("작성자 확인:", subContent.get(i));
                    oData.add(oItem);

                }
        // ListView, Adapter 생성 및 연결
                review_list = (ListView)findViewById(R.id.review_list);//병원 리뷰 리스트 생성
                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                review_list.setAdapter(oAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        write_r = findViewById(R.id.write_r);//병원 리뷰 작성 버튼
        write_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hospitalReview.this, hospitalWrite.class);
                intent.putExtra("hpName", name);

                startActivity(intent);

            }
        });



    }
}