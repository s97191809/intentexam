package com.example.intentexam;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Administrator on 2017-08-07.
 */

public class CustomDialog extends AppCompatActivity {

    private OnDataSelectionListener selectionListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    EditText a_title;
    EditText a_content;
    SharedPreferences getloginInfo;
    SharedPreferences getDateInfo;
    // EditText join_name = (EditText) findViewById(R.id.join_name);
    //EditText join_weight = (EditText) findViewById(R.id.join_weight);
    private CalendarMonthView MonthView;
    private CalendarMonthAdapter MonthAdapter;
    private MonthItem m;
    private Context context;
    CalendarMonthAdapter monthViewAdapter;

    public CustomDialog(Context context) {
        this.context = context;
    }


    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.

        Dialog dlg = new Dialog(this.context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.alert_layout);

        // 커스텀 다이얼로그를 노출한다.

        dlg.show();

        getDateInfo = context.getSharedPreferences("dateInfo", context.MODE_PRIVATE);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        a_title = (EditText) dlg.findViewById(R.id.a_title);
        a_content = (EditText) dlg.findViewById(R.id.a_content);

        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        mDatabase = FirebaseDatabase.getInstance();
        getloginInfo = context.getSharedPreferences("info", context.MODE_PRIVATE);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCalendar = Calendar.getInstance();

                //db-----------

                mReference = mDatabase.getReference("calender");


                String title = a_title.getText().toString();
                String content = a_content.getText().toString();

                String id = getloginInfo.getString("inputId", "");

                String curYear = getDateInfo.getString("year", "");
                String curMonth = getDateInfo.getString("month", "");
                curMonth = String.valueOf(Integer.parseInt(curMonth)+1);
                String day = getDateInfo.getString("day", "");


                //------------------일정추가:제목,내용/캘린더에서 년,월,일/로그인 정보에서 아이디 db에 추가, 제목은 달력에 보여주기
                Toast.makeText(context, "일정 작성 성공", Toast.LENGTH_SHORT).show();
                writeMemo(title, content, id, curYear, curMonth, day);
                finish();

                //------------------일정추가

                // 커스텀 다이얼로그를 종료한다.
                // 종료 조건은 타이틀이나 내용이 빈칸이 아닌 경우에만 종료.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }

    private void writeMemo(String title, String content, String id, String curYear, String curMonth, String day) {
        Memo memo = new Memo(title, content, id, curYear, curMonth, day);

        mReference.child(title).setValue(memo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(context, "일정을 작성했습니다.", Toast.LENGTH_SHORT).show();

                        toString();
                        finish();//종료와 함께 넘어가짐
                    }
                });

    }

    public class Memo {
        public String title;
        public String content;
        public String id;
        public String curYear;
        public String curMonth;
        public String day;

        public Memo() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Memo(String title, String content, String id, String curYear, String curMonth, String day) {
            this.title = title;
            this.content = content;
            this.id = id;
            this.curYear = curYear;
            this.curMonth = curMonth;
            this.day = day;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }


        public String getId() {
            return id;
        }

        public void setId(String Id) {
            this.id = id;
        }

        public String getCurYear() {
            return curYear;
        }

        public void setCurYear(String curYear) {
            this.curYear = curYear;
        }

        public String getCurMonth() {
            return curMonth;
        }

        public void setCurMonth(String curMonth) {
            this.curMonth = curMonth;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        @Override
        public String toString() {
            return "calendar{" +
                    "title='" + title + '\'' +
                    "content='" + content + '\'' +
                    ", id='" + id + '\'' +
                    ", curYear='" + curYear + '\'' +
                    ", curMonth='" + curMonth + '\'' +
                    ", day='" + day + '\'' +
                    '}';
        }
    }

    private void notEnoughInfo() {
        Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
    }


}
