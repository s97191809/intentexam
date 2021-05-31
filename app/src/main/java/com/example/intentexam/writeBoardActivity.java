package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skt.Tmap.TMapAddressInfo;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class writeBoardActivity extends AppCompatActivity {
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    EditText a_title;
    EditText board_content;
    Button board_w_Button;
    Button board_cancelButton;
    Button add_pic_Button;
    TextView b_address;
    SharedPreferences sf;
    Uri photoURI;
    final ArrayList<Integer> boardNum = new ArrayList<>();
    private int maxNum;
    private TMapView tmap;
    private final String TMAP_API_KEY = "l7xx5450926a109d4b33a7f3f0b5c89a2f0c";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_board);

        mDatabase = FirebaseDatabase.getInstance();

        a_title = (EditText) findViewById(R.id.a_title);
        board_content = (EditText) findViewById(R.id.board_content);



        tmap = new TMapView(this);
        tmap.setSKTMapApiKey(TMAP_API_KEY);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        sf = getSharedPreferences("info", MODE_PRIVATE);
        String id = sf.getString("inputId", "");

        Date time = new Date();

        String time1 = format1.format(time);

        mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String key = messageData.child("num").getValue(String.class);
                    boardNum.add(Integer.parseInt(key));

                    maxNum=Collections.max(boardNum);
                    Log.d("번호확인 : ", key);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        board_w_Button = findViewById(R.id.board_w_Button);
        board_w_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location lc = new location(writeBoardActivity.this);
                String lat = String.valueOf(lc.getLatitude());
                String lon = String.valueOf(lc.getLongitude());
                String title = a_title.getText().toString().trim();
                String content = board_content.getText().toString().trim();
                String address = b_address.getText().toString();
                writeBoard(content, id, String.valueOf(time1), title, String.valueOf(maxNum+1),lat,lon,address);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                String filename = title+ content + address + ".jpg";
                Uri file = photoURI;
                StorageReference riversRef = storageRef.child("board_img/"+filename);

                UploadTask uploadTask = riversRef.putFile(file);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                } );
                }

            });


        b_address = findViewById(R.id.b_address);
        board_cancelButton = findViewById(R.id.board_add_Button);
        board_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location lc = new location(writeBoardActivity.this);
                Double lat = lc.getLatitude();
                Double lon = lc.getLongitude();
                TMapPoint tp = new TMapPoint(lat, lon);
                TMapData tMapData = new TMapData();
                tMapData.reverseGeocoding(tp.getLatitude(), tp.getLongitude(), "A03", new TMapData.reverseGeocodingListenerCallback() {
                    @Override
                    public void onReverseGeocoding(TMapAddressInfo addressInfo) {
                        //Log.e("선택한 위치의 주소는 " , addressInfo.strFullAddress);
                        b_address.setText(addressInfo.strFullAddress);
                    }
                });
            }
        });
        add_pic_Button = (Button)findViewById(R.id.add_pic_Button);
        add_pic_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                // 선택한 이미지에서 비트맵 생성

                photoURI = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);

                Log.d("이미지 이름 : ", String.valueOf(bitmap));

                // 이미지 표시
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeBoard(String content, String writer, String date, String title, String num,String lat, String lon,String address) {
        User user = new User(content, writer, date, title, num, lat, lon,address);



        mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
        mReference.child(content).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                toString();


                finish();
                // 리스트뷰 참조 및 Adapter달기


                // 데이터 1000개 생성--------------------------------.
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed


                    }
                });

    }

    public class User {
        public String content;
        public String id;
        public String date;
        public String title;
        public String num;
        public String gPoint;
        public String lat;
        public String lon;
        public String address;


        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String content, String writer, String date, String title, String num,String lat, String lon,String address) {
            this.content = content;
            this.id = writer;
            this.date = date;
            this.title = title;
            this.num = num;
            this.gPoint = gPoint;
            this.lat = lat;
            this.lon = lon;
            this.address = address;

        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }
        public String getgPoint() {
            return "0";
        }

        public void setgPoint(String num) {
            this.gPoint = "0";
        }
        public void setLat(String lat){
            this.lat = lat;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon){
            this.lon = lon;
        }
        public String getAddress() {
            return address;
        }

        public void setAddress(String address){
            this.address = address;
        }

        @Override
        public String toString() {
            return "User{" +
                    "content='" + content + '\'' +
                    "id='" + id + '\'' +
                    ", date='" + date + '\'' +
                    ", title='" + title + '\'' +
                    ", num='" + num + '\'' +
                    ", gPoint='" + gPoint + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lon='" + lon + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

}