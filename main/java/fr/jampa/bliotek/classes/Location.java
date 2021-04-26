package fr.jampa.bliotek.classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {

    private int location_id, room_id, floor_id, city_id, country_id;
    private String description, room, floor, city, country;
    SQLiteDataBaseHelper dbHelper;

    public Location hydrateFromCursor(Cursor DataFromDB){
        description = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(dbHelper.LAST_DESCRIPTION));
        room = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(dbHelper.LAST_ROOM));
        floor =DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(dbHelper.LAST_FLOOR));
        city =DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(dbHelper.LAST_CITY));
        country =DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(dbHelper.LAST_COUNTRY));
        return this;
    }
    public Location hydrateFromJson(JSONObject json){
        try{
            if(json.has("location_id")){
                location_id = json.getInt("location_id");
            }
            if(json.has("room_id")){
                room_id = json.getInt("room_id");
            }
            if(json.has("floor_id")){
                floor_id = json.getInt("floor_id");
            }
            if(json.has("city_id")){
                city_id = json.getInt("city_id");
            }
            if(json.has("country_id")){
                country_id = json.getInt("country_id");
            }
            if(json.has("description")){
                description = json.getString("description");
            }
            if(json.has("room_name")){
                room = json.getString("room_name");
            }
            if(json.has("room")){
                room = json.getString("room");
            }
            if(json.has("floor_name")){
                floor = json.getString("floor_name");
            }
            if(json.has("floor")){
                floor = json.getString("floor");
            }
            if(json.has("city_name")){
                city = json.getString("city_name");
            }
            if(json.has("city")){
                city = json.getString("city");
            }
            if(json.has("country_name")){
                country = json.getString("country_name");
            }
            if(json.has("country")){
                country = json.getString("country");
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return this;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getFloor_id() {
        return floor_id;
    }

    public void setFloor_id(int floor_id) {
        this.floor_id = floor_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
