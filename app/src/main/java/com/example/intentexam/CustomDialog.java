package com.example.intentexam;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;

import java.util.Calendar;

/**
 * Created by Administrator on 2017-08-07.
 */

public class CustomDialog extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;



    EditText a_title = (EditText) findViewById(R.id.a_title);
    EditText a_content = (EditText) findViewById(R.id.a_content);

   // EditText join_name = (EditText) findViewById(R.id.join_name);
    //EditText join_weight = (EditText) findViewById(R.id.join_weight);
   CalendarMonthAdapter monthViewAdapter;


    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog();

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.alert_layout);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText a_title = (EditText) dlg.findViewById(R.id.a_title);
        final EditText a_content = (EditText) dlg.findViewById(R.id.a_content);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //db-----------
                Calendar mCalendar = Calendar.getInstance();
                mDatabase = FirebaseDatabase.getInstance();
                mReference = mDatabase.getReference("calender");
              //  curYear = mCalendar.get(Calendar.YEAR);
                //  curMonth = mCalendar.get(Calendar.MONTH);

                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
              //  main_label.setText(title.getText().toString());

                //------------------일정추가:제목,내용/캘린더에서 년,월,일/로그인 정보에서 아이디 db에 추가, 제목은 달력에 보여주기

               // input_title = (EditText) findViewById(R.id.a_title);
               // input_content = (EditText) findViewById(R.id.a_content);
//                mReference.child("title").setValue(a_title.getText().toString());

                monthView = (CalendarMonthView) v.findViewById(R.id.monthView);
                monthViewAdapter = new CalendarMonthAdapter(getContext());
                monthView.setAdapter(monthViewAdapter);

                MonthItem curItem = (MonthItem) monthViewAdapter.getItem(position);

                String title = a_title.getText().toString().trim();
                String content = a_content.getText().toString().trim();

                String id = a_title.getText().toString().trim();//수정해야됨
                String curYear =  mCalendar.get(Calendar.YEAR).toString().trim();
                String curMonth =  mCalendar.get(Calendar.MONTH);
                String day =  curItem.getDay().toString().trim();




                if(title.isEmpty() || content.isEmpty()) {

                    notEnoughInfo();
                }
                else {
                    writeMemo(title, content, id, curYear, curMonth, day);
                }

                //------------------일정추가
                // 커스텀 다이얼로그를 종료한다.
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

        mReference.child(title).setValue(title)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(CustomDialog.this, "일정을 작성했습니다.", Toast.LENGTH_SHORT).show();
                        // 로그인 화면으로 이동
                        toString();
                        finish();//종료와 함께 넘어가짐
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(CustomDialog.this, "일정을 작성을 실패했습니다.", Toast.LENGTH_SHORT).show();
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
