package fr.jampa.bliotek.resources;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.jampa.bliotek.AddBook;
import fr.jampa.bliotek.BookInfos;
import fr.jampa.bliotek.R;
import fr.jampa.bliotek.classes.Location;
import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.HttpPostRequest;

/**
 * Layout that appears on top of BookInfos Activity when LongClick on single exemplary in order to change its location.
 * Some of the functions below have been duplicated in AddBookActivity as I could not get to share them properly...
 * **/

public class PopUpModal {

    private Context context;
    private int exempID;
    int countTimesCalled;
    List<Location> locData, roomData, floorData, cityData, countryData;
    AutoCompleteTextView description, room, floor, city, country;
    PopupWindow popupWindow;

    public PopUpModal(Context context, int exempID){
        this.context = context;
        this.exempID = exempID;
    }

    /**
     * Function that gets called to show the window layout
     * **/
    public void showPopupWindow(final View view) {

        UserManager UserManag = new UserManager(context);
        User user = UserManag.getUserInfos();
        int UserID = user.getUser_id();
        String APIkey = user.getUser_APIkey();

        countTimesCalled = 0;
        locData = new ArrayList<>();
        roomData = new ArrayList<>();
        floorData = new ArrayList<>();
        cityData = new ArrayList<>();
        countryData = new ArrayList<>();

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.book_infos_cardview, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window

         description = popupView.findViewById(R.id.modal_actv_description);

        // Drop autoCompleteOptions on layout click
         description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if(hasFocus){
                     description.showDropDown();
                 }
             }
         });
         description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String[] split = description.getText().toString().split("-");
                 int pos = Integer.parseInt(split[2])-1;
                 description.setText(locData.get(pos).getDescription());
                 room.setText(locData.get(pos).getRoom());
                 floor.setText(locData.get(pos).getFloor());
                 city.setText(locData.get(pos).getCity());
                 country.setText(locData.get(pos).getCountry());
             }
         });
         room = popupView.findViewById(R.id.modal_actv_room);

         // Drop autoCompleteOptions on layout click
         room.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if(hasFocus){
                     room.showDropDown();
                 }
             }
         });

         /**
          * Associating location elements by ids in order to Autocomplete location layouts with corresponding values
          * Kind of ugly. Could be made by server's DB system
          *
          * **/
         room.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String[] split = room.getText().toString().split("-");
                 int pos = Integer.parseInt(split[1]);
                 for (Location roomI: roomData){
                     if(pos == roomI.getRoom_id()){
                         room.setText(roomI.getRoom());
                         for( Location floorI: floorData){
                             if(floorI.getFloor_id() == roomI.getFloor_id()){
                                 floor.setText(floorI.getFloor());
                                 for(Location cityI: cityData){
                                     if(cityI.getCity_id() == floorI.getCity_id()){
                                         city.setText(cityI.getCity());
                                         for(Location countryI: countryData){
                                             if(countryI.getCountry_id() == cityI.getCountry_id()){
                                                 country.setText(countryI.getCountry());
                                             }
                                         }
                                     }
                                 }
                             }
                         }
                     }
                 }
             }
         });
         floor = popupView.findViewById(R.id.modal_actv_floor);

        // Drop autoCompleteOptions on layout click
         floor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    floor.showDropDown();
                }
            }
         });
         city = popupView.findViewById(R.id.modal_actv_city);

        // Drop autoCompleteOptions on layout click
         city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    city.showDropDown();
                }
            }
         });
         country = popupView.findViewById(R.id.modal_actv_country);

        // Drop autoCompleteOptions on layout click
         country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    country.showDropDown();
                }
            }
         });
         String options[] = {"APIkey="+APIkey+"&action=locations&value=none",
                "APIkey="+APIkey+"&action=rooms&value=none",
                "APIkey="+APIkey+"&action=floors&value=none",
                "APIkey="+APIkey+"&action=cities&value=none",
                "APIkey="+APIkey+"&action=countries&value=none"};

         for(int i=0; i<5; i++){
             String[] URLString = {Constants.URL_GET_DESTINATION, options[i], "POST", "application/x-www-form-urlencoded"};
             new autoCompleteSuggestions().execute(URLString);
         }

         Button buttonEdit = popupView.findViewById(R.id.modal_btn_send);

         // Send exemplary new location to server
         buttonEdit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                if(description.getText().toString() != "" && room.getText().toString() != ""
                        && floor.getText().toString() != "" && city.getText().toString() != "" && country.getText().toString() != ""){
                    String[] URLString7 = {Constants.URL_POST_DESTINATION, "action=changeLocation&APIkey="+APIkey+"&userID="+UserID+
                            "&exemplary_id="+exempID+"&country="+country.getText().toString()+
                            "&city="+city.getText().toString()+"&floor="+floor.getText().toString()+
                            "&room="+room.getText().toString()+"&location_description="+description.getText().toString(), "POST", "application/x-www-form-urlencoded"};
                    new simplePostRequests().execute(URLString7);
                }
             }
         });

         //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });

    }

    /**
     * Get DB location values (different tables) to hydrate the autoCompleteTextViews
     *
     * **/
    public class autoCompleteSuggestions extends HttpPostRequest {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("Erreur")) {
                countTimesCalled++;
                try {
                    JSONArray json = new JSONArray(s);
                    for(int i = 0; i<json.length(); i++){
                        Location location = new Location().hydrateFromJson(json.getJSONObject(i));
                        if(location.getLocation_id() != 0){
                            locData.add(location);
                        }else if(location.getRoom_id() != 0){
                            roomData.add(location);
                        }else if(location.getFloor_id() != 0){
                            floorData.add(location);
                        }else if(location.getCity_id() != 0){
                            cityData.add(location);
                        }else if(location.getCountry_id() != 0){
                            countryData.add(location);
                        }
                    }
                    switch(countTimesCalled){
                        case 1: populateDescription(); break;
                        case 2: populateRoom(); break;
                        case 3: populateFloor(); break;
                        case 4: populateCity(); break;
                        case 5: populateCountry(); countTimesCalled = 0; break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void populateDescription(){
        String[] locations = new String[locData.size()];
        for(int i = 0; i<locData.size(); i++){
            locations[i] = locData.get(i).getDescription()+" - "+locData.get(i).getRoom()+"-"+String.valueOf(locData.get(i).getLocation_id());
        }
        ArrayAdapter locationAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, locations);
        description.setAdapter(locationAdapter);
    }
    private void populateRoom(){
        String[] rooms = new String[roomData.size()];
        for(int i = 0; i<roomData.size(); i++){
            rooms[i] = roomData.get(i).getRoom()+"-"+String.valueOf(roomData.get(i).getRoom_id());
        }
        ArrayAdapter roomAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, rooms);
        room.setAdapter(roomAdapter);
    }
    private void populateFloor(){
        String[] floors = new String[floorData.size()];
        for(int i = 0; i<floorData.size(); i++){
            floors[i] = floorData.get(i).getFloor();
        }
        ArrayAdapter FloorAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, floors);
        floor.setAdapter(FloorAdapter);
    }
    private void populateCity(){
        String[] cities = new String[cityData.size()];
        for(int i = 0; i<cityData.size(); i++){
            cities[i] = cityData.get(i).getCity();
        }
        ArrayAdapter cityAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, cities);
        city.setAdapter(cityAdapter);
    }
    private void populateCountry(){
        String[] countries = new String[countryData.size()];
        for(int i = 0; i<countryData.size(); i++){
            countries[i] = countryData.get(i).getCountry();
        }
        ArrayAdapter countryAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, countries);
        country.setAdapter(countryAdapter);
    }

    // Send new location
    public class simplePostRequests extends HttpPostRequest{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(context)) {
                Toast.makeText(context, R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            popupWindow.dismiss();
            Toast.makeText(context, "Modifié !", Toast.LENGTH_LONG).show();
        }
    }

}
