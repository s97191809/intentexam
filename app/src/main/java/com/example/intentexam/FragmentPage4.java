package com.example.intentexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentPage4 extends Fragment {//내정보 페이지 클래스
    //객체 선언
    Button logoutButton;
    Button withdrawalButton;
    Button my_listButton;
    TextView idView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    TextView nameView;
    TextView weightView;
    TextView coinView;
    SharedPreferences sf;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_4, container, false);
        // 로그인 정보 수신
        sf = getContext().getSharedPreferences("info", getContext().MODE_PRIVATE);
        String id = sf.getString("inputId", "");

        String name = sf.getString("name", "");
        String weight = sf.getString("weight", "");


        idView = v.findViewById(R.id.id);
        nameView = v.findViewById(R.id.name);
        weightView = v.findViewById(R.id.weight);
        coinView = v.findViewById(R.id.coin);

        idView.setText("아이디 : " + id);
        nameView.setText("이름 : " + name);
        weightView.setText("몸무게(kg) : " + weight);


        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("user"); // 레벨업 시 내정보 화면에 반영
        mReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messageData : snapshot.getChildren()) {
                    String db_id = messageData.child("userId").getValue().toString();
                    if (db_id.equals(id)) {
                        coinView.setText("레벨 : " + messageData.child("coin").getValue().toString());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logoutButton = v.findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), loginActivity.class);
                startActivity(intent);

                SharedPreferences.Editor editor = sf.edit();
                editor.clear();
                editor.commit();

                Toast.makeText(getActivity(), "로그아웃.", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(FragmentPage4.this).commit();
                fragmentManager.popBackStack();//finish() 액티비티 종료

            }
        });

        withdrawalButton = v.findViewById(R.id.withdrawal);//회원 탈퇴 버튼 
        withdrawalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), drawalActivity.class);
                startActivity(intent);
            }
        });

        my_listButton = v.findViewById(R.id.my_list_button);//작성한 글 목록 버튼
        my_listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), myListActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}