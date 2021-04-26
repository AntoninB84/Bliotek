package fr.jampa.bliotek.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;
/**
 * Some user informations are stored in local DB.
 * If none, the user is disconnected.
 * Setting and getting DB values
 * **/
public class UserManager {
    Context myContext;
    SQLiteDataBaseHelper dbHelper;
    SQLiteDatabase db;
    User user;

    // Constructor setting context and connection to DB
    public UserManager(Context context){
        myContext = context;
        dbHelper = new SQLiteDataBaseHelper(myContext);
        db = dbHelper.getWritableDatabase();
        user = new User();
    }

    public User getUserInfos(){
        String query = "SELECT * FROM "+dbHelper.USER_TABLE_NAME;
        Cursor result = db.rawQuery(query, null);
        result.moveToFirst(); // We only need to check the first row, as there is only one user registered in DB at a time
        if(!(result == null) && result.getCount()>0){
            user.hydrateFromCursor(result); // Here we found a result in DB, so the user is connected
            return user;
        }else{
            return null; // Here we did not, the user is not connected
        }
    }

    public void addUser(JSONObject fromWeb){
        user.hydrateFromJson(fromWeb);
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.USER_ID_FIELD, user.getUser_id());
        contentValues.put(dbHelper.USER_NAME_FIELD, user.getUser_name());
        contentValues.put(dbHelper.USER_EMAIL_FIELD, user.getUser_email());
        contentValues.put(dbHelper.USER_APIKEY_FIELD, user.getUser_APIkey());
        contentValues.put(dbHelper.USER_SUBSCRIPTION_FIELD, user.getUser_subscription_timestamp());
        contentValues.put(dbHelper.USER_PRIVILEGES_FIELD, user.getUser_privileges());
        db.insert(dbHelper.USER_TABLE_NAME, null, contentValues);

    }
    /** Called to disconnect user **/
    public void deleteUser(){
       db.delete(dbHelper.USER_TABLE_NAME, dbHelper.USER_ID_FIELD, null);

    }
}
