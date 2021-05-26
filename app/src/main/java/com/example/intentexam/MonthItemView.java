package com.example.intentexam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MonthItemView extends androidx.appcompat.widget.AppCompatTextView {
    Calendar mCalendar;
    TextView textview1;
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
    SharedPreferences sf;
    CalendarMonthAdapter monthViewAdapter;
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
        FragmentPage1 f1 = new FragmentPage1();
/*        monthViewAdapter = new CalendarMonthAdapter(getContext());
        String year = String.valueOf(monthViewAdapter.curMonth);

        sf = getContext().getSharedPreferences("dateInfo", getContext().MODE_PRIVATE);
        SharedPreferences.Editor getDate = sf.edit();
        String month = sf.getString("month", "");
        Log.d("월", month);
        getDate.clear();
        getDate.commit();


        textview1 = findViewById(R.id.monthText);*/
        if (day != 0) {
            setText(String.valueOf(day));

            //-------------userdb
            mCalendar = Calendar.getInstance();
            getloginInfo = getContext().getSharedPreferences("info", getContext().MODE_PRIVATE);
            String id= getloginInfo.getString("inputId", "");
            String curYear = String.valueOf(mCalendar.get(Calendar.YEAR)).trim();
            String curMonth = String.valueOf(f1.curMonth+1);
                    //String.valueOf(mCalendar.get(Calendar.MONTH)).trim();


            //-------------caldb
            mDatabase = FirebaseDatabase.getInstance();
            mReference = mDatabase.getReference("calender");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot messageData : snapshot.getChildren()) {
                        db_id = messageData.child("id").getValue().toString();
                        db_curYear = messageData.child("curYear").getValue().toString().trim();
                        db_curMonth = messageData.child("curMonth").getValue().toString().trim();
                        db_day = messageData.child("day").getValue().toString().trim();
                        db_title  = messageData.child("title").getValue().toString().trim();
                      //  Log.d("오긴하니 : ", db_title);
                    //    Log.d("오긴하니 curMonth: ", curMonth);
                        if (db_id.equals(id)&&db_curYear.equals(curYear)&& db_curMonth.equals(curMonth) && db_day.equals(String.valueOf(day))) {
                            setText(day + "\n" +db_title);

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

