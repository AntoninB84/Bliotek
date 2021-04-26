package fr.jampa.bliotek;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.jampa.bliotek.classes.Book;
import fr.jampa.bliotek.classes.Location;
import fr.jampa.bliotek.classes.LocationManager;
import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.Constants;
import fr.jampa.bliotek.resources.HttpPostRequest;
import fr.jampa.bliotek.resources.PopUpModal;

import static java.lang.Integer.parseInt;

/**
 * Adding book to server DB
 * Getting available locations from server DB
 * Getting already registered book_title, authors and editor
 * **/

public class AddBook extends AppCompatActivity {

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    LinearLayout lin_isbn10;
    EditText isbn10, isbn13, pages, description;
    AutoCompleteTextView title, editor, location_description, country, city, floor, room;
    MultiAutoCompleteTextView author;
    List<Location> locData, roomData, floorData, cityData, countryData;
    int countTimesCalled;
    Spinner language;
    Button camera, send;
    ProgressDialog pDialog;
    LocationManager locationManager;
    User user;
    String value, APIkey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book_activity);
        locationManager = new LocationManager(getApplicationContext()); // Only set to get LAST INSERTED/CHOOSEN book location
        UserManager UserManag = new UserManager(this);
        user = UserManag.getUserInfos();
        APIkey = user.getUser_APIkey();

        lin_isbn10 = findViewById(R.id.linlyt_isbn10);
        isbn10 = findViewById(R.id.et_isbn10);
        isbn13 = findViewById(R.id.et_isbn13);
        isbn13.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(isbn13.getText().toString().equals("")){
                    camera.setText(R.string.addbook_camera_btn);
                }else{
                    camera.setText(R.string.addbook_search_isbn);
                }
            }
        });
        title = findViewById(R.id.acet_book_field);
        author = findViewById(R.id.macet_book_author);
        editor = findViewById(R.id.acet_book_editor);
        language = findViewById(R.id.et_book_language);
        value = "fr";
        pages = findViewById(R.id.et_book_pages);
        description = findViewById(R.id.et_book_description);
        language.setOnItemSelectedListener(getSelectedLanguage);
        country = findViewById(R.id.et_book_location_country);
        city = findViewById(R.id.et_book_location_city);
        floor = findViewById(R.id.et_book_location_floor);
        room = findViewById(R.id.et_book_location_room);
        location_description = findViewById(R.id.et_book_location_description);
        populateSpinner();

        send = (Button)findViewById(R.id.addBook_btn_send);
        send.setOnClickListener(sendBook);
        camera = (Button)findViewById(R.id.btn_call_camera);
        camera.setOnClickListener(callCamera);
        textInputListener();

        Intent intent = getIntent();
        // Get the extras (if there are any) from the CameraActivity
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("barcode")) {
                String barcode = extras.getString("barcode");
                confirmationISBN(barcode);
            }
        }
    }
    private void populateSpinner(){
       /**
        * Populating spinner with local arrays from resources
        * **/
        ArrayAdapter<CharSequence> langAdapter = ArrayAdapter.createFromResource(this, R.array.languages_labels, R.layout.white_spinner);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(langAdapter);

        String options[] = {"APIkey="+APIkey+"&action=locations&value=none",
                "APIkey="+APIkey+"&action=rooms&value=none",
                "APIkey="+APIkey+"&action=floors&value=none",
                "APIkey="+APIkey+"&action=cities&value=none",
                "APIkey="+APIkey+"&action=countries&value=none"};

        for(int i=0; i<5; i++){
            String[] URLString = {Constants.URL_GET_DESTINATION, options[i], "POST", "application/x-www-form-urlencoded"};
            new locationAutoCompleteSuggestions().execute(URLString);
        }
        Location lastLocation = locationManager.getLastLocation();
        country.setText(lastLocation.getCountry());
        city.setText(lastLocation.getCity());
        floor.setText(lastLocation.getFloor());
        room.setText(lastLocation.getRoom());
        location_description.setText(lastLocation.getDescription());

        countTimesCalled = 0;
        locData = new ArrayList<>();
        roomData = new ArrayList<>();
        floorData = new ArrayList<>();
        cityData = new ArrayList<>();
        countryData = new ArrayList<>();

        location_description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    location_description.showDropDown();
                }
            }
        });
        location_description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] split = location_description.getText().toString().split("-");
                int pos = Integer.parseInt(split[2])-1;
                location_description.setText(locData.get(pos).getDescription());
                room.setText(locData.get(pos).getRoom());
                floor.setText(locData.get(pos).getFloor());
                city.setText(locData.get(pos).getCity());
                country.setText(locData.get(pos).getCountry());
            }
        });
        room.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    room.showDropDown();
                }
            }
        });
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
        floor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    floor.showDropDown();
                }
            }
        });
        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    city.showDropDown();
                }
            }
        });
        country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    country.showDropDown();
                }
            }
        });
    }
    private AdapterView.OnItemSelectedListener getSelectedLanguage = new AdapterView.OnItemSelectedListener() {
        /** DB value is not display value ( DB -> fr, display -> Français ) **/
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            value = getResources().getStringArray(R.array.languages_values)[position];
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private void textInputListener(){
        title.addTextChangedListener(new GenericTextWatcher(title));
        author.addTextChangedListener(new GenericTextWatcher(author));
        editor.addTextChangedListener(new GenericTextWatcher(editor));
    }

    /**
     * Constructing URL with book infos to send to server
     * **/
    private View.OnClickListener sendBook = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isbn13.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_isbn, Toast.LENGTH_LONG).show();
            }else if(title.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_title, Toast.LENGTH_LONG).show();
            }else if(editor.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_editor, Toast.LENGTH_LONG).show();
            }else if(author.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_author, Toast.LENGTH_LONG).show();
            }else if(location_description.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_location, Toast.LENGTH_LONG).show();
            }else if(room.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_room, Toast.LENGTH_LONG).show();
            }else if(floor.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_floor, Toast.LENGTH_LONG).show();
            }else if(city.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_city, Toast.LENGTH_LONG).show();
            }else if(country.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), R.string.addbook_toast_country, Toast.LENGTH_LONG).show();
            }else {
                // Put selected/inserted location into local DB
                Location location = new Location();
                location.setDescription(String.valueOf(location_description.getText().toString()));
                location.setRoom(room.getText().toString());
                location.setFloor(floor.getText().toString());
                location.setCity(city.getText().toString());
                location.setCountry(country.getText().toString());
                locationManager.changeLastLocation(location);

                // Kind of way ... might need to fix httpPostRequests
                String[] URLBookString = {Constants.URL_POST_DESTINATION, "action=addbook&APIkey=" + user.getUser_APIkey() +
                        "&isbn10=" + isbn10.getText() +
                        "&isbn13=" + isbn13.getText() +
                        "&title=" + title.getText() +
                        "&editor=" + editor.getText() +
                        "&author=" + author.getText() +
                        "&language=" + value +
                        "&pages=" + pages.getText() +
                        "&description=" + description.getText() +
                        "&published_timestamp=" + "" +
                        "&country=" + country.getText().toString() +
                        "&city=" + city.getText().toString() +
                        "&floor=" + floor.getText().toString() +
                        "&room=" + room.getText().toString() +
                        "&location_description=" + location_description.getText() +
                        "&userID=" + user.getUser_id(),
                        "POST",
                        "application/x-www-form-urlencoded"};
                new bliotekSend().execute(URLBookString);
            }
        }
    };
    private boolean hasCameraPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
    }
    private View.OnClickListener callCamera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isbn13.getText().toString().equals("")){
                if (hasCameraPermission()) {
                    enableCamera();
                } else {
                    requestCameraPermission();
                }
            }else{
                confirmationISBN(String.valueOf(isbn13.getText()));
            }
        }
    };
    private void enableCamera(){
        /** Starting CameraActivity **/
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * Barcode detected, asking isbn confirmation
     * **/
    private void confirmationISBN(String barcode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Isbn :")
                .setMessage(barcode)
                .setPositiveButton("Rechercher", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //On positive click, GOOGLE API request
                        isbn13.setText(barcode);
                        String[] URLString = {"https://www.googleapis.com/books/v1/volumes?q=isbn:"+barcode+"&key="+Constants.GOOGLE_API_KEY, "", "GET", "application/x-www-form-urlencoded"};
                        new isbnGoogleRequester().execute(URLString);
                    }
                })
                .setNegativeButton("Réessayer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableCamera();
                    }
                })
                .show();
    }
    /**
     * Isbn confirmed, asking google for infos
     * **/
    private class isbnGoogleRequester extends HttpPostRequest {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(Connectivity.isConnected(getApplicationContext())==true) {
                pDialog = new ProgressDialog(AddBook.this);
                pDialog.setMessage("Veuillez patienter...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }else{
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("Erreur")){
                try {
                    String in_isbn10 = null;

                    /** Google Book Structure ( partial )
                     *  {   "kind",
                     *      "totalItems",
                     *      "items": [
                     *          {   "kind",
                     *              "id",
                     *              "etag",
                     *              "selflink",
                     *              "volumeInfo": {
                     *                  "title",
                     *                  "authors": ["..."],
                     *                  "publishedDate",
                     *                  "description",
                     *                  "industryIdentifiers": [ {"type", "identifiers"},{"type", "identifiers"}],
                     *                  "readingModes": { "text", "image"},
                     *                  "pageCount",
                     *                  "printType",
                     *                  "categories",
                     *                  ....,
                     *                  "Language"
                     *                  ...
                     * **/

                    JSONObject json = new JSONObject(s);
                    int totalItems = json.getInt("totalItems");
                    if(totalItems == 0){
                        // Code 200 but isbn was not found on google API
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddBook.this);
                        builder.setTitle("Erreur :")
                                .setMessage("Aucune correspondance trouvée...")
                                .setPositiveButton("Ok", null)
                                .show();
                    }else {
                        JSONArray items = json.getJSONArray("items");
                        JSONObject itemsArray = items.getJSONObject(0);
                        JSONObject volumeInfo = itemsArray.getJSONObject("volumeInfo");
                        if (volumeInfo.has("title")) {
                            String in_title = volumeInfo.getString("title");
                            title.setText(in_title);
                        }
                        if (volumeInfo.has("authors")) {
                            String in_authors = volumeInfo.getString("authors");
                            in_authors = in_authors.substring(2, in_authors.length() - 2);
                            in_authors = in_authors.replace("\"", "");
                            author.setText(in_authors);
                        }
                        /*if(volumeInfo.has("publishedDate")){
                        String in_published = volumeInfo.getString("publishedDate");
                        date.setText(in_published);
                        LocalDate localDateTime = LocalDate.parse(in_published);
                        }*/
                        if (volumeInfo.has("description")) {
                            String in_description = volumeInfo.getString("description");
                            description.setText(in_description);
                        }
                        if (volumeInfo.has("pageCount")) {
                            int in_pages = volumeInfo.getInt("pageCount");
                            pages.setText(String.valueOf(in_pages));
                        }
                        if (volumeInfo.has("language")) {
                            String in_language = volumeInfo.getString("language");
                            int len = getResources().getStringArray(R.array.languages_values).length;
                            for (int i = 0; i < len; i++) {
                                if (in_language.equals(getResources().getStringArray(R.array.languages_values)[i])) {
                                    language.setSelection(i);
                                }
                            }
                        }
                        if (volumeInfo.has("publisher")) {
                            String in_editor = volumeInfo.getString("publisher");
                            editor.setText(in_editor);
                        }

                        JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                        JSONObject inIdArray = industryIdentifiers.getJSONObject(0);
                        String checkIsbn = inIdArray.getString("type");
                        if (checkIsbn.equals("ISBN_10")) {
                            in_isbn10 = inIdArray.getString("identifier");
                            isbn10.setText(in_isbn10);
                            lin_isbn10.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    pDialog.dismiss();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Isbn :")
                        .setMessage("Une erreur s'est produite...")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }
    }
    /**
     * Sending book infos to web server
     * Handling server response : adding book, or asking about exemplary, or fails ....
     * **/
    private class bliotekSend extends HttpPostRequest{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(Connectivity.isConnected(getApplicationContext())==true) {
                pDialog = new ProgressDialog(AddBook.this);
                pDialog.setMessage("Veuillez patienter...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }else{
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            if(!s.equals("Erreur")){
                try {
                    JSONObject json = new JSONObject(s); // Server response
                    if(json.has("book")&&json.has("exemplary")&&json.has("location")&&json.has("booksDB")){
                        // Going to addExemplary activity
                        Intent goNext = new Intent(getApplicationContext(), AddExemplary.class);
                        //goNext.putExtra("booksInfos", (Parcelable) json);
                        goNext.putExtra("booksInfos",  String.valueOf(json));

                        startActivity(goNext);
                        finish();
                    }else{
                        Intent goNext = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(goNext);
                        Toast.makeText(getApplicationContext(), "Succès", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    pDialog.dismiss();
                }
            }else{
                builder.setTitle("Isbn :")
                        .setMessage("Une erreur s'est produite...")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        }
    }
    private class autoCompleteSuggestions extends HttpPostRequest{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("Erreur")){
                try{
                    JSONArray json = new JSONArray(s);
                    JSONObject test = json.getJSONObject(0);
                    if(test.has("book_id")){
                        String[] titles = new String[json.length()];
                        for(int i = 0; i < json.length(); i++){
                            Book book = new Book().hydrateFromJson(json.getJSONObject(i));
                            titles[i] = book.getTitle();
                        }
                        ArrayAdapter titleAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, titles);
                        title.setAdapter(titleAdapter);
                        title.setThreshold(1);
                    }else if(test.has("author")){
                        String[] authors = new String[json.length()];
                        for(int i = 0; i < json.length(); i++){
                            JSONObject item = json.getJSONObject(i);
                            authors[i] = item.getString("author");
                        }
                        ArrayAdapter authorAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, authors);
                        author.setAdapter(authorAdapter);
                        author.setThreshold(1);
                        author.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                    }else{
                        String[] editors = new String[json.length()];
                        for(int i = 0; i < json.length(); i++){
                            JSONObject item = json.getJSONObject(i);
                            editors[i] = item.getString("editor");
                        }
                        ArrayAdapter editorAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, editors);
                        editor.setAdapter(editorAdapter);
                        editor.setThreshold(1);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public class locationAutoCompleteSuggestions extends HttpPostRequest {
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
        ArrayAdapter locationAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locations);
        location_description.setAdapter(locationAdapter);
    }
    private void populateRoom(){
        String[] rooms = new String[roomData.size()];
        for(int i = 0; i<roomData.size(); i++){
            rooms[i] = roomData.get(i).getRoom()+"-"+String.valueOf(roomData.get(i).getRoom_id());
        }
        ArrayAdapter roomAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, rooms);
        room.setAdapter(roomAdapter);
    }
    private void populateFloor(){
        String[] floors = new String[floorData.size()];
        for(int i = 0; i<floorData.size(); i++){
            floors[i] = floorData.get(i).getFloor();
        }
        ArrayAdapter FloorAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, floors);
        floor.setAdapter(FloorAdapter);
    }
    private void populateCity(){
        String[] cities = new String[cityData.size()];
        for(int i = 0; i<cityData.size(); i++){
            cities[i] = cityData.get(i).getCity();
        }
        ArrayAdapter cityAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cities);
        city.setAdapter(cityAdapter);
    }
    private void populateCountry(){
        String[] countries = new String[countryData.size()];
        for(int i = 0; i<countryData.size(); i++){
            countries[i] = countryData.get(i).getCountry();
        }
        ArrayAdapter countryAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countries);
        country.setAdapter(countryAdapter);
    }
    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString();
            String options = "";
            if (!input.equals("")) {
                switch (view.getId()) {
                    case R.id.acet_book_field:
                        options = "action=title&APIkey=" + APIkey + "&value="+input;
                        break;
                    case R.id.macet_book_author:
                        options = "action=author&APIkey=" + APIkey + "&value="+input;
                        break;
                    case R.id.acet_book_editor:
                        options = "action=editor&APIkey=" + APIkey + "&value="+input;
                        break;
                }
                String[] URLString = {Constants.URL_GET_DESTINATION, options, "POST", "application/x-www-form-urlencoded"};
                new autoCompleteSuggestions().execute(URLString);
            }
        }
    }
}
