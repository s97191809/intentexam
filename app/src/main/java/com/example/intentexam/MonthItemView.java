package com.example.intentexam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MonthItemView extends androidx.appcompat.widget.AppCompatTextView {
    Calendar mCalendar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private MonthItem item;
    private Context context;
    SharedPreferences getloginInfo;
    private String db_id;
    private String db_curYear;
    private String db_curMonth;
    private String db_day;
    private String db_title;

    public MonthItemView(Context context) {
        super(context);

        init();
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setBackgroundColor(Color.WHITE);
    }


    public MonthItem getItem() {
        return item;
    }

    public void setItem(MonthItem item) {
        this.item = item;

        int day = item.getDay();
        if (day != 0) {
            setText(String.valueOf(day));

            //-------------userdb
            mCalendar = Calendar.getInstance();
            getloginInfo = getContext().getSharedPreferences("info", getContext().MODE_PRIVATE);
            String id= getloginInfo.getString("inputId", "");
            String curYear = ""+mCalendar.get(Calendar.YEAR);
            String curMonth =  ""+mCalendar.get(Calendar.MONTH);

            //-------------caldb
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference("calender");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot messageData : snapshot.getChildren()) {
                        db_id = messageData.child("id").toString();
                        db_curYear = messageData.child("curYear").toString().trim();
                         db_curMonth = messageData.child("curMonth").toString().trim();
                        db_day = messageData.child("day").toString().trim();
                        db_title = messageData.child("title").toString().trim();
                        if (db_id.equals(id)&& db_curYear.equals(curYear)&& db_curMonth.equals(curMonth) && db_day.equals(day)) {
                            setText(db_title);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        } else {
            setText("");
        }

    }


}

