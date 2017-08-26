//package edu.cmu.test0530;
package com.example.amyli.nutritiontracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.example.amyli.nutritiontracker.SQLiteHelper.DATABASE_NAME;

/**
 * Created by sophiejeong on 5/29/17.
 */
public class NutritionIntakeDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_nutrientName,
            SQLiteHelper.COLUMN_dateTime,
            SQLiteHelper.COLUMN_consumedAmount };

    public NutritionIntakeDAO(Context context) {
        dbHelper = new SQLiteHelper (context, DATABASE_NAME, null,
                SQLiteHelper.DATABASE_VERSION);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }
    public void insertEvent(String name, String dateTime, String
            amount)
    {
        ContentValues newEvent = new ContentValues();
        newEvent.put(SQLiteHelper.COLUMN_nutrientName, name);
        newEvent.put(SQLiteHelper.COLUMN_dateTime, dateTime);
        newEvent.put(SQLiteHelper.COLUMN_consumedAmount, amount);
        open();
        database.insert(SQLiteHelper.TABLE_Event, null, newEvent);
        close();
    }
    public void updateEvent(long id, String name, String dateTime,
                            String amount)
    {
        ContentValues editEvent = new ContentValues();
        editEvent.put(SQLiteHelper.COLUMN_nutrientName, name);
        editEvent.put(SQLiteHelper.COLUMN_dateTime, dateTime);
        editEvent.put(SQLiteHelper.COLUMN_consumedAmount, amount);
        open();
        database.update(SQLiteHelper.TABLE_Event, editEvent,
                "_id=" + id, null);
        close();
    }
    public Cursor getAllEventNames()
    {
        database = dbHelper.getReadableDatabase();
        //database = dbHelper.getWritableDatabase();
        return database.query(SQLiteHelper.TABLE_Event, new
                String[] {"_id", "name"}, null, null, null, null, "_id");
    }
    public Cursor getAllEvents()
    {
        database = dbHelper.getReadableDatabase();
        //database = dbHelper.getWritableDatabase();
        return database.query(SQLiteHelper.TABLE_Event, new String[] {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_nutrientName, SQLiteHelper.COLUMN_dateTime,
                SQLiteHelper.COLUMN_consumedAmount}, null, null, null, null, "id");
    }
/*    public List<Event> getAllEventLists() {
        List<Event> events = new ArrayList<Event>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_Event,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event Event = cursorToEvent(cursor);
            events.add(Event);
            cursor.moveToNext();
        } // make sure to close the cursor
        cursor.close();
        return events;
    } */

/*    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setName(cursor.getString(1));
        return event;
    } */

    public Cursor getOneEvent(long id)
    {
        database = dbHelper.getReadableDatabase();
        return database.query(SQLiteHelper.TABLE_Event, null,
                "_id=" + id, null, null, null, null);
    }
    public void deleteEvent(String id)
    {
        open();
        database.delete(SQLiteHelper.TABLE_Event, SQLiteHelper.COLUMN_dateTime + "=" + id,
                null);
        close();
    }
    public void clearTable(){
        open();
        database.delete(SQLiteHelper.TABLE_Event, "1", null);
        close();
    }
}