package fr.jampa.bliotek;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.Constants;
import fr.jampa.bliotek.resources.HttpPostRequest;

public class AddExemplary extends AppCompatActivity {

    ListView ListBooks;
    Button Abort, DiffBook;
    int UserID;
    ProgressDialog pDialog;
    String APIkey, infos;
    JSONArray booksDB, exempsDB, locaDB;
    JSONObject book, exemplaries, loca, inputBook, inputLoca;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exemplary_activity);

        UserManager UserManag = new UserManager(this);
        User user = UserManag.getUserInfos();
        UserID = user.getUser_id();
        APIkey = user.getUser_APIkey();

        DiffBook = (Button) findViewById(R.id.addExemp_btn_diffBook);
        DiffBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new bliotekSend().execute(UrlMaker("addbook&o=1", ""));
            }
        });
        Abort = (Button) findViewById(R.id.addExemp_btn_abort);
        Abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        ListBooks = (ListView) findViewById(R.id.addExemp_lv_books);

        /** Intent structure
         *  First 3 objects are infos about the current book
         *  Last 3 objects are arrays of books and locations found in DB
         *
         * {"book" : { book_id, isbn10, isbn13, title, author, editor, published_timestamp, pages, language, description },
         * {"exemplary" : { exemplary_id, book_id, current_location_id, added_timestamp, added_by},
         * {"location" : { location_id, country, city, floor, room, description },
         * {"booksDB" : [{book_id,....}, ...],
         * "exempsDB" : [{exemplary_id, ...}, ...],
         * "locationsDB" : [{location_id, ...}, ...]}
         *
         * **/
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null && extras.containsKey("booksInfos")){
            infos = extras.getString("booksInfos");

            try {
                JSONObject json = new JSONObject(infos);
                inputBook = json.getJSONObject("book");
                inputLoca = json.getJSONObject("location");
                if(inputBook.getString("isbn10").equals("") || inputBook.getString("isbn13").equals("") ){
                    DiffBook.setVisibility(View.VISIBLE);
                }
                booksDB = json.getJSONArray("booksDB");
                exempsDB = json.getJSONArray("exempsDB");
                locaDB = json.getJSONArray("locationsDB");
            }catch(JSONException e) {
                e.printStackTrace();
            }

            ListBooks.setAdapter(new bookDBAdapter());
            ListBooks.setOnItemClickListener(selectBook);

        }

    }
    private void goBack(){
        Intent goAffichage = new Intent(AddExemplary.this, MainActivity.class);
        startActivity(goAffichage);
        finish();
    }
    private AdapterView.OnItemClickListener selectBook = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView BookTVid = (TextView) view.findViewById(R.id.addExemp_tv_bookID);
            TextView BookTVTitle = (TextView) view.findViewById(R.id.addExemp_tv_bookTitle);

            AlertDialog.Builder builder = new AlertDialog.Builder(AddExemplary.this);
            builder.setTitle("Que souhaitez-vous faire ?")
                    .setPositiveButton("Ajouter un exemplaire", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new bliotekSend().execute(UrlMaker("addexemplary", String.valueOf(BookTVTitle.getText())));
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                        }
                    })
                    .show();
        }
    };
    private String[] UrlMaker(String action, String bookTitle){
        String URL = Constants.URL_POST_DESTINATION;
        String parameters = new String();
        try {
            if (action.equals("addexemplary")) {
                parameters =    "&bookTitle=" + bookTitle +
                                "&room=" + inputLoca.getString("room") +
                                "&description=" + inputLoca.getString("description") +
                                "&userID=" + UserID;
            } else {
                String authors = inputBook.getString("author");
                authors = authors.replace("\"", "");
                authors = authors.replace("[", "");
                authors = authors.replace("]", "");
                Log.i("Bliotest", authors);
                parameters =    "&userID=" + UserID +
                                "&isbn10=" + inputBook.getString("isbn10") +
                                "&isbn13=" + inputBook.getString("isbn13") +
                                "&title=" + inputBook.getString("title") +
                                "&author=" + authors +
                                "&editor=" + inputBook.getString("editor") +
                                "&language=" + inputBook.getString("language") +
                                "&pages=" + inputBook.getString("pages") +
                                "&description=" + inputBook.getString("description") +
                                "&published_timestamp=" + inputBook.getString("published_timestamp")+
                                "&country="+ inputLoca.getString("country")+
                                "&city="+ inputLoca.getString("city")+
                                "&floor="+ inputLoca.getString("floor")+
                                "&room="+ inputLoca.getString("room") +
                                "&location_description="+ inputLoca.getString("description");
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }

        String[] URLBliotek = {URL, "action="+action+"&APIkey="+APIkey+parameters, "POST", "application/x-www-form-urlencoded"};
        return URLBliotek;
    }

    /**
     * Custom adapter to display corresponding books as list
     * **/
    class bookDBAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return booksDB.length();
        }
        @Override
        public Object getItem(int arg0) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView title, authors, editor, exemp, bookTVid;
            View row;
            String tempString = new String();

            LayoutInflater inflater = getLayoutInflater();
            row = inflater.inflate(R.layout.add_exemp_list, null);
            bookTVid = (TextView) row.findViewById(R.id.addExemp_tv_bookID);
            title = (TextView) row.findViewById(R.id.addExemp_tv_bookTitle);
            authors = (TextView) row.findViewById(R.id.addExemp_tv_authors);
            editor = (TextView) row.findViewById(R.id.addExemp_tv_editor);
            exemp = (TextView) row.findViewById(R.id.addExemp_tv_exemps);

            try {
                book = booksDB.getJSONObject(position);
                bookTVid.setText(book.getString("book_id"));
                title.setText(book.getString("title"));
                authors.setText(book.getString("author"));
                editor.setText(book.getString("editor"));
                for (int j = 0; j < exempsDB.length(); j++) {
                    exemplaries = exempsDB.getJSONObject(j);
                    if (exemplaries.getString("book_id").equals(book.getString("book_id"))) {
                        for (int k = 0; k < locaDB.length(); k++) {
                            loca = locaDB.getJSONObject(k);
                            if (loca.getString("location_id").equals(exemplaries.getString("current_location_id"))) {
                                tempString += "Exemplaire : " +
                                        loca.getString("description") +
                                        ", " + loca.getString("room") +
                                        ", " + loca.getString("floor") +
                                        ", " + loca.getString("city") +
                                        ", " + loca.getString("country")+"\n";
                            }
                        }
                    }
                }
                exemp.setText(tempString);

            }catch(JSONException e) {
                e.printStackTrace();
            }

            return row;
        }
    }

    /**
     * Send create new book's exemplary, or new book
     * **/
    private class bliotekSend extends HttpPostRequest {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(Connectivity.isConnected(getApplicationContext())==true) {
                pDialog = new ProgressDialog(AddExemplary.this);
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
                    Log.i("Bliotest", "Ok");
                    Toast.makeText(getApplicationContext(), "Succès", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    goBack();
            }
        }
    }
}
