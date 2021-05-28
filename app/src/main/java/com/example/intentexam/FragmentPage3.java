package com.example.intentexam;

import android.content.Intent;
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

import java.util.ArrayList;

public class FragmentPage3 extends Fragment {

    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    Button board_write_button;
    TextView textView3;
    ListView board_list;
    final ArrayList<String> boardTitle = new ArrayList<>();
    final ArrayList<String> subContent = new ArrayList<>();
    final ArrayList<String> boardContent = new ArrayList<>();
    final ArrayList<ItemData> oData = new ArrayList<>();

    @Override
    public void onPause() {//리스트 초기화
        super.onPause();
        boardTitle.clear();
        subContent.clear();
        oData.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_page_3, container, false);

        board_write_button = v.findViewById(R.id.board_write_button);
        board_write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), writeBoardActivity.class);
                startActivity(intent);
            }
        });
        new Runnable() {
            @Override
            public void run() {
                ListViewAdapter oAdapter = new ListViewAdapter(oData);
                oAdapter.notifyDataSetChanged();
            }
        };
        board_list = (ListView) v.findViewById(R.id.board_list);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("board"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String db_title = messageData.child("title").getValue().toString();
                    String db_writer = messageData.child("id").getValue().toString();
                    String db_date = messageData.child("date").getValue().toString();
                    String content = messageData.child("content").getValue().toString();
                    String subCon = db_writer + "        " + db_date;
                    if (boardTitle.contains(db_title) && subContent.contains(subCon)) {
                    } else {
                        boardTitle.add(db_title);
                        subContent.add(subCon);
                        boardContent.add(content);
                    }
                    // 리스트뷰 참조 및 Adapter달기
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
                String data = oData.get(position).toString();

                intent.putExtra("title", boardTitle.get(position)) ;
                intent.putExtra("content", boardContent.get(position)) ;

                Log.d("위치를 찾아봅시다 : ", boardTitle.get(position) + "," + boardContent.get(position));
                Log.d("위치를 찾아봅시다2 : ", String.valueOf(position));

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