package com.example.hp2.sirus;

/**
 * Created by mrdis on 12/17/2017.
 */

public class Message {

    String text;
    int owner;

    public Message(String text, int owner) {
        this.text = text;
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
}
