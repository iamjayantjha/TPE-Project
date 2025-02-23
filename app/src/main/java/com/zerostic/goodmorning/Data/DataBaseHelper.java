package com.zerostic.goodmorning.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zerostic.goodmorning.Activities.MainActivity;
import com.zerostic.goodmorning.Application.DataList;

import java.util.ArrayList;

/**
 Coded by iamjayantjha
 **/

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AlarmDetails.db";
    public static final String TABLE_NAME1 = "alarm_table";
    public static final String TABLE_NAME2 = "method_table";
    public static final String TABLE_NAME3 = "alarm_hr_min";
    public static final String TABLE_NAME4 = "location_data";
    public static final String TABLE_NAME5 = "recurring_days";
    public static final String TABLE_NAME6 = "tracker";
    public static final String COL_NO1 = "ID";
    public static final String COL_NO2 = "HOUR";
    public static final String COL_NO3 = "MINUTE";
    public static final String COL_NO4 = "TITLE";
    public static final String COL_NO5 = "METHOD";
    public static final String COL_NO6 = "ALARM_TONE";
    public static final String COL_NO7 = "SNOOZE";
    public static final String COL_NO8 = "SNOOZE_ORDER";
    public static final String COL_NO9 = "VOLUME";
    public static final String COL_NO10 = "VIBRATE";
    public static final String COL_NO11 = "WAKE_UP_CHECK";
    public static final String COL_NO12 = "TYPE";

    public DataBaseHelper (Context context){
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME1 + " (ID TEXT PRIMARY KEY, HOUR TEXT, MINUTE TEXT, TITLE TEXT, METHOD TEXT, ALARM_TONE TEXT, SNOOZE TEXT, SNOOZE_ORDER TEXT, VOLUME TEXT, VIBRATE TEXT, WAKE_UP_CHECK TEXT, TYPE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME2 + " (METHOD TEXT PRIMARY KEY, NUMBER_OF_QUEST TEXT, DIFFICULTY TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME3 + " (ID TEXT PRIMARY KEY, HOUR TEXT, MINUTE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME4 + " (ID TEXT PRIMARY KEY, LOCATION TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME5 + " (ID TEXT PRIMARY KEY, SUN TEXT, MON TEXT, TUE TEXT, WED TEXT, THU TEXT, FRI TEXT, SAT TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME6 + " (ID TEXT PRIMARY KEY, START_TIME TEXT, END_TIME TEXT, SLEEP_HOUR TEXT, STATUS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
         switch(i) {
            case 2:
                //upgrade logic from version 2 to 3
                sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_NAME6 + " (ID TEXT PRIMARY KEY, START_TIME TEXT, END_TIME TEXT, SLEEP_HOUR TEXT)");
            case 3:
                //upgrade logic from version 3 to 4
                sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_NAME6+" ADD COLUMN "+"STATUS"+" TEXT");
                break;
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown oldVersion " + i);
        }

    }

    public static String getUserLocation(Context context){
        String location = "";
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = dataBaseHelper.readLocation("1", db);
        if (cursor.getCount() == 0){
            return location;
        }else {
            while (cursor.moveToNext()){
                location = cursor.getString(1);
            }
        }
        return location;
    }

    public boolean insertData(String id,String hour, String minute, String title, String method, String alarmTone, String snooze, String snooze_order, String volume, String vibrate, String wake_up_check, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NO1, id);
        contentValues.put(COL_NO2, hour);
        contentValues.put(COL_NO3, minute);
        contentValues.put(COL_NO4, title);
        contentValues.put(COL_NO5, method);
        contentValues.put(COL_NO6, alarmTone);
        contentValues.put(COL_NO7, snooze);
        contentValues.put(COL_NO8, snooze_order);
        contentValues.put(COL_NO9, volume);
        contentValues.put(COL_NO10, vibrate);
        contentValues.put(COL_NO11, wake_up_check);
        contentValues.put(COL_NO12, type);
        long result = db.insert(TABLE_NAME1,null,contentValues);
     //   db.close();
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean insertTrackerData(String id,String start_time, String end_time, String sleep_hour, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("START_TIME", start_time);
        contentValues.put("END_TIME", end_time);
        contentValues.put("SLEEP_HOUR", sleep_hour);
        contentValues.put("STATUS", status);
        long result = db.insert(TABLE_NAME6,null,contentValues);
        //db.close();
        return result != -1;
    }

    public boolean updateTrackerData(String id,String start_time, String end_time, String sleep_hour, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("START_TIME", start_time);
        contentValues.put("END_TIME", end_time);
        contentValues.put("SLEEP_HOUR", sleep_hour);
        contentValues.put("STATUS", status);
        long result = db.update(TABLE_NAME6,contentValues,"ID =?",new String[]{id});
     //   db.close();
        return result != -1;
    }

    public Cursor readTrackerData(String id, SQLiteDatabase db){
        String[] projection = {"ID","START_TIME","END_TIME","SLEEP_HOUR","STATUS"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME6, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public void deleteTrackerData(String id, SQLiteDatabase sqLiteDatabase){
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        sqLiteDatabase.delete(TABLE_NAME6,selection,selection_args);
    }

    public void deleteEntireTrackerData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME6);
    }


    public boolean insertRecurringData(String id ,String sun, String mon, String tue, String wed, String thu, String fri, String sat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("SUN", sun);
        contentValues.put("MON", mon);
        contentValues.put("TUE", tue);
        contentValues.put("WED", wed);
        contentValues.put("THU", thu);
        contentValues.put("FRI", fri);
        contentValues.put("SAT", sat);
        long result = db.insert(TABLE_NAME5,null,contentValues);
        return result != -1;
    }

    public boolean updateRecurringData(String id ,String sun, String mon, String tue, String wed, String thu, String fri, String sat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("SUN", sun);
        contentValues.put("MON", mon);
        contentValues.put("TUE", tue);
        contentValues.put("WED", wed);
        contentValues.put("THU", thu);
        contentValues.put("FRI", fri);
        contentValues.put("SAT", sat);
        long result = db.update(TABLE_NAME5,contentValues,"ID =?",new String[]{id});
        return result != -1;
    }

    public Cursor readRecurringData(String id, SQLiteDatabase db){
        String[] projection = {"ID","SUN","MON","TUE", "WED", "THU", "FRI", "SAT"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME5, projection, selection, selection_args, null, null, null);
        return  cursor;
    }


    public boolean insertLocation(String id, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("LOCATION", location);
        long result = db.insert(TABLE_NAME4,null,contentValues);
       // db.close();
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean updateLocation(String id, String location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("LOCATION", location);
        long result = db.update(TABLE_NAME4,contentValues,"ID =?",new String[]{id});
        if (result>0){
            return true;
        }else {
            return false;
        }
    }

    public Cursor readLocation(String id, SQLiteDatabase db){
        String[] projection = {"ID","LOCATION"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME4, projection, selection, selection_args, null, null, null);
        return  cursor;
    }


    public boolean insertHrMin(String id, String hour, String minute){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",id);
        contentValues.put("HOUR", hour);
        contentValues.put("MINUTE", minute);
        long result = db.insert(TABLE_NAME3,null,contentValues);
        //db.close();
        if (result >0){
            return true;
        }else {
            return  false;
        }
    }

    public boolean updateHrMin(String id, String hour, String minute){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",id);
        contentValues.put("HOUR", hour);
        contentValues.put("MINUTE", minute);
        long result = db.update(TABLE_NAME3,contentValues,"ID =?",new String[]{id});
        //db.close();
        if (result >0){
            return true;
        }else {
            return  false;
        }
    }

    public Cursor readHrMin(String id, SQLiteDatabase db){
        String[] projection = {"ID","HOUR","MINUTE"};
        String selection = COL_NO1+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME3, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public boolean updateData(String id, String hour, String minute, String title, String method, String alarmTone, String snooze, String snooze_order, String volume, String vibrate, String wake_up_check, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NO1, id);
        contentValues.put(COL_NO2, hour);
        contentValues.put(COL_NO3, minute);
        contentValues.put(COL_NO4, title);
        contentValues.put(COL_NO5, method);
        contentValues.put(COL_NO6, alarmTone);
        contentValues.put(COL_NO7, snooze);
        contentValues.put(COL_NO8, snooze_order);
        contentValues.put(COL_NO9, volume);
        contentValues.put(COL_NO10, vibrate);
        contentValues.put(COL_NO11, wake_up_check);
        contentValues.put(COL_NO12, type);
        long result = db.update(TABLE_NAME1,contentValues,"ID =?",new String[]{id});
        if (result>0){
            return true;
        }else {
            return false;
        }
    }

    public boolean insertMethodData(String method, String num_of_quest, String difficulty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Method", method);
        contentValues.put("Number_Of_Quest", num_of_quest);
        contentValues.put("Difficulty", difficulty);
        long result = db.insert(TABLE_NAME2,null,contentValues);
      //  db.close();
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean updateMethodData(String method, String num_of_quest, String difficulty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Method", method);
        contentValues.put("Number_Of_Quest", num_of_quest);
        contentValues.put("Difficulty", difficulty);
        int result = db.update(TABLE_NAME2,contentValues,"Method =?",new String[]{method});
        if (result>0){
            return true;
        }else {
            return false;
        }
    }

    public Cursor readData(String id, SQLiteDatabase db){
        String[] projection = {COL_NO1,COL_NO2,COL_NO3,COL_NO4, COL_NO5, COL_NO6, COL_NO7, COL_NO8, COL_NO9, COL_NO10, COL_NO11, COL_NO12};
        String selection = COL_NO1+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME1, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public Cursor readHourMin(String hour,String min, SQLiteDatabase db){
        String[] projection = {COL_NO1,COL_NO2,COL_NO3,COL_NO4, COL_NO5, COL_NO6, COL_NO7, COL_NO8, COL_NO9, COL_NO10, COL_NO11, COL_NO12};
        String selection = COL_NO2+" LIKE ? AND "+COL_NO3+" LIKE ?";
        String[] selection_args = {hour,min};
        Cursor cursor = db.query(TABLE_NAME1, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public Cursor readMethodData(String method, SQLiteDatabase db){
        String[] projection = {"Method", "Number_Of_Quest", "Difficulty"};
        String selection = "Method"+" LIKE ?";
        String[] selection_args = {method};
        Cursor cursor = db.query(TABLE_NAME2, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from "+TABLE_NAME1,null);
        return res;
    }


    public Cursor getAllMethodData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from "+TABLE_NAME2,null);
        return res;
    }

    public Cursor getAllTrackerData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from "+TABLE_NAME6,null);
        return res;
    }

    public Cursor getMethodData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from "+TABLE_NAME2,null);
        return res;
    }

    public Cursor getLocationData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from "+TABLE_NAME4,null);
        return res;
    }

    public void deleteData(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        sqLiteDatabase.delete(TABLE_NAME1,selection,selection_args);
        sqLiteDatabase.delete(TABLE_NAME5,selection,selection_args);
        sqLiteDatabase.delete(TABLE_NAME3,selection,selection_args);
    }

    public ArrayList<DataList> getNotes() {
        ArrayList<DataList> arrayList = new ArrayList<>();

        // select all query
        String select_query= "SELECT *FROM " + TABLE_NAME1;

        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataList noteModel = new DataList();
                noteModel.setID(cursor.getString(0));
                noteModel.setHour(cursor.getString(1));
                noteModel.setMinutes(cursor.getString(2));
                arrayList.add(noteModel);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    public boolean isInWUC(int alarmId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME1+" WHERE ID = "+alarmId+" AND TYPE = 'wuc'",null);
        return cursor.getCount() != 0;
    }
}
