package com.example.g29.msbandapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.CurrencyPluralInfo;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "msband.db";
/*    public static final String TABLE_NAME = "";
    public static final String COL_NAME = "";
    public static final String COL_VALUE = "";*/

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table if not exists acc_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, X REAL, Y REAL, Z REAL, TS INTEGER)");
        db.execSQL("Create Table if not exists alt_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, FLIGHTS_ASC INTEGER, FLIGHTS_DESC INTEGER, RATE REAL, STEP_GAIN INTEGER, STEP_LOSS INTEGER, STEPS_ASC INTEGER, STEPS_DESC INTEGER, TOTAL_GAIN INTEGER, TOTAL_LOSS INTEGER, TS INTEGER)");
        db.execSQL("Create Table if not exists amb_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, BRIGHTNESS INTEGER, TS INTEGER)");
        db.execSQL("Create Table if not exists bar_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, AIR_PRESSURE REAL, TEMP_CEL REAL, TEMP_FAH REAL, TS INTEGER)");
        db.execSQL("Create Table if not exists cal_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, CALORIES INTEGER, TS INTEGER)");
        db.execSQL("Create Table if not exists cont_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, CONTACT_STATE TEXT, TS INTEGER)");
        db.execSQL("Create Table if not exists dist_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, MOTION_TYPE TEXT, PACE REAL, SPEED REAL, TOTAL_DISTANCE INTEGER, TS INTEGER)");
        db.execSQL("Create Table if not exists gsr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, GSR INTEGER, TS INTEGER)");
        db.execSQL("Create Table if not exists gyr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, AVX REAL, AVY REAL, AVZ REAL, TS INTEGER)");
        db.execSQL("Create Table If not exists hr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, HR INTEGER, QUALITY TEXT, TS INTEGER)");
        db.execSQL("Create Table if not exists ped_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, TOTAL_STEPS INTEGER, TS INTEGER)");
        db.execSQL("Create Table if not exists rr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, RR REAL, TS INTEGER)");
        db.execSQL("Create Table if not exists skinTemp_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, TEMP_CEL REAL, TEMP_FAH REAL, TS INTEGER)");
        db.execSQL("Create Table if not exists uv_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, INDEX_LEVEL TEXT, TS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*        if(newVersion > oldVersion){
            String query = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_NAME + " " + COL_VALUE;
            db.execSQL(query);
        }*/
        //onCreate(db);
    }

    public boolean insertData(int userId, AccData accData, AltData altData, AmbData ambData, BarData barData, CalData calData, ContData contData,
                              DistData distData, GSRData gsrData, GyrData gyrData, HRData hrData, PedData pedData, RRData rrData, SkinTempData skinTempData,
                              UVData uvData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues accValues = new ContentValues();
        accValues.put("USER_ID", userId);
        accValues.put("X", accData.x);
        accValues.put("Y", accData.y);
        accValues.put("Z", accData.z);
        accValues.put("TS", accData.ts);

        ContentValues altValues = new ContentValues();
        altValues.put("USER_ID", userId);
        altValues.put("FLIGHTS_ASC", altData.flightsAscended);
        altValues.put("FLIGHTS_DESC", altData.flightsDescended);
        altValues.put("RATE", altData.rate);
        altValues.put("STEP_GAIN", altData.steppingGain);
        altValues.put("STEP_LOSS", altData.steppingLoss);
        altValues.put("STEPS_ASC", altData.stepsAscended);
        altValues.put("STEPS_DESC", altData.stepsDescended);
        altValues.put("TOTAL_GAIN", altData.totalGain);
        altValues.put("TOTAL_LOSS", altData.totalLoss);
        altValues.put("TS", altData.ts);

        ContentValues ambValues = new ContentValues();
        ambValues.put("USER_ID", userId);
        ambValues.put("BRIGHTNESS", ambData.brightness);
        ambValues.put("TS", ambData.ts);

        ContentValues barValues = new ContentValues();
        barValues.put("USER_ID", userId);
        barValues.put("AIR_PRESSURE", barData.airPressure);
        barValues.put("TEMP_CEL", barData.temperature_cel);
        barValues.put("TEMP_FAH", barData.temperature_fah);
        barValues.put("TS", barData.ts);

        ContentValues calValues = new ContentValues();
        calValues.put("USER_ID", userId);
        calValues.put("CALORIES", calData.calories);
        calValues.put("TS", calData.ts);

        ContentValues contactValues = new ContentValues();
        contactValues.put("USER_ID", userId);
        contactValues.put("CONTACT_STATE", contData.contactStateStr);
        contactValues.put("TS", contData.ts);

        ContentValues distValues = new ContentValues();
        distValues.put("USER_ID", userId);
        distValues.put("MOTION_TYPE", distData.motionTypeStr);
        distValues.put("PACE", distData.pace);
        distValues.put("SPEED", distData.speed);
        distValues.put("TOTAL_DISTANCE", distData.totalDistance);
        distValues.put("TS", distData.ts);

        ContentValues gsrValues = new ContentValues();
        gsrValues.put("USER_ID", userId);
        gsrValues.put("GSR", gsrData.gsrValue);
        gsrValues.put("TS", gsrData.ts);

        ContentValues gyrValues = new ContentValues();
        gyrValues.put("USER_ID", userId);
        gyrValues.put("AVX", gyrData.avx);
        gyrValues.put("AVY", gyrData.avy);
        gyrValues.put("AVZ", gyrData.avz);
        gyrValues.put("TS", gyrData.ts);

        ContentValues hrValues = new ContentValues();
        hrValues.put("USER_ID", userId);
        hrValues.put("HR", hrData.hr);
        hrValues.put("QUALITY", hrData.quality);
        hrValues.put("TS", hrData.ts);

        ContentValues pedValues = new ContentValues();
        pedValues.put("USER_ID", userId);
        pedValues.put("TOTAL_STEPS", pedData.totalSteps);
        pedValues.put("TS", pedData.ts);

        ContentValues rrValues = new ContentValues();
        rrValues.put("USER_ID", userId);
        rrValues.put("RR", rrData.rr);
        rrValues.put("TS", rrData.ts);

        ContentValues skinTempValues = new ContentValues();
        skinTempValues.put("USER_ID", userId);
        skinTempValues.put("TEMP_CEL", skinTempData.temperature_cel);
        skinTempValues.put("TEMP_FAH", skinTempData.temperature_fah);
        skinTempValues.put("TS", skinTempData.ts);

        ContentValues uvValues = new ContentValues();
        uvValues.put("USER_ID", userId);
        uvValues.put("INDEX_LEVEL", uvData.indexLevelStr);
        uvValues.put("TS", uvData.ts);

        long accResult = db.insert("acc_data", null, accValues);
        long altResult = db.insert("alt_data", null, altValues);
        long ambResult = db.insert("amb_data", null, ambValues);
        long barResult = db.insert("bar_data", null, barValues);
        long calResult = db.insert("cal_data", null, calValues);
        long contResult = db.insert("cont_data", null, contactValues);
        long distResult = db.insert("dist_data", null, distValues);
        long gsrResult = db.insert("gsr_data", null, gsrValues);
        long gyrResult = db.insert("gyr_data", null, gyrValues);
        long hrResult = db.insert("hr_data", null, hrValues);
        long pedResult = db.insert("ped_data", null, pedValues);
        long rrResult = db.insert("rr_data", null, rrValues);
        long skinTempResult = db.insert("skinTemp_data", null, skinTempValues);
        long uvResult = db.insert("uv_data", null, uvValues);

        if(accResult == -1 || altResult == -1 || ambResult == -1 || barResult == -1 || calResult == -1 ||
        contResult == -1 || distResult == -1 || gsrResult == -1 || gyrResult == -1 || hrResult == -1 ||
        pedResult == -1 || rrResult == -1 || skinTempResult == -1 || uvResult == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAccData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM acc_data", null);
        //db.execSQL("DROP TABLE acc_data");
        //db.execSQL("Create Table if not exists acc_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, X REAL, Y REAL, Z REAL, TS INTEGER)");

        return cursor;

    }

    public Cursor getAltData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM alt_data", null);
        //db.execSQL("DROP TABLE alt_data");
        //db.execSQL("Create Table if not exists alt_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, FLIGHTS_ASC INTEGER, FLIGHTS_DESC INTEGER, RATE REAL, STEP_GAIN INTEGER, STEP_LOSS INTEGER, STEPS_ASC INTEGER, STEPS_DESC INTEGER, TOTAL_GAIN INTEGER, TOTAL_LOSS INTEGER, TS INTEGER)");

        return cursor;
    }

    public Cursor getAmbData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM amb_data", null);
        //db.execSQL("DROP TABLE amb_data");
        //db.execSQL("Create Table if not exists amb_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, BRIGHTNESS INTEGER, TS INTEGER)");

        return cursor;
    }

    public Cursor getBarData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bar_data", null);
        //db.execSQL("DROP TABLE bar_data");
        //db.execSQL("Create Table if not exists bar_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, AIR_PRESSURE REAL, TEMP_CEL REAL, TEMP_FAH REAL, TS INTEGER)");

        return cursor;
    }

    public Cursor getCalData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cal_data", null);
        //db.execSQL("DROP TABLE cal_data");
        //db.execSQL("Create Table if not exists cal_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, CALORIES INTEGER, TS INTEGER)");

        return cursor;
    }

    public Cursor getContData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cont_data", null);
        //db.execSQL("DROP TABLE cont_data");
        //db.execSQL("Create Table if not exists cont_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, CONTACT_STATE TEXT, TS INTEGER)");

        return cursor;
    }

    public Cursor getDistData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM dist_data", null);
        //db.execSQL("DROP TABLE dist_data");
        //db.execSQL("Create Table if not exists dist_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, MOTION_TYPE TEXT, PACE REAL, SPEED REAL, TOTAL_DISTANCE INTEGER, TS INTEGER)");

        return cursor;
    }

    public Cursor getGsrData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM gsr_data", null);
        //db.execSQL("DROP TABLE gsr_data");
        //db.execSQL("Create Table if not exists gsr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, GSR INTEGER, TS INTEGER)");

        return cursor;
    }

    public Cursor getGyrData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM gyr_data", null);
        //db.execSQL("DROP TABLE gyr_data");
        //db.execSQL("Create Table if not exists gyr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, AVX REAL, AVY REAL, AVZ REAL, TS INTEGER)");

        return cursor;
    }

    public Cursor getHrData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM hr_data", null);
        //db.execSQL("DROP TABLE hr_data");
        //db.execSQL("Create Table If not exists hr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, HR INTEGER, QUALITY TEXT, TS INTEGER)");

        return cursor;
    }

    public Cursor getPedData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ped_data", null);
        //db.execSQL("DROP TABLE ped_data");
        //db.execSQL("Create Table if not exists ped_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, TOTAL_STEPS INTEGER, TS INTEGER)");

        return cursor;
    }

    public Cursor getRrData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM rr_data", null);
        //db.execSQL("DROP TABLE rr_data");
        //db.execSQL("Create Table if not exists rr_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, RR REAL, TS INTEGER)");

        return cursor;
    }

    public Cursor getSkinTempData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM skinTemp_data", null);
        //db.execSQL("DROP TABLE skinTemp_data");
        //db.execSQL("Create Table if not exists skinTemp_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, TEMP_CEL REAL, TEMP_FAH REAL, TS INTEGER)");

        return cursor;
    }

    public Cursor getUvData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM uv_data", null);
        //db.execSQL("DROP TABLE uv_data");
        //db.execSQL("Create Table if not exists uv_data (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_ID INTEGER, INDEX_LEVEL TEXT, TS INTEGER)");

        return cursor;
    }

    public void deleteAllRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM acc_data");
        db.execSQL("DELETE FROM alt_data");
        db.execSQL("DELETE FROM amb_data");
        db.execSQL("DELETE FROM bar_data");
        db.execSQL("DELETE FROM cal_data");
        db.execSQL("DELETE FROM cont_data");
        db.execSQL("DELETE FROM dist_data");
        db.execSQL("DELETE FROM gsr_data");
        db.execSQL("DELETE FROM gyr_data");
        db.execSQL("DELETE FROM hr_data");
        db.execSQL("DELETE FROM ped_data");
        db.execSQL("DELETE FROM rr_data");
        db.execSQL("DELETE FROM skinTemp_data");
        db.execSQL("DELETE FROM uv_data");

        return;
    }
}
