package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skt.Tmap.TMapAddressInfo;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class showBoardDetail extends AppCompatActivity {//게시글 상세보기 클래스
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    TextView boardTitleView;
    TextView boardDetailView;
    ImageView imgview;
    TextView address;
    Button boardGpoint;
    String filename;
    String gp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_more);

        Intent secondIntent = getIntent();
        String title = secondIntent.getStringExtra("title");
        String content = secondIntent.getStringExtra("content");
        address = findViewById(R.id.b_title);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("board");//게시글에 맞는 이미지 이름 생성
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_title = messageData.child("title").getValue().toString().trim();
                    String db_content = messageData.child("content").getValue().toString().trim();
                    String db_id = messageData.child("id").getValue().toString().trim();
                    Log.d("값 확인", title + "," + content);
                    if (title.equals(db_title) && content.equals(db_content)) {
                        String db_address = messageData.child("address").getValue().toString();
                        address.setText(db_address);
                        filename = title + "_" + db_id;
                        Log.d("파일이름  ", filename);
                        imgview = findViewById(R.id.imgview);
                        FirebaseStorage storage = FirebaseStorage.getInstance();//생성된 이름으로 디비에서 이미지 불러오기
                        StorageReference storageRef = storage.getReference().child("board_img/");
                        storageRef.child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Toast.makeText(getApplicationContext(), "다운로드 성공 : "+ uri, Toast.LENGTH_SHORT).show();
                                Glide.with(getApplicationContext()).load(uri).into(imgview);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
                if(!filename.isEmpty()){
                    String fn =secondIntent.getStringExtra("filename");
                    imgview = findViewById(R.id.imgview);
                    FirebaseStorage storage = FirebaseStorage.getInstance();//생성된 이름으로 디비에서 이미지 불러오기
                    StorageReference storageRef = storage.getReference().child("board_img/");
                    storageRef.child(fn).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Toast.makeText(getApplicationContext(), "다운로드 성공 : "+ uri, Toast.LENGTH_SHORT).show();
                            Glide.with(getApplicationContext()).load(uri).into(imgview);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //게시글 제목,내용 설정
        boardTitleView = findViewById(R.id.a_title);
        boardTitleView.setText(title);

        boardDetailView = findViewById(R.id.board_content);
        boardDetailView.setText(content);

        boardGpoint = findViewById(R.id.board_good);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("board"); //좋아요 눌릴시 디비 갱신 및 출력
        mReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String db_title = messageData.child("title").getValue().toString().trim();
                    String db_content = messageData.child("content").getValue().toString().trim();

                    if (title.equals(db_title) && content.equals(db_content)) {
                        gp = messageData.child("gPoint").getValue().toString();
                        boardGpoint.setText("좋아요 " + gp);
                        Log.d("받은 좋아요 수 : ", gp);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        boardGpoint.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View v) {//좋아요 버튼

                mReference = mDatabase.getReference().child("board"); // 지워야할 내용에 해당되는 부분 지우기
                mReference.child(content).child("gPoint").setValue(String.valueOf(Integer.parseInt(gp) + 1))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                boardGpoint.setText("좋아요" + " " + gp);


                            }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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