package com.example.intentexam;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BalloonOverlayView extends FrameLayout {//TMapApi 사용자 정의 풍선뷰를 표시하기 위한 클래스 입니다.

    private LinearLayout layout;
    private TextView title;
    private TextView subTitle;

    public BalloonOverlayView(Context context, String labelName, String id) {

        super(context);

        setPadding(10, 0, 10, 0);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        setupView(context, layout, labelName, id);

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;
        addView(layout, params);

    }


    protected void setupView(Context context, final ViewGroup parent, String labelName, String id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View view = inflater.inflate(R.layout.balloon_overlay, parent, true);

        title = (TextView) view.findViewById(R.id.all_path);
        subTitle = (TextView) view.findViewById(R.id.section_path);
        setTitle(labelName);
        setSubTitle(id);

    }

    public void setTitle(String str) {
        title.setText(str);
    }

    public void setSubTitle(String str) {
        subTitle.setText(str);

    }
}

