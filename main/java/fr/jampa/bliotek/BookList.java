package fr.jampa.bliotek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.jampa.bliotek.classes.Book;
import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.BookAdapter;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.Constants;
import fr.jampa.bliotek.resources.HttpPostRequest;
import fr.jampa.bliotek.resources.OnLoadMoreListener;

public class BookList extends AppCompatActivity {

    Spinner researchBy;
    EditText searchBar;
    BookAdapter bookAdapter;
    RecyclerView recyclerView;
    public List<Book> books;
    int UserID, bookCount, booksModulo, nbTimesCalled, nbPossibleCalls;
    String APIkey, activity;
    boolean fromDBcount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_activity);

        UserManager UserManag = new UserManager(this);
        User user = UserManag.getUserInfos();
        UserID = user.getUser_id();
        APIkey = user.getUser_APIkey();

        books = new ArrayList<>();
        nbTimesCalled = new Integer(0);
        nbPossibleCalls = new Integer(0);

        searchBar = findViewById(R.id.bookList_et_search);
        researchBy = (Spinner) findViewById(R.id.bookList_spin_search);
        ArrayAdapter<CharSequence> researchOptions = ArrayAdapter.createFromResource(this, R.array.researchBy, android.R.layout.simple_spinner_item);
        researchOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        researchBy.setAdapter(researchOptions);


        Intent intent = getIntent();
        activity = intent.getExtras().getString("activity");
        if(!activity.equals("booklist")){
            LinearLayout header = (LinearLayout) findViewById(R.id.bookList_lyt_header);
            header.setVisibility(View.GONE);
            searchMaker();
        }else{
            populateRecycler();
        }

    }

    public void populateRecycler(){
        String[] URLString = {Constants.URL_GET_DESTINATION, "action=countBooks&APIkey=" + APIkey + "&value=none", "POST", "application/x-www-form-urlencoded"};

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    clearRecycler();
                    searchMaker();
                }else{
                    clearRecycler();
                    new DBcount().execute(URLString);
                }
            }
        });
        if(searchBar.getText().toString().equals("")){
            new DBcount().execute(URLString);
        }
    }

    /**
     * SearchBar and Spinner options
     * **/
    public void searchMaker(){
        String by = new String();
        String value = searchBar.getText().toString();
        switch (researchBy.getSelectedItemPosition()){
            case 0:
                if(!activity.equals("booklist")){
                    by = activity;
                    value = String.valueOf(UserID);
                    break;
                }else{
                    by = "title"; break;
                }
            case 1: by = "author"; break;
            case 2: by = "editor"; break;
        }
        String[] URLString = {Constants.URL_GET_DESTINATION, "action=bookby&APIkey=" + APIkey + "&by="+by+"&value="+value, "POST", "application/x-www-form-urlencoded"};
        new InitiateList().execute(URLString);
    }

    public void clearRecycler(){
        /**
        * When searchBar is filled with anything, we need to empty the recyclerView to make room for query results
         * May not be needed as a function if only called in one place...
        * **/
        books.clear();
        recyclerView.clearOnScrollListeners();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * Hydrate book list depending on Intent Extra to know what kind of List is requested
     * **/
    private class InitiateList extends HttpPostRequest {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("Erreur")) {
                try{
                    JSONArray json = new JSONArray(s); // Books from DB
                    for(int i = 0;i < json.length(); i++){
                        Book book = new Book().hydrateFromJson(json.getJSONObject(i));
                        books.add(book);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
                recyclerView = (RecyclerView) findViewById(R.id.bookList_rv_books);
                recyclerView.setLayoutManager(new LinearLayoutManager(BookList.this));
                bookAdapter = new BookAdapter(recyclerView, books, BookList.this);
                recyclerView.setAdapter(bookAdapter);

                if(fromDBcount) {
                    bookAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            String value = "";
                            int temp = (nbTimesCalled + 1) * 20;
                            if (nbTimesCalled == nbPossibleCalls) {
                                Toast.makeText(BookList.this, "Fin de la liste", Toast.LENGTH_LONG).show();
                            } else {
                                if (nbTimesCalled == 0) {
                                    value = "20-39";
                                } else {
                                    value = String.valueOf(temp + "-" + (temp + 19));
                                }
                                String[] URLString = {Constants.URL_GET_DESTINATION, "action=books&APIkey=" + APIkey + "&value=" + value, "POST", "application/x-www-form-urlencoded"};
                                new LoadMore().execute(URLString);
                            }
                        }
                    });
                    fromDBcount = false;
                }
            }
        }
    }
    /**
     * Load more books on ScrollDown
     * Conditions in BookAdapter class
     * **/
    public class LoadMore extends HttpPostRequest {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("Erreur")){
                books.add(null);
                bookAdapter.notifyItemInserted(books.size() - 1);
                books.remove(books.size() - 1);
                bookAdapter.notifyItemRemoved(books.size());
                try{
                    JSONArray json = new JSONArray(s);
                    for (int i = 0; i < json.length(); i++) {
                        Book book = new Book().hydrateFromJson(json.getJSONObject(i));
                        books.add(book);
                    }
                }catch(
                        JSONException e)
                {
                    e.printStackTrace();
                }
                bookAdapter.notifyDataSetChanged();
                bookAdapter.setLoaded();
                nbTimesCalled++;
            }else{
                Toast.makeText(BookList.this, "Erreur", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Get number of books in DB
     * **/
    public class DBcount extends HttpPostRequest{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!Connectivity.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_connection_toast, Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("Erreur")){

                bookCount = Integer.parseInt(s);
                nbPossibleCalls = Math.round((bookCount-20)/20);
                booksModulo = bookCount%20;
                if(booksModulo != 0){ nbPossibleCalls++;}

                fromDBcount = true;
                String[] URLString = {Constants.URL_GET_DESTINATION, "action=books&APIkey=" + APIkey + "&value=0-19", "POST", "application/x-www-form-urlencoded"};
                new InitiateList().execute(URLString);
            }
        }
    }
}

