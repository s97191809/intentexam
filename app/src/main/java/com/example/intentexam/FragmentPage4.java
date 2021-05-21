package com.example.intentexam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentPage4 extends Fragment {
    //객체 선언
    Button button4;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
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

        textView1 = v.findViewById(R.id.id);
        textView2 = v.findViewById(R.id.pw);
        textView3 = v.findViewById(R.id.name);
        textView4 = v.findViewById(R.id.weight);

        textView1.setText("아이디 : " + id);
        textView2.setText("비번 : " + pw);
        textView3.setText("이름 : " + name);
        textView4.setText("몸무게(kg) : " + weight);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}