package com.example.intentexam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class showBoardDetail extends AppCompatActivity {
    TextView a_title;
    TextView board_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_more);


    }
}