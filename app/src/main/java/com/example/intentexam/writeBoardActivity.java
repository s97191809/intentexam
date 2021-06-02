package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    private static final int REQUEST_IMAGE_1 = 1;
    private String imagePath1 = "";


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
                final String cu = title;

                //1. 사진을 storage에 저장하고 그 url을 알아내야함..

                String filename = cu + "_" + id;

                StorageReference storageRef = storage.getReferenceFromUrl("gs://narang-a1a26.appspot.com/").child("board_img/" + filename);


                UploadTask uploadTask;


                Uri file = null;
                file = photoURI;

                uploadTask = storageRef.putFile(file);


/*                StorageReference storageRef = storage.getReference();
                String filename = title+ content + address + ".jpg";
                Uri file = photoURI;
                StorageReference riversRef = storageRef.child("board_img/"+filename);

                UploadTask uploadTask = riversRef.putFile(file);*/
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
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Get Album"), REQUEST_IMAGE_1);


            }
        });
    }
    private String getRealPathFromUri(Uri uri)
    {
        String[] proj=  {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return  url;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                // 선택한 이미지에서 비트맵 생성

                photoURI = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);



                // 이미지 표시
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }



/*    public String getExternalPath(String forlderName){

        String sdPath ="";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + forlderName;
        }else{
            sdPath  = getFilesDir() +"/" + forlderName;

        }
        return sdPath;
    }

    public String setImageAndSaveImageReturnPath(View v, Intent data) {

        try {
            // URI 가져오기
            Uri selectedImageUri = data.getData();

            // 선택한 이미지에서 비트맵 생성
            InputStream in = getContentResolver().openInputStream(selectedImageUri);
            Bitmap img = BitmapFactory.decodeStream(in);
            in.close();

            String path = getString(R.string.app_name);
            String fileName = "/" + System.currentTimeMillis() + ".png";
            String externalPath = getExternalPath(path);

            String address = externalPath + fileName;

            //imagePath1 = address;
            //Toast.makeText(context, "imagePath1", Toast.LENGTH_SHORT).show();

            BufferedOutputStream out = null;

            File dirFile = new File(externalPath);

            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }

            File copyFile = new File(address);

            try {

                copyFile.createNewFile();

                out = new BufferedOutputStream(new FileOutputStream(copyFile));

                img.compress(Bitmap.CompressFormat.PNG, 100, out);

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(copyFile)));

                Log.d("여부 : ", "이미지저장됨");
                //Toast.makeText(getActivity(), captureMessage, Toast.LENGTH_LONG).show();
                // 저장되었다는 문구 생성

                out.close();
                // 이거때문인가


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("여부 : ", "에러");
            }
            return address;

        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }

    }*/



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