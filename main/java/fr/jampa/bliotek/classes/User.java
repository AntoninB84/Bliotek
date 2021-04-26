package fr.jampa.bliotek.classes;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    int user_id, user_subscription_timestamp, user_privileges;
    String user_name, user_email, user_APIkey;
    SQLiteDataBaseHelper db;

    public User(){}

    public User hydrateFromCursor(Cursor DataFromDB){
        user_id = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.USER_ID_FIELD));
        user_name = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.USER_NAME_FIELD));
        user_email = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.USER_EMAIL_FIELD));
        user_APIkey = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.USER_APIKEY_FIELD));
        user_subscription_timestamp = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.USER_SUBSCRIPTION_FIELD));
        user_privileges = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.USER_PRIVILEGES_FIELD));
        return this;
    }
    public User hydrateFromJson(JSONObject json){

        try {
            user_id = json.getInt("user_id");
            user_name = json.getString("name");
            user_email = json.getString("email");
            user_APIkey = json.getString("APIkey");
            user_subscription_timestamp = json.getInt("subscription_timestamp");
            user_privileges = json.getInt("privileges");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_subscription_timestamp() {
        return user_subscription_timestamp;
    }

    public void setUser_subscription_timestamp(int user_subscription_timestamp) {
        this.user_subscription_timestamp = user_subscription_timestamp;
    }

    public int getUser_privileges() {
        return user_privileges;
    }

    public void setUser_privileges(int user_privileges) {
        this.user_privileges = user_privileges;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_APIkey() {
        return user_APIkey;
    }

    public void setUser_APIkey(String user_APIkey) {
        this.user_APIkey = user_APIkey;
    }


}
