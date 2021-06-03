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

public class trailActivity extends AppCompatActivity {//산책로 클래스
    ListView listview;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    final ArrayList<String> trailInfo = new ArrayList<>();
    final ArrayList<String> trailName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("trail");

        mReference.addValueEventListener(new ValueEventListener() {//산책로 정보 불러오기
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messageData : snapshot.getChildren()) {
                    String key = messageData.child("name").getValue(String.class);
                    trailName.add(key);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, trailName);
                listview = (ListView) findViewById(R.id.trailList);
                listview.setAdapter(adapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String data = (String) adapterView.getItemAtPosition(position);
                        Intent intent = new Intent();

                        mReference = mDatabase.getReference("trail");
                        mReference.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot messageData : snapshot.getChildren()) {
                                    //산책로 정보 전달을 위한 해쉬맵
                                    HashMap<String, HashMap<String, Object>> userInfo = (HashMap<String, HashMap<String, Object>>) messageData.getValue();
                                    for (String u : userInfo.keySet()) {

                                        trailInfo.add(String.valueOf(userInfo.get(u)));

                                    }
                                    if (trailInfo.contains(data)) {
                                        String key = messageData.child("name").getValue(String.class);//산책로 정보 저장
                                        intent.putExtra("name", String.valueOf(key));
                                        intent.putExtra("start", String.valueOf(messageData.child("Start").getValue()));
                                        intent.putExtra("end", String.valueOf(messageData.child("End").getValue()));
                                        setResult(RESULT_OK, intent);
                                        finish();
                                        break;
                                    } else {
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
