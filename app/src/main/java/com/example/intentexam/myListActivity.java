package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class myListActivity extends AppCompatActivity {// 작성한 글 목록 클래스
    Button rvListButton;
    ListView myList;
    Button bdListButton;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    SharedPreferences sf;


    final ArrayList<String> reviewContent = new ArrayList<>();
    final ArrayList<String> reviewSubContent = new ArrayList<>();
    final ArrayList<String> hosList = new ArrayList<>();

    final ArrayList<String> boardTitle = new ArrayList<>();
    final ArrayList<String> boardContent = new ArrayList<>();
    final ArrayList<String> boardSubContent = new ArrayList<>();

    final ArrayList<ItemData> oData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list);
        sf = getSharedPreferences("info", MODE_PRIVATE);
        String id = sf.getString("inputId", "");

        mDatabase = FirebaseDatabase.getInstance();
        new Runnable() {
            @Override
            public void run() {
                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                oAdapter.notifyDataSetChanged();
            }
        };
        myList = findViewById(R.id.my_list);

        rvListButton = findViewById(R.id.rv_list);//작성한 병원 리뷰 로드 버튼
        rvListButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                boardContent.clear();
                boardSubContent.clear();
                boardTitle.clear();
                oData.clear();
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
                                        String db_date = messageData.child("date").getValue().toString();

                                        if (db_id.equals(id)) {//로그인한 id와 디비의 id가 같으면

                                            Log.d("이계정이 가진 글목록", db_content);
                                            String subCon = db_id + "        " + db_date + "         " + dbHosList;
                                            if (reviewContent.contains(db_content) && reviewSubContent.contains(subCon)) {
                                                reviewContent.set(reviewContent.indexOf(db_content), db_content);
                                                reviewSubContent.set(reviewSubContent.indexOf(subCon), subCon);
                                            } else {
                                                hosList.add(dbHosList);
                                                reviewContent.add(db_content);
                                                reviewSubContent.add(subCon);
                                            }


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
                            if (oData.size() != reviewContent.size()) {
                                oItem.strTitle = reviewContent.get(i);
                                oItem.strDate = reviewSubContent.get(i);
                                Log.d("작성자 확인:", reviewSubContent.get(i));
                                oData.add(oItem);
                            }


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

        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//리스트뷰 롱 클릭리스너
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(myListActivity.this);

                builder.setTitle("해당 글을 삭제 하시겠습니까?").setMessage("삭제 후 버튼을 터치해 갱신해 주세요");


                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {//작성한 글 삭제
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ListViewAdapter oAdapter = new ListViewAdapter(oData);
                        oAdapter.notifyDataSetChanged();
                        String data = oData.get(position).toString();

                        if (!reviewContent.isEmpty()) {//병원 리뷰 글 삭제

                            oData.remove(position);

                            mReference = mDatabase.getReference().child("hospitalreview").child(hosList.get(position));
                            mReference.child(reviewContent.get(position)).setValue(null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                            reviewContent.remove(reviewContent.indexOf(reviewContent.get(position)));
                                            reviewSubContent.remove(reviewSubContent.indexOf(reviewSubContent.get(position)));
                                            oAdapter.notifyDataSetChanged();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        } else if (!boardContent.isEmpty()) { //게시글 삭제
                            oData.remove(position);

                            mReference = mDatabase.getReference().child("board"); // 지워야할 내용에 해당되는 부분 지우기
                            mReference.child(boardContent.get(position)).setValue(null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                            boardTitle.remove(boardTitle.indexOf(boardTitle.get(position)));
                                            boardContent.remove(boardContent.indexOf(boardContent.get(position)));
                                            boardSubContent.remove(boardSubContent.indexOf(boardSubContent.get(position)));
                                            oData.remove(oData.remove(position));
                                            oAdapter.notifyDataSetChanged();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {//취소 버튼
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        bdListButton = findViewById(R.id.bd_list);//게시글 목록 로드 버튼
        bdListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reviewContent.clear();
                reviewSubContent.clear();
                oData.clear();
                mReference = mDatabase.getReference("board");
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                            String dbKey = messageData.getKey();

                            mReference = mDatabase.getReference("board");
                            mReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                                        String db_title = messageData.child("title").getValue().toString();
                                        String db_content = messageData.child("content").getValue().toString();
                                        String db_id = messageData.child("id").getValue().toString();
                                        String db_date = messageData.child("date").getValue().toString();

                                        if (db_id.equals(id)) {//로그인한 id와 내용의 id가 같으면

                                            Log.d("이계정이 가진 글목록", db_content);
                                            String subCon = db_id + "        " + db_date;
                                            // 한 병원에 계정 하나가 하나의 리뷰만 등록가능
                                            if (boardTitle.contains(db_title) && boardSubContent.contains(subCon)) {

                                                boardTitle.set(boardTitle.indexOf(db_title), db_title);
                                                boardContent.set(boardContent.indexOf(db_content), db_content);
                                                boardSubContent.add(subCon);

                                            } else {
                                                boardTitle.add(db_title);
                                                boardContent.add(db_content);
                                                boardSubContent.add(subCon);
                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }

                            });
                        }
                        Log.d("리스트 길이 : ", String.valueOf(boardTitle.size()));
                        if (!boardContent.isEmpty()) {
                            for (int i = 0; i < boardTitle.size(); i++) {
                                ItemData oItem = new ItemData();
                                if (oData.size() != boardTitle.size()) {
                                    oItem.strTitle = boardTitle.get(i);
                                    oItem.strDate = boardSubContent.get(i);
                                    Log.d("작성자 확인:", boardSubContent.get(i));
                                    oData.add(oItem);
                                }


                            }
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