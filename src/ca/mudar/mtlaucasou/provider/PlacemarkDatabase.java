package ca.mudar.mtlaucasou.provider;

import ca.mudar.mtlaucasou.provider.PlacemarkContract.PlacemarkColumns;
import ca.mudar.mtlaucasou.provider.PlacemarkContract.SyncColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class PlacemarkDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SecurityDatabase";

    private static final String DATABASE_NAME = "mtlaucasou.db";
    private static final int DATABASE_VERSION = 10;

    interface Tables {
        String FIRE_HALLS = "fire_halls";
        String SPVM_STATIONS = "spvm_stations";
        String WATER_SUPPLIES = "water_supplies";
        String EMERGENCY_HOSTELS = "emergency_hostels";
    }
    
    
    public PlacemarkDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Creating database tables. DB name: " + DATABASE_NAME);
        Log.v(TAG, "Creating database tables: " + Tables.FIRE_HALLS + ", " + Tables.SPVM_STATIONS + ", " + Tables.WATER_SUPPLIES + ", " + Tables.EMERGENCY_HOSTELS );
        
        db.execSQL( "CREATE TABLE " + Tables.FIRE_HALLS + " ( " 
                + BaseColumns._ID                        + " INTEGER PRIMARY KEY , "
                + SyncColumns.UPDATED                    + " INTEGER NOT NULL ,"
                + PlacemarkColumns.PLACEMARK_ID          + " INTEGER NOT NULL , "
                + PlacemarkColumns.PLACEMARK_NAME        + " TEXT NOT NULL DEFAULT '' COLLATE UNICODE, "
                + PlacemarkColumns.PLACEMARK_DESCRIPTION + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_ADDRESS     + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LAT     + " DECIMAL(5,8) NOT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LNG     + " DECIMAL(5,8) NOT NULL, "
                + "UNIQUE (" + PlacemarkColumns.PLACEMARK_ID + ") ON CONFLICT REPLACE)");

        db.execSQL( "CREATE TABLE " + Tables.SPVM_STATIONS + " ( " 
                + BaseColumns._ID                        + " INTEGER PRIMARY KEY , "
                + SyncColumns.UPDATED                    + " INTEGER NOT NULL ,"
                + PlacemarkColumns.PLACEMARK_ID          + " INTEGER NOT NULL , "
                + PlacemarkColumns.PLACEMARK_NAME        + " TEXT NOT NULL DEFAULT '' COLLATE UNICODE, "
                + PlacemarkColumns.PLACEMARK_DESCRIPTION + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_ADDRESS     + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LAT     + " DECIMAL(5,8) NOT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LNG     + " DECIMAL(5,8) NOT NULL, "
                + "UNIQUE (" + PlacemarkColumns.PLACEMARK_ID + ") ON CONFLICT REPLACE)");
        
        db.execSQL( "CREATE TABLE " + Tables.WATER_SUPPLIES + " ( " 
                + BaseColumns._ID                        + " INTEGER PRIMARY KEY , "
                + SyncColumns.UPDATED                    + " INTEGER NOT NULL ,"
                + PlacemarkColumns.PLACEMARK_ID          + " INTEGER NOT NULL , "
                + PlacemarkColumns.PLACEMARK_NAME        + " TEXT NOT NULL DEFAULT '' COLLATE UNICODE, "
                + PlacemarkColumns.PLACEMARK_DESCRIPTION + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_ADDRESS     + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LAT     + " DECIMAL(5,8) NOT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LNG     + " DECIMAL(5,8) NOT NULL, "
                + "UNIQUE (" + PlacemarkColumns.PLACEMARK_ID + ") ON CONFLICT REPLACE)");

        db.execSQL( "CREATE TABLE " + Tables.EMERGENCY_HOSTELS + " ( " 
                + BaseColumns._ID                        + " INTEGER PRIMARY KEY , "
                + SyncColumns.UPDATED                    + " INTEGER NOT NULL ,"
                + PlacemarkColumns.PLACEMARK_ID          + " INTEGER NOT NULL , "
                + PlacemarkColumns.PLACEMARK_NAME        + " TEXT NOT NULL DEFAULT '' COLLATE UNICODE, "
                + PlacemarkColumns.PLACEMARK_DESCRIPTION + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_ADDRESS     + " TEXT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LAT     + " DECIMAL(5,8) NOT NULL, "
                + PlacemarkColumns.PLACEMARK_GEO_LNG     + " DECIMAL(5,8) NOT NULL, "
                + "UNIQUE (" + PlacemarkColumns.PLACEMARK_ID + ") ON CONFLICT REPLACE)");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ". Old data will be destroyed. DB name: " + DATABASE_NAME);
        
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FIRE_HALLS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SPVM_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.WATER_SUPPLIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.EMERGENCY_HOSTELS);

        onCreate(db);
    }
}
