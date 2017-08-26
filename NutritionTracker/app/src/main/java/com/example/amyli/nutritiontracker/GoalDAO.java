package com.example.amyli.nutritiontracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.example.amyli.nutritiontracker.SQLiteHelperGoals.DATABASE_NAME;

public class GoalDAO { //TODO
    private SQLiteDatabase database;
    private SQLiteHelperGoals dbHelper;

    private String[] allColumns = { SQLiteHelperGoals.COLUMN_ID,
                                    SQLiteHelperGoals.COLUMN_nutrientName,
                                    SQLiteHelperGoals.COLUMN_goalAmount};

    public GoalDAO(Context context) {
        dbHelper = new SQLiteHelperGoals (context, DATABASE_NAME, null, SQLiteHelperGoals.DATABASE_VERSION);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }
    public void insertEvent(String name, String amount)
    {
        ContentValues newEvent = new ContentValues();
        newEvent.put(SQLiteHelperGoals.COLUMN_nutrientName, name);
        newEvent.put(SQLiteHelperGoals.COLUMN_goalAmount, amount);
        open();
        database.insert(SQLiteHelperGoals.TABLE_Event, null, newEvent);
        close();
    }
    public void updateEvent(long id, String name, String amount)
    {
        ContentValues editEvent = new ContentValues();
        editEvent.put(SQLiteHelperGoals.COLUMN_nutrientName, name);
        editEvent.put(SQLiteHelperGoals.COLUMN_goalAmount, amount);
        open();
        database.update(SQLiteHelperGoals.TABLE_Event, editEvent,
                "_id=" + id, null);
        close();
    }
    public Cursor getAllEventNames()
    {
        database = dbHelper.getReadableDatabase();
        //database = dbHelper.getWritableDatabase();
        return database.query(SQLiteHelperGoals.TABLE_Event, new
                String[] {"_id", "name"}, null, null, null, null, "_id");
    }
    public Cursor getAllEvents()
    {
        database = dbHelper.getReadableDatabase();
        //database = dbHelper.getWritableDatabase();
        return database.query(SQLiteHelperGoals.TABLE_Event,
                allColumns,
                null, null, null, null, "id");
    }

    public Cursor getOneEvent(long id)
    {
        database = dbHelper.getReadableDatabase();
        return database.query(SQLiteHelperGoals.TABLE_Event, null,
                "_id=" + id, null, null, null, null);
    }
    public void deleteEvent(String id)
    {
        open();
        database.delete(SQLiteHelperGoals.TABLE_Event, SQLiteHelperGoals.COLUMN_nutrientName + "=" + id,
                null);
        close();
    }
    public void clearTable(){
        open();
        database.delete(SQLiteHelperGoals.TABLE_Event, "1", null);
        close();
    }
}