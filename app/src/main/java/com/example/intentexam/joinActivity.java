package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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


public class joinActivity extends AppCompatActivity {
    private EditText join_id;
    private EditText join_name;
    private EditText join_password;
    private EditText join_weight;
    private Button button;

    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        writeNewUser();
    }
    private void writeNewUser(String userId, String name, String password, String kg) {
        User user = new User(name, password, kg);

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

        public String userName;
        public String password;
        public String kg;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userName, String password, String kg) {
            this.userName = userName;
            this.password = password;
            this.password = kg;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return password;
        }

        public void setEmail(String email) {
            this.password = email;
        }
        public String getKg() {
            return kg;
        }

        public void Kg(String email) {
            this.kg = kg;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + userName + '\'' +
                    ", pw='" + password + '\'' +
                    ", kg='" + kg + '\'' +
                    '}';
        }
    }
}

