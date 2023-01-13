package com.example.smartwaterbottle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CpuUsageInfo;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String ALARMS_TABLE = "ALARMS_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MEDICINE_NAME = "MEDICINE_NAME";
    public static final String COLUMN_HOUR = "HOUR";
    public static final String COLUMN_MINUTE = "MINUTE";
    public static final String COLUMN_IS_REPEATING = "IS_REPEATING";
    public static final String COLUMN_PILL_BOX = "BOX";
    public static final String COLUMN_PILL_NUMBER = "PILL_NUMBER";

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
        String createTableStatement = "CREATE TABLE " + ALARMS_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MEDICINE_NAME + " TEXT, " + COLUMN_HOUR + " INT, " + COLUMN_MINUTE + " INT, " + COLUMN_IS_REPEATING + " BOOL, " + COLUMN_PILL_BOX + " TEXT, " + COLUMN_PILL_NUMBER + " TEXT)";

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
        cv.put(COLUMN_PILL_BOX, alarmModel.getPill_Container());
        cv.put(COLUMN_PILL_NUMBER, alarmModel.getpill_Number());

        long insert = db.insert(ALARMS_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<AlarmModel> getEveryone() {
        List<AlarmModel> returnList = new ArrayList<>();

        // get data from the database
        String queryString = "SELECT * FROM " + ALARMS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            /**
             * Here we loop through the cursor (result set) and create new alarm objects.
             * We put them into the return list.
              */
            do {
                int alarmID = cursor.getInt(0);
                String alarmName = cursor.getString(1);
                int alarmHour = cursor.getInt(2);
                int alarmMinutes = cursor.getInt(3);
                boolean alarmIsRepeating = cursor.getInt(4) == 1 ? true: false;
                String alarmPillBox = cursor.getString(5);
                String alarmPillNumber = cursor.getString(6);

                AlarmModel newAlarm = new AlarmModel(alarmID, alarmName, alarmHour, alarmMinutes,
                        alarmIsRepeating, alarmPillBox, alarmPillNumber);
                returnList.add(newAlarm);

            } while (cursor.moveToNext());

        } else {
            // failure. we do not add anything to the list.
        }

        // we close both the cursor and the database when we are done.
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean deleteOne(AlarmModel alarmModel) {
        //find alarmModel in the database. if found -> delete it and return true.
        // it not found -> return false
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "DELETE FROM " + ALARMS_TABLE + " WHERE " + COLUMN_ID + " = " + alarmModel.getId();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

}
