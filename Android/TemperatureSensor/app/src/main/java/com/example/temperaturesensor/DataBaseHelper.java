package com.example.temperaturesensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends  SQLiteOpenHelper{
    private final static String TEMP_TABLE = "temperature";
    private final static String LAST_TEMP = "last_temp";
    private final static String HIGH_TEMP = "high_temp";
    private final static String LOW_TEMP = "low_temp";


    public DataBaseHelper(Context context) {
        super(context, TEMP_TABLE, null, 1);
    }

    //creates the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TEMP_TABLE
                +  "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + LAST_TEMP + " TEXT,"
                + HIGH_TEMP + " TEXT,"
                + LOW_TEMP + " TEXT)";
        db.execSQL(createTableQuery);

    }

    public boolean addTemp(String lastTemp, String highTemp, String lowTemp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_TEMP,lastTemp);
        contentValues.put(HIGH_TEMP,highTemp);
        contentValues.put(LOW_TEMP,lowTemp);
        long data = db.insert(TEMP_TABLE,null,contentValues);
        if (data == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getTemp(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.query(TEMP_TABLE,new String[]{LAST_TEMP,HIGH_TEMP,LOW_TEMP},null,null,null,null,"ID DESC","1");
        return data;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
