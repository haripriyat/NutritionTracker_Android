package com.example.amyli.nutritiontracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by amyli on 6/1/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Event";
    private static java.lang.String DATABASE_CREATE = "CREATE TABLE Event (id TEXT PRIMARY KEY, nutrientName TEXT, datetime TEXT, consumedAmount TEXT);";
    public static String TABLE_Event = "event";
    public static String COLUMN_ID = "id";
    public static String COLUMN_nutrientName = "nutrientName";
    public static String COLUMN_dateTime = "datetime";
    public static String COLUMN_consumedAmount = "consumedAmount";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Event);
    }

}
