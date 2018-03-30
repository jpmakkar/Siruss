
package com.example.hp2.sirus;

import android.net.Uri;



public class Util {

    public static final String db="Sirus";
    public static final int vers=1;

    public static final String tab="Remember";
    public static final String tab1="Requests";
    public static final String date="Date";
    public static final String thing="Thing";

    public static final String query="create table Remember(" +
            "Date text," +
            "Thing text" +
            ")";

    public static final String query1="create table Requests(" +
            "Message text," +
            "Owner int" +
            ")";

    public static final Uri u=Uri.parse("content://com.example.hp2.sirus.stark/"+tab);

    public static final Uri u1=Uri.parse("content://com.example.hp2.sirus.stark/"+tab1);
}
