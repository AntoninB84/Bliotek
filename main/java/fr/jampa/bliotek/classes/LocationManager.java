package fr.jampa.bliotek.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.time.LocalDateTime;

/**
 * The last selected/inserted book's location in AddBook Activity is stored in local DB.
 * Setting and getting DB values
 * **/

public class LocationManager {
    Context myContext;
    SQLiteDataBaseHelper dbHelper;
    SQLiteDatabase db;

    public LocationManager(Context context){
        myContext = context;
        dbHelper = new SQLiteDataBaseHelper(myContext);
        db = dbHelper.getWritableDatabase();
    }

    public void changeLastLocation(Location location){
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.LAST_COUNTRY, location.getCountry());
        contentValues.put(dbHelper.LAST_CITY, location.getCity());
        contentValues.put(dbHelper.LAST_FLOOR, location.getFloor());
        contentValues.put(dbHelper.LAST_ROOM, location.getRoom());
        contentValues.put(dbHelper.LAST_DESCRIPTION, location.getDescription());
        db.update(dbHelper.LAST_LOCATION_TABLE_NAME, contentValues, dbHelper.LAST_LOCATION_ID+"=1", null);
    }
    public Location getLastLocation(){
        String query = "SELECT * FROM "+dbHelper.LAST_LOCATION_TABLE_NAME;
        try(Cursor result =db.rawQuery(query, null)){
            result.moveToFirst();
            if(!(result == null) && result.getCount()>0){
                Location location = new Location().hydrateFromCursor(result);
                return location;
            }else{
                Location location = new Location();
                return location;
            }

        }
    }
}
