package com.example.intentexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapPoint;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class trailActivity extends AppCompatActivity {
    ListView listview;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private Gson gson;
    final ArrayList<String> trailInfo = new ArrayList<>();
    final ArrayList<String> trailName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail);
        mDatabase = FirebaseDatabase.getInstance();
        // 이름을 가져온 다음에 이름들을 넣어서 이름들을 가지고 있는 값들을 다 가져오기
        mReference = mDatabase.getReference("trail");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messageData : snapshot.getChildren()) {
                    String key = messageData.child("name").getValue(String.class);
                //    Log.d("키 값 : ", key);
                    trailName.add(key);
                 //   HashMap<String, HashMap<String, Object>> userInfo = (HashMap<String, HashMap<String, Object>>) messageData.getValue();
                  //  for (String u : userInfo.keySet()) {
                   //     Log.d(":시발제발부ㅜ : ", String.valueOf(userInfo.get(u)));
                   // }

                    //키값 리스트로 리스트 뷰를 만든다음에 해당 산책로를 선택하면 어레이 리스트에서 이름값을 지우고 해시맵을
                    //frag2로 넘겨줘서 거기서 산책로를 그려준다.
                    //키값을 가져와서 밸류들을 찾자
                   // int count = (int) messageData.getChildrenCount();
                    //Log.i("갖고있는 길이  : ", String.valueOf(count));
                    //길이를ㄹ 알면 이름으로 함더 하면 되잖아

                }//리스트를 터치하면 그 리스트 뷰의 정보를 지도화면에 넘겨줘서 보여줘야지
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, trailName);
                listview = (ListView) findViewById(R.id.trailList);
                listview.setAdapter(adapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String data = (String) adapterView.getItemAtPosition(position);
                        Intent intent = new Intent() ;

                        //    Log.d("선택한 값", data);
                        mReference = mDatabase.getReference("trail");
                        mReference.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot messageData : snapshot.getChildren()) {

                                    Log.d(":시발제발부ㅜ : ", String.valueOf(messageData.child("Start").getValue()));

                                    HashMap<String, HashMap<String, Object>> userInfo = (HashMap<String, HashMap<String, Object>>) messageData.getValue();
                                    for (String u : userInfo.keySet()) {

                                        //    Log.d(":시발제발부ㅜ : ", String.valueOf(userInfo.get(u)));
                                        trailInfo.add(String.valueOf(userInfo.get(u)));

                                    }
                                    if(trailInfo.contains(data)){
                                        //SharedPreferences sp= getSharedPreferences("trailInfo", MODE_PRIVATE);
                                        //SharedPreferences.Editor mEdit1= sp.edit();
                                        String key = messageData.child("name").getValue(String.class);
                                        intent.putExtra("name", String.valueOf(key)) ;
                                        intent.putExtra("start", String.valueOf(messageData.child("Start").getValue())) ;

                                        intent.putExtra("end", String.valueOf(messageData.child("End").getValue())) ;

                                        //mEdit1.putInt("Status_size",trailInfo.size()); /*sKey is an array*/
                                       /* for(int i=0;i<trailInfo.size();i++)
                                        {
                                            mEdit1.remove("Status_" +trailInfo.get(i) );
                                            mEdit1.putString("Status_" + i, trailInfo.get(i));
                                        }*/

                                        //mEdit1.commit();
                             //           Log.d("일치하는 값", data);
                                        setResult(RESULT_OK, intent) ;
                                        finish();
                                        break;
                                    }else{
                                        trailInfo.clear();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }


}
