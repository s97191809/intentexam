package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class dayListActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    SharedPreferences getloginInfo;
    ListView day_list;
    final ArrayList<ItemData> oData = new ArrayList<>();
    final ArrayList<String> boardTitle = new ArrayList<>();
    final ArrayList<String> boardContent = new ArrayList<>();
    final ArrayList<String> boardSubContent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daylist);

        getloginInfo = getSharedPreferences("info", MODE_PRIVATE);
        String id = getloginInfo.getString("inputId", "");


        day_list = findViewById(R.id.day_list);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("calender"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boardContent.clear();
                boardTitle.clear();
                boardSubContent.clear();
                oData.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_title = messageData.child("title").getValue().toString();
                    String db_content = messageData.child("content").getValue().toString();
                    String db_id = messageData.child("id").getValue().toString();
                    String db_year = messageData.child("curYear").getValue().toString();
                    String db_month = messageData.child("curMonth").getValue().toString();
                    String db_day = messageData.child("day").getValue().toString();

                    if (db_id.equals(id)) {//로그인한 id와 내용의 id가 같으면
                        String db_date = db_year + "-" + db_month + "-" + db_day;
                        Log.d("이계정이 가진 글목록", db_content);
                        String subCon = db_content;

                        if (boardTitle.contains(db_title) && boardContent.contains(db_content)) {
                        } else {
                            boardTitle.add(db_title);
                            boardContent.add(db_content);
                            boardSubContent.add(subCon);
                        }


                    }
                }
                Log.d("리스트 길이 : ", String.valueOf(boardTitle.size()));

                for (int i = 0; i < boardTitle.size(); i++) {
                    ItemData oItem = new ItemData();
                    if (oData.size() != boardTitle.size()) {
                        oItem.strTitle = boardTitle.get(i);
                        oItem.strDate = boardSubContent.get(i);
                        Log.d("작성자 확인:", boardSubContent.get(i));
                        oData.add(oItem);
                    }
                }
                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                day_list.setAdapter(oAdapter);
            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
// ListView, Adapter 생성 및 연결 ------------------------



        });
        day_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(dayListActivity.this);

                builder.setTitle("해당 글을 삭제 하시겠습니까?").setMessage("삭제 후 버튼을 터치해 갱신해 주세요ㅎ;");


                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ListViewAdapter oAdapter = new ListViewAdapter(oData);
                        String data = oData.get(position).toString();

                            oData.remove(position);
                            //위치가 같으니 해당 위치에 있는 놈을 찾아서 해당하는 리뷰를 삭제하면 되겠습니다.

                            mReference = mDatabase.getReference().child("calender"); // 지워야할 내용에 해당되는 부분 지우기
                            mReference.child(boardTitle.get(position)).setValue(null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            boardContent.clear();
                                            boardTitle.clear();
                                            boardSubContent.clear();
                                            oData.clear();
                                            oAdapter.notifyDataSetChanged();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                        // listview 갱신.



                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }
    }



