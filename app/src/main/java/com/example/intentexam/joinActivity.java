package com.example.intentexam;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class joinActivity extends AppCompatActivity {


    Button btn_save;
    Button check_button;
    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//안뒈??
        setContentView(R.layout.activity_join);
        EditText join_id = (EditText) findViewById(R.id.join_id);
        EditText join_pw = (EditText) findViewById(R.id.join_password);
        EditText join_name = (EditText) findViewById(R.id.join_name);
        EditText join_weight = (EditText) findViewById(R.id.join_weight);
        btn_save = findViewById(R.id.join_button);
        check_button = findViewById(R.id.check_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        check_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String id = join_id.getText().toString();

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = join_id.getText().toString();
                String pw = join_pw.getText().toString();
                String name = join_name.getText().toString();
                String weight = join_weight.getText().toString();

                writeNewUser(id, name, pw, weight);

            }
        });
    }

    private void writeNewUser(String userId, String name, String password, String weight) {
        User user = new User(userId, name, password, weight);

        mDatabase.child("user").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(joinActivity.this, "회원가입을 완료했습니다.", Toast.LENGTH_SHORT).show();
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
    private void readUser(String userId, String name, String password, String weight){
        User user = new User(userId, name, password, weight);
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {// 동일한 값이 바뀌면 갱신이 되어버림 이건 가입시에 데이터있는지 확인
                // Get Post object and use the values to update the UI
                if(dataSnapshot.getValue(User.class) != null){

                    User post = dataSnapshot.getValue(User.class);
                    Log.w("FireBaseData", "getData" + post.toString());
                } else {
                    Toast.makeText(joinActivity.this, "값이존재???????", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}


