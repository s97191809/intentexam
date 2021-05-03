package com.example.intentexam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentPage1 extends Fragment {
    DatePicker datePicker;
    TextView viewDatePick;
    EditText edtDiary;
    Button btnSave;

    String fileName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_1, container, false);
        //-----------캘린더-------------------
        datePicker = (DatePicker) v.findViewById(R.id.datePicker);
        viewDatePick = (TextView) v.findViewById(R.id.viewDatePick);
        edtDiary = (EditText) v.findViewById(R.id.edtDiary);
        btnSave = (Button) v.findViewById(R.id.btnSave);



        //날짜 및 시간 형식 지정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Date 객체 사용
        Date date = new Date();
        String time1 = simpleDateFormat.format(date);
        viewDatePick.setText(time1);

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 이미 선택한 날짜에 일기가 있는지 없는지 체크해야할 시간이다

            }
        });


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



}



