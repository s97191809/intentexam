package com.example.intentexam;

public class ListViewItem {//리스트뷰

    private String titleStr ;
    private String descStr ;


    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
}