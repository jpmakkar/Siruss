package com.example.hp2.sirus;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by hp 2 on 26-03-2018.
 */

public class Sirusdatabase extends ContentProvider {

    dbhelper dbhelper;
    SQLiteDatabase database;
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int i=database.delete(uri.getLastPathSegment(),selection,selectionArgs);
        return i;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long i=database.insert(uri.getLastPathSegment(),null,values);

        Uri u=Uri.parse("/stark/"+i);
        return u;
    }

    @Override
    public boolean onCreate() {
        dbhelper=new dbhelper(getContext(),Util.db,null,Util.vers);
        database=dbhelper.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c=database.query(uri.getLastPathSegment(),projection,null,null,null,null,null);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    class dbhelper extends SQLiteOpenHelper {

        public dbhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(Util.query);
            sqLiteDatabase.execSQL(Util.query1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
