package com.example.smartwaterbottle;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String ALARMS_TABLE = "ALARMS_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MEDICINE_NAME = "MEDICINE_NAME";
    public static final String COLUMN_HOUR = "HOUR";
    public static final String COLUMN_MINUTE = "MINUTE";
    public static final String COLUMN_IS_REPEATING = "IS_REPEATING";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "alarm_db", null, 1);
    }

    /**
     * This method is called the first time a database is accessed.
     * There should be code in here to create a new database.
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + ALARMS_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MEDICINE_NAME + " TEXT, " + COLUMN_HOUR + " INT, " + COLUMN_MINUTE + " INT, " + COLUMN_IS_REPEATING + " BOOL)";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    /**
     * This method is called if the database version number changes.
     * It prevents previous users apps from breaking when you change the database design.
     *
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(AlarmModel alarmModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        // content value = associative array, put data in pairs (similar to hashmap)
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MEDICINE_NAME, alarmModel.getMedicine_Name());
        cv.put(COLUMN_HOUR, alarmModel.getHour());
        cv.put(COLUMN_MINUTE, alarmModel.getMinutes());
        cv.put(COLUMN_IS_REPEATING, alarmModel.isRepeating());

        long insert = db.insert(ALARMS_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }
}
