package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class joinActivity extends AppCompatActivity {


    Button btn_save;
    Button btn_check;
    Button btn_cancel;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    final ArrayList<String> arrId = new ArrayList<>();
    int check = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        EditText join_id = (EditText) findViewById(R.id.join_id);
        EditText join_pw = (EditText) findViewById(R.id.join_password);
        EditText join_name = (EditText) findViewById(R.id.join_name);
        EditText join_weight = (EditText) findViewById(R.id.join_weight);

        btn_save = findViewById(R.id.join_button);
        btn_check = findViewById(R.id.check_button);
        btn_cancel = findViewById(R.id.cancel);
        initDatabase();
        mReference = mDatabase.getReference("user");
        if (arrId.isEmpty()) {// 회원 정보 가져오기
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                        for(DataSnapshot messageData: dataSnapshot.getChildren()){
                            String db_id = messageData.child("userId").getValue().toString();

                            arrId.add(db_id);
                        }
                        for(String ch_id : arrId){
                            Log.d("디비 아이디 값 : ", ch_id);

                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = join_id.getText().toString();
                if(arrId.contains(id)) {
                    Toast.makeText(joinActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(joinActivity.this, "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = join_id.getText().toString().trim();
                String pw = join_pw.getText().toString().trim();
                String name = join_name.getText().toString().trim();
                String weight = join_weight.getText().toString().trim();


                if(arrId.contains(id)) {
                            Toast.makeText(joinActivity.this, "아이디를 확인해 주세요!.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(joinActivity.this, "회원 가입 성공", Toast.LENGTH_SHORT).show();
                            writeNewUser(id, name, pw, weight);
                            finish();
                        }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void writeNewUser(String userId, String name, String password, String weight) {
        User user = new User(userId, name, password, weight);
        // 중복 체크를 했을 때 트루면 가입 안되게 하고 펄스면 되게
        mReference.child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(joinActivity.this, "회원가입을 완료했습니다.", Toast.LENGTH_SHORT).show();
                        // 로그인 화면으로 이동
                        toString();
                        finish();//종료와 함께 넘어가짐

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed

                        Toast.makeText(joinActivity.this, "회원가입을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public class User {
        public String userId;
        public String userName;
        public String password;
        public String weight;
        public String coin;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userId, String userName, String password, String weight) {
            this.userId = userId;
            this.userName = userName;
            this.password = password;
            this.weight = weight;
            this.coin = coin;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeightKg(String weight) {
            this.weight = weight;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + userId + '\'' +
                    "name='" + userName + '\'' +
                    ", pw='" + password + '\'' +
                    ", kg='" + weight + '\'' +
                    ", coin='" + coin + '\'' +
                    '}';
        }
    }

    private void readUser(String userId, String name, String password, String weight) {
        User user = new User(userId, name, password, weight);
        mReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue(User.class) != null) {

                    User post = dataSnapshot.getValue(User.class);
                    Log.w("FireBaseData", "getData" + post.toString());
                } else {
                    Toast.makeText(joinActivity.this, "값이존재", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReference.addChildEventListener(mChild);
    }
       
}



