package com.example.amyli.nutritiontracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by amyli on 6/1/2017.
 */

public class SQLiteHelperGoals extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Goals";
    private static String DATABASE_CREATE = "CREATE TABLE Goals (id TEXT PRIMARY KEY, nutrientName TEXT, goalAmount TEXT);";
    public static String TABLE_Event = "goal";
    public static String COLUMN_ID = "id";
    public static String COLUMN_nutrientName = "nutrientName";
    public static String COLUMN_goalAmount = "goalAmount";

    public SQLiteHelperGoals(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
