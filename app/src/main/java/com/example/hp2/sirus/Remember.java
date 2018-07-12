package com.example.hp2.sirus;

/**
 * Created by hp 2 on 25-04-2018.
 */

public class Remember {
    String date,text;

    public Remember(String date, String text) {
        this.date = date;
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
