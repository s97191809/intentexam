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

public class FragmentPage4 extends Fragment {
    //객체 선언
    Button logoutButton;
    Button withdrawalButton;
    Button my_listButton;
    TextView idView;
    TextView pwView;
    TextView nameView;
    TextView weightView;
    SharedPreferences sf;
    private TextView txt_preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_4, container, false);
        // 값 넘겨 받아오는 부분
        sf = getContext().getSharedPreferences("info", getContext().MODE_PRIVATE);
        String id = sf.getString("inputId", "");
        String pw = sf.getString("inputPwd", "");
        String name = sf.getString("name", "");
        String weight = sf.getString("weight", "");

        idView = v.findViewById(R.id.id);
        pwView = v.findViewById(R.id.pw);
        nameView = v.findViewById(R.id.name);
        weightView = v.findViewById(R.id.weight);

        idView.setText("아이디 : " + id);
        pwView.setText("비번 : " + pw);
        nameView.setText("이름 : " + name);
        weightView.setText("몸무게(kg) : " + weight);

        logoutButton = v.findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), loginActivity.class);
                startActivity(intent);

                SharedPreferences.Editor editor = sf.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();
                Toast.makeText(getActivity(), "로그아웃.", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(FragmentPage4.this).commit();
                fragmentManager.popBackStack();

            }
        });

        withdrawalButton = v.findViewById(R.id.withdrawal);
        withdrawalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), drawalActivity.class);
                startActivity(intent);
            }
        });

        my_listButton = v.findViewById(R.id.my_list_button);
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