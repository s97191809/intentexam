package com.example.intentexam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MonthItemView extends androidx.appcompat.widget.AppCompatTextView {
    Calendar mCalendar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private MonthItem item;
    private Context context;
    SharedPreferences getloginInfo;

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
       //     mCalendar = Calendar.getInstance();
      //      getloginInfo = context.getSharedPreferences("info", context.MODE_PRIVATE);
       //     String id=getloginInfo.getString("inputId", "");
       //     String curYear = ""+mCalendar.get(Calendar.YEAR);
       //     String curMonth =  ""+mCalendar.get(Calendar.MONTH);

            //-------------caldb
      //      mReference = mDatabase.getReference("calender");
      //      String db_id = mReference.child("id").getValue().toString();
      //      String db_curYear = mReference.child("curYea").getValue().toString().trim();
       //     String db_curMonth = mReference.child("curMonth").getValue().toString().trim();
      //      String db_day = mReference.child("day").getValue().toString().trim();
      //      String db_title = mReference.child("title").getValue().toString().trim();

      //      if (db_id.equals(id)&& db_curYear.equals(curYear)&& db_curMonth.equals(curMonth) && db_day.equals(day)) {
    //            setText(db_title);
  //          }
        } else {
            setText("");
        }

    }


}

