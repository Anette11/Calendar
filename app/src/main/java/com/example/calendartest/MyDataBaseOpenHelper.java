package com.example.calendartest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataBaseOpenHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "events";
    private static final int DATABASE_VERSION = 4;
    public static final String COLUMN_1 = "ID";
    public static final String COLUMN_2 = "eventTitle";
    public static final String COLUMN_3 = "eventDescription";
    public static final String COLUMN_4 = "eventDate";
    public static final String COLUMN_5 = "eventDateInt";
    private boolean isExists;

    public MyDataBaseOpenHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    public boolean isExists() {
        return isExists;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_2 + " TEXT,"
                + COLUMN_3 + " TEXT,"
                + COLUMN_4 + " TEXT,"
                + COLUMN_5 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addEvent(Event event) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, event.getEventTitle());
        contentValues.put(COLUMN_3, event.getEventDescription());
        contentValues.put(COLUMN_4, event.getEventDate());
        contentValues.put(COLUMN_5, event.getEventDateInt());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllEvents() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM events ORDER BY eventDateInt ASC";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public int getEventId(String eventTitle, String eventDescription, String eventDate, int eventDateInt) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT " + COLUMN_1 + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_2 + " = '" + eventTitle + "'"
                + " AND " + COLUMN_3 + " = '" + eventDescription + "'"
                + " AND " + COLUMN_4 + " = '" + eventDate + "'"
                + " AND " + COLUMN_5 + " = '" + eventDateInt + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int itemId = -1;

        while (cursor.moveToNext()) {
            itemId = cursor.getInt(0);
        }
        return itemId;
    }

    public void updateEvent
            (String newEventDate, String newEventTitle, String newEventDescription, int id, int newEventDateInt) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, newEventTitle);
        contentValues.put(COLUMN_3, newEventDescription);
        contentValues.put(COLUMN_4, newEventDate);
        contentValues.put(COLUMN_5, newEventDateInt);
        sqLiteDatabase.update(TABLE_NAME, contentValues,
                COLUMN_1 + "= '" + id + "'", null);
    }

    public void deleteEvent(String eventTitle, int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_1
                + " = '" + id + "' AND " + COLUMN_2 + " = '" + eventTitle + "'";
        sqLiteDatabase.execSQL(query);
    }

    public void deleteAddEvents() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + COLUMN_4 + " FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            isExists = true;
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
        }
    }
}
