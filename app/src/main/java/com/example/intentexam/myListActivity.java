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

public class myListActivity extends AppCompatActivity {
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
        // 작성한 글 보여주기
        // 리뷰 버튼 누르면 리스트 나오고 길게 터치하면 삭제?
        // 버튼 누르면 디비에서 값을 슥 가지고옴 조건은 id 를 포함하면 걸러짐
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

        rvListButton = findViewById(R.id.rv_list);
        rvListButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                boardContent.clear();
                boardSubContent.clear();
                boardTitle.clear();
                oData.clear();
                mReference = mDatabase.getReference("hospitalreview"); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    //여기다가 리스트 초기화
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
                                            String subCon = db_id + "        " + db_date + "         " + dbHosList;
                                            // 한 병원에 계정 하나가 하나의 리뷰만 등록가능
                                            if (reviewContent.contains(db_content) && reviewSubContent.contains(subCon)) {

                                                reviewContent.set(reviewContent.indexOf(db_content),db_content);
                                                reviewSubContent.set(reviewSubContent.indexOf(subCon),subCon);
                                                // 같은 내용도 생각하기
                                                // 같은날 같은제목 일수도
                                                // 리뷰 번호가 필요한가 필요하겠다//
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

        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(myListActivity.this);

                builder.setTitle("해당 글을 삭제 하시겠습니까?").setMessage("삭제 후 버튼을 터치해 갱신해 주세요");


                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ListViewAdapter oAdapter = new ListViewAdapter(oData);
                        oAdapter.notifyDataSetChanged();
                        String data = oData.get(position).toString();

                        if (!reviewContent.isEmpty()) {


                            // 게시판 경우도 추가
//                            Log.d("위치를 찾아봅시다 : ", String.valueOf(reviewContent.get(position)) + "," + hosList.get(position));
 //                           Log.d("위치를 찾아봅시다2 : ", String.valueOf(position));
                            // 아이템 삭제
                            oData.remove(position);
                            //위치가 같으니 해당 위치에 있는 놈을 찾아서 해당하는 리뷰를 삭제하면 되겠습니다.

                            mReference = mDatabase.getReference().child("hospitalreview").child(hosList.get(position)); // 지워야할 내용에 해당되는 부분 지우기
                            mReference.child(reviewContent.get(position)).setValue(null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                         /*                   reviewContent.clear();
                                            reviewSubContent.clear();
*/
                                            reviewContent.remove(reviewContent.indexOf(reviewContent.get(position)));
                                            reviewSubContent.remove(reviewSubContent.indexOf(reviewSubContent.get(position)));
                                            oAdapter.notifyDataSetChanged();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        } else if(!boardContent.isEmpty()){
                            oData.remove(position);
                            //위치가 같으니 해당 위치에 있는 놈을 찾아서 해당하는 리뷰를 삭제하면 되겠습니다.

                            mReference = mDatabase.getReference().child("board"); // 지워야할 내용에 해당되는 부분 지우기
                            mReference.child(boardContent.get(position)).setValue(null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                             /*               boardContent.clear();
                                            boardTitle.clear();
                                            boardSubContent.clear();
                                            oData.clear();*/
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
                        // listview 갱신.


                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        bdListButton = findViewById(R.id.bd_list);
        bdListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              /*  reviewContent.clear();*/
                reviewContent.clear();
                reviewSubContent.clear();
                oData.clear();
                mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                            String dbKey = messageData.getKey();//병원이름받고
                            //Log.d("이계정이 가진 글목록 를! 불러올 병원들", db_content);
                            mReference = mDatabase.getReference("board"); // 병원이름들 하나하나 넣음
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

                                                boardTitle.set(boardTitle.indexOf(db_title),db_title);
                                                boardContent.set(boardContent.indexOf(db_content),db_content);
                                                boardSubContent.add(/*boardSubContent.indexOf(subCon),*/subCon);

                                                // 같은 내용도 생각하기
                                                // 같은날 같은제목 일수도
                                                // 리뷰 번호가 필요한가 필요하겠다//
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
                        if(!boardContent.isEmpty()) {
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