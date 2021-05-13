package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.HashMap;


public class joinActivity extends AppCompatActivity {


    Button btn_save;
    Button btn_check;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
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
        initDatabase();
        mReference = mDatabase.getReference("user");
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = join_id.getText().toString();
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String db_id = messageData.child("userId").getValue().toString().trim();
                            Log.d("input_id: ", id + ", " +
                                    "db_id: " +db_id );
                            if (db_id.equals(id)) {
                                overlapId();
                                break;
                            } else if(id != ""){
                                useId();
                                check++;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = join_id.getText().toString().trim();
                String pw = join_pw.getText().toString().trim();
                String name = join_name.getText().toString().trim();
                String weight = join_weight.getText().toString().trim();


                if(id.isEmpty() || pw.isEmpty() || name.isEmpty() || weight.isEmpty() || check == 0) {

                    notEnoughInfo();
                }
                else if(check>0){
                    writeNewUser(id, name, pw, weight);
                }

            }
        });
        check = 0;
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

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userId, String userName, String password, String weight) {
            this.userId = userId;
            this.userName = userName;
            this.password = password;
            this.weight = weight;
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

        @Override
        public String toString() {
            return "User{" +
                    "id='" + userId + '\'' +
                    "name='" + userName + '\'' +
                    ", pw='" + password + '\'' +
                    ", kg='" + weight + '\'' +
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
    private boolean checkId(String db_id, String input_id){
        if(db_id.equals(input_id)){
            return true;
        } else{
          return false;
        }

    }
    private void overlapId() {
        Toast.makeText(this, "같은 ID가 존재합니다.", Toast.LENGTH_SHORT).show();
    }
    private void notEnoughInfo() {
        Toast.makeText(this, "입력 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
    }
    private void checkId() {
        Toast.makeText(this, "ID를 입력해 주세요.", Toast.LENGTH_SHORT).show();
    }
    private void useId() {
        Toast.makeText(this, "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
    }
}


