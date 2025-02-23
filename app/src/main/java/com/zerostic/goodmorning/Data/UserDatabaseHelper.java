package com.zerostic.goodmorning.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserDetails.db";
    public static final String TABLE_NAME1 = "user_info";
    public static final String TABLE_NAME2 = "user_settings";
    public static final String TABLE_NAME3 = "user_subscription";
    public static final String TABLE_NAME4 = "user_ads";


    public UserDatabaseHelper (Context context){
        super(context, DATABASE_NAME, null, 6);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME1 + " (ID TEXT PRIMARY KEY, FIRST_NAME TEXT, LAST_NAME TEXT, PHONE_NUMBER TEXT, EMAIL TEXT, COUNTRY TEXT, PAYMENT_STATUS TEXT, IMAGE_URI TEXT, ACCOUNT_OPEN_DATE TEXT, VERIFICATION TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_NAME2 + " (ID TEXT PRIMARY KEY, DEFAULT_THEME TEXT, DEFAULT_VOICE_SETTINGS TEXT, DEFAULT_WEATHER_SETTINGS TEXT, TYPE_OF_SLEEPER TEXT, WEATHER TEXT, UPDATE_PROMPT TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_NAME3 + " (ID TEXT PRIMARY KEY, SUBSCRIPTION TEXT, PAYMENT_DATE TEXT, NEXT_PAYMENT_DATE TEXT, CURRENCY TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_NAME4 + " (ID TEXT PRIMARY KEY, DATE TEXT, ADS_WATCHED TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* if (oldVersion < 2) {
            db.execSQL("ALTER TABLE "+TABLE_NAME1+" ADD COLUMN "+COLUMN_NAME+" TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE "+TABLE_NAME1+" ADD COLUMN "+COLUMN_NAME+" TEXT");
        }*/
        switch(oldVersion) {
            case 1:
                //upgrade logic from version 1 to 2
                db.execSQL("ALTER TABLE "+TABLE_NAME1+" ADD COLUMN "+"VERIFICATION"+" TEXT");
            case 2:
                //upgrade logic from version 2 to 3
                db.execSQL("CREATE TABLE "+TABLE_NAME3 + " (ID TEXT PRIMARY KEY, SUBSCRIPTION TEXT, PAYMENT_DATE TEXT, NEXT_PAYMENT_DATE TEXT, CURRENCY TEXT)");
            case 3:
                //upgrade logic from version 3 to 4
                db.execSQL("ALTER TABLE "+TABLE_NAME2+" ADD COLUMN "+"WEATHER"+" TEXT");

            case 4:
                //upgrade logic from version 3 to 4
                db.execSQL("CREATE TABLE "+TABLE_NAME4 + " (ID TEXT PRIMARY KEY, DATE TEXT, ADS_WATCHED TEXT)");


            case 5:
                //upgrade logic from version 3 to 4
                db.execSQL("ALTER TABLE "+TABLE_NAME2 + " ADD COLUMN "+"UPDATE_PROMPT"+" TEXT");
                break;
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown oldVersion " + oldVersion);
        }

    }

    public boolean insertUserData(String id,String fname,String lname, String phoneNumber, String email, String country, String paymentStatus, String imageUri, String accountOpenDate, String verification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("FIRST_NAME", fname);
        contentValues.put("LAST_NAME", lname);
        contentValues.put("PHONE_NUMBER", phoneNumber);
        contentValues.put("EMAIL", email);
        contentValues.put("COUNTRY", country);
        contentValues.put("PAYMENT_STATUS", paymentStatus);
        contentValues.put("IMAGE_URI", imageUri);
        contentValues.put("ACCOUNT_OPEN_DATE", accountOpenDate);
        contentValues.put("VERIFICATION", verification);
        long result = db.insert(TABLE_NAME1,null,contentValues);
           db.close();
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean insertUserSettings(String id, String defaultTheme, String voiceSettings, String weatherSettings, String sleeperType, String weather, String update_prompt){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("DEFAULT_THEME", defaultTheme);
        contentValues.put("DEFAULT_VOICE_SETTINGS", voiceSettings);
        contentValues.put("DEFAULT_WEATHER_SETTINGS", weatherSettings);
        contentValues.put("TYPE_OF_SLEEPER", sleeperType);
        contentValues.put("WEATHER", weather);
        contentValues.put("UPDATE_PROMPT",update_prompt);
        long result = db.insert(TABLE_NAME2,null,contentValues);
           db.close();
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public static boolean doesUserDetailsExist(Context context) {
        UserDatabaseHelper dataBaseHelper = new UserDatabaseHelper(context);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = dataBaseHelper.readUserData("1", db);
        return cursor.getCount() > 0;
    }

    public static boolean doesUserSettingsExist(Context context) {
        UserDatabaseHelper dataBaseHelper = new UserDatabaseHelper(context);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = dataBaseHelper.readUserSettings("1", db);
        return cursor.getCount() > 0;

    }

    public boolean insertAdsData(String id, String date, String adsWatched){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("DATE", date);
        contentValues.put("ADS_WATCHED", adsWatched);
        long result = db.insert(TABLE_NAME4,null,contentValues);
        db.close();
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean insertPaymentDetails(String id, String subscription, String payDate, String nextPayDate, String currency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("SUBSCRIPTION", subscription);
        contentValues.put("PAYMENT_DATE", payDate);
        contentValues.put("NEXT_PAYMENT_DATE", nextPayDate);
        contentValues.put("CURRENCY", currency);
        long result = db.insert(TABLE_NAME3,null,contentValues);
        db.close();
        return result != -1;
    }

    public void deleteData(String id, SQLiteDatabase sqLiteDatabase){
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        sqLiteDatabase.delete(TABLE_NAME4,selection,selection_args);
    }

    public void deleteUserSettings(String id, SQLiteDatabase sqLiteDatabase){
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        sqLiteDatabase.delete(TABLE_NAME2,selection,selection_args);
    }

    public void deleteAllUserData(String id, SQLiteDatabase sqLiteDatabase){
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        sqLiteDatabase.delete(TABLE_NAME1,selection,selection_args);
        sqLiteDatabase.delete(TABLE_NAME2,selection,selection_args);
        sqLiteDatabase.delete(TABLE_NAME3,selection,selection_args);
        sqLiteDatabase.delete(TABLE_NAME4,selection,selection_args);
    }

    public boolean updateUserData(String id,String fname,String lname, String phoneNumber, String email, String country, String paymentStatus, String imageUri, String accountOpenDate, String verification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("FIRST_NAME", fname);
        contentValues.put("LAST_NAME", lname);
        contentValues.put("PHONE_NUMBER", phoneNumber);
        contentValues.put("EMAIL", email);
        contentValues.put("COUNTRY", country);
        contentValues.put("PAYMENT_STATUS", paymentStatus);
        contentValues.put("IMAGE_URI", imageUri);
        contentValues.put("ACCOUNT_OPEN_DATE", accountOpenDate);
        contentValues.put("VERIFICATION", verification);
        long result = db.update(TABLE_NAME1,contentValues,"ID =?",new String[]{id});
        db.close();
        return result > 0;
    }

    public boolean updateAdsData(String id, String date, String adsWatched){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("DATE", date);
        contentValues.put("ADS_WATCHED", adsWatched);
        long result = db.update(TABLE_NAME4,contentValues,"ID =?",new String[]{id});
        db.close();
        return result > 0;
    }

    public boolean updateUserSettingsColumn(String id, String voiceSettings){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEFAULT_VOICE_SETTINGS", voiceSettings);
        long result = db.update(TABLE_NAME2, contentValues,  "ID= ?", new String[] {id});
        db.close();
        return result > 0;
    }

    public boolean updatePaymentDetails(String id, String subscription, String payDate, String nextPayDate, String currency){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("SUBSCRIPTION", subscription);
        contentValues.put("PAYMENT_DATE", payDate);
        contentValues.put("NEXT_PAYMENT_DATE", nextPayDate);
        contentValues.put("CURRENCY", currency);
        long result = db.update(TABLE_NAME3,contentValues,"ID =?",new String[]{id});
        db.close();
        return result > 0;
    }

    public boolean updateUserSettings(String id, String defaultTheme, String voiceSettings, String weatherSettings, String sleeperType,String weather,String update_prompt){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("DEFAULT_THEME", defaultTheme);
        contentValues.put("DEFAULT_VOICE_SETTINGS", voiceSettings);
        contentValues.put("DEFAULT_WEATHER_SETTINGS", weatherSettings);
        contentValues.put("TYPE_OF_SLEEPER", sleeperType);
        contentValues.put("WEATHER", weather);
        contentValues.put("UPDATE_PROMPT",update_prompt);
        long result = db.update(TABLE_NAME2,contentValues,"ID =?",new String[]{id});
        db.close();
        return result > 0;
    }

    public Cursor readUserData(String id, SQLiteDatabase db){
        String[] projection = {"ID","FIRST_NAME","LAST_NAME","PHONE_NUMBER","EMAIL","COUNTRY","PAYMENT_STATUS","IMAGE_URI","ACCOUNT_OPEN_DATE","VERIFICATION"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME1, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public  Cursor readPaymentData(String id, SQLiteDatabase db){
        String[] projection = {"ID","SUBSCRIPTION","PAYMENT_DATE","NEXT_PAYMENT_DATE","CURRENCY"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME3, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public  Cursor readAdsData(String id, SQLiteDatabase db){
        String[] projection = {"ID","DATE","ADS_WATCHED"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME4, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public Cursor readUserSettings(String id, SQLiteDatabase db){
        String[] projection = {"ID","DEFAULT_THEME","DEFAULT_VOICE_SETTINGS","DEFAULT_WEATHER_SETTINGS","TYPE_OF_SLEEPER","WEATHER","UPDATE_PROMPT"};
        String selection = "ID"+" LIKE ?";
        String[] selection_args = {id};
        Cursor cursor = db.query(TABLE_NAME2, projection, selection, selection_args, null, null, null);
        return  cursor;
    }

    public String getFirstName() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = readUserData("1", db);
        cursor.moveToFirst();
        return cursor.getString(1);
    }
}
