package com.example.intentexam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapAddressInfo;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import static android.app.Activity.RESULT_OK;

public class FragmentPage3 extends Fragment { // 게시글 클래스

    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    Button board_write_button;
    TextView textView3;

    ListView board_list;
    final ArrayList<String> boardTitle = new ArrayList<>();
    final ArrayList<String> subContent = new ArrayList<>();
    final ArrayList<String> boardContent = new ArrayList<>();
    final ArrayList<String> filename = new ArrayList<>();
    final ArrayList<ItemData> oData = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("board"); // 게시글 정보 로드
        mReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boardTitle.clear();
                subContent.clear();
                oData.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_title = messageData.child("title").getValue().toString();
                    String db_writer = messageData.child("id").getValue().toString();
                    String db_date = messageData.child("date").getValue().toString();
                    String content = messageData.child("content").getValue().toString();
                    String gpoint = messageData.child("gPoint").getValue().toString();


                    String subCon = db_writer + "        " + db_date + "      " + "좋아요 수 : " + gpoint;
                    if (boardTitle.contains(db_title) && subContent.contains(subCon)) {
                    } else {
                        filename.add(db_title+db_writer);
                        boardTitle.add(db_title);
                        subContent.add(subCon);
                        boardContent.add(content);
                    }
                    // 리스트뷰 참조 및 Adapter
                }
                if (oData.size() != boardTitle.size()) {
                    for (int i = 0; i < boardTitle.size(); i++) {
                        ItemData oItem = new ItemData();
                        oItem.strTitle = boardTitle.get(i);
                        oItem.strDate = subContent.get(i);
                        Log.d("작성자 확인:", subContent.get(i));
                        oData.add(oItem);
                    }
                }
                // ListView, Adapter 생성 및 연결 ------------------------

                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                board_list.setAdapter(oAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_3, container, false);

        board_write_button = v.findViewById(R.id.board_write_button);//게시글 작성 버튼
        board_write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), writeBoardActivity.class);
                startActivity(intent);
            }
        });
        new Runnable() {//리스트 갱신 쓰레드
            @Override
            public void run() {
                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                oAdapter.notifyDataSetChanged();
            }
        };
        board_list = (ListView) v.findViewById(R.id.board_list);
        
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("board"); // 게시글 정보 로드
        mReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boardTitle.clear();
                subContent.clear();
                boardContent.clear();
                oData.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_title = messageData.child("title").getValue().toString();
                    String db_writer = messageData.child("id").getValue().toString();
                    String db_date = messageData.child("date").getValue().toString();
                    String content = messageData.child("content").getValue().toString();
                    String gpoint = messageData.child("gPoint").getValue().toString();


                    String subCon = db_writer + "        " + db_date + "      " + "좋아요 수 : " + gpoint;
                    if (boardTitle.contains(db_title) && subContent.contains(subCon)) {
                    } else {
                        filename.add(db_title+db_writer);
                        boardTitle.add(db_title);
                        subContent.add(subCon);
                        boardContent.add(content);
                    }
                    // 리스트뷰 참조 및 Adapter
                }
                if (oData.size() != boardTitle.size()) {
                    for (int i = 0; i < boardTitle.size(); i++) {
                        ItemData oItem = new ItemData();
                        oItem.strTitle = boardTitle.get(i);
                        oItem.strDate = subContent.get(i);
                        Log.d("작성자 확인:", subContent.get(i));
                        oData.add(oItem);
                    }
                }
                // ListView, Adapter 생성 및 연결 ------------------------

                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                board_list.setAdapter(oAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        board_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), showBoardDetail.class);
                ListViewAdapter oAdapter = new ListViewAdapter(oData);




                intent.putExtra("title", boardTitle.get(position));
                intent.putExtra("content", boardContent.get(position));
                intent.putExtra("filename", filename.get(position));
                intent.putExtra("content_last", boardContent.get(boardContent.size()-1));

                startActivity(intent);

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}

