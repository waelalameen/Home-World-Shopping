package com.wael.alameen.worldonlineshopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginInfo extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "info.db";
    private static final String TABLE_NAME = "info_table";

    LoginInfo(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, place TEXT, phone TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    boolean insert(String name, String place, String phone) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("place", place);
        contentValues.put("phone", phone);
        long res = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return res != -1;
    }

    Cursor showALL() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT name, place, phone FROM "+TABLE_NAME;
        return sqLiteDatabase.rawQuery(query, null);
    }
}
