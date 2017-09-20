package com.wael.alameen.worldonlineshopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RequestItemsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "req.db";
    private static final String TABLE_NAME = "req_table";

    RequestItemsDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price TEXT, logo TEXT, color TEXT, size TEXT, num TEXT, sec TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    boolean insert(String name, String price, String logo, String color, String size, String num, String sec) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("price", price);
        contentValues.put("logo", logo);
        contentValues.put("color", color);
        contentValues.put("size", size);
        contentValues.put("num", num);
        contentValues.put("sec", sec);
        long res = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return res != -1;
    }

    Cursor showAll() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT name, price, logo, color, size, num, sec FROM "+TABLE_NAME;
        return sqLiteDatabase.rawQuery(query, null);
    }

    public void delete() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "DELETE FROM "+TABLE_NAME;
        sqLiteDatabase.execSQL(query);
    }
}
