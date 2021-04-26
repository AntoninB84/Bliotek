package fr.jampa.bliotek;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.jampa.bliotek.classes.Book;
import fr.jampa.bliotek.classes.Comment;
import fr.jampa.bliotek.resources.CommentAdapter;
import fr.jampa.bliotek.resources.Constants;
import fr.jampa.bliotek.resources.PopUpModal;
import fr.jampa.bliotek.classes.User;
import fr.jampa.bliotek.classes.UserManager;
import fr.jampa.bliotek.resources.Connectivity;
import fr.jampa.bliotek.resources.HttpPostRequest;

public class BookInfos extends AppCompatActivity {

    int UserID;
    String APIkey, bookID, ratings;
    TextView title, author, editor, language, pages, description, isbn10, isbn13, readby, interested;
    RatingBar yourRating;
    CheckBox haveread, imInterested;
    LinearLayout exemplaries;
    CommentAdapter commentAdapter;
    List<Comment> comments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_infos_activity);

        exemplaries = findViewById(R.id.bookInfos_llyt_exemplaries);
        title = findViewById(R.id.bookInfos_tv_title);
        author = findViewById(R.id.bookInfos_tv_author);
        editor = findViewById(R.id.bookInfos_tv_editor);
        language = findViewById(R.id.bookInfos_tv_language);
        pages = findViewById(R.id.bookInfos_tv_pages);
        description = findViewById(R.id.bookInfos_tv_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        description.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                description.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        isbn10 = findViewById(R.id.bookInfos_tv_isbn10);
        isbn13 = findViewById(R.id.bookInfos_tv_isbn13);
        readby = findViewById(R.id.bookInfos_tv_readby);
        interested = findViewById(R.id.bookInfos_tv_interested);
        haveread = findViewById(R.id.bookInfos_cb_haveread);
        imInterested = findViewById(R.id.bookInfos_cb_interested);
        EditText commentArea = findViewById(R.id.bookInfos_etml_addComment);
        Button sendComment = findViewById(R.id.bookInfos_btn_sendComment);
        comments = new ArrayList<>();
        yourRating = findViewById(R.id.rb_your_rating);

        UserManager UserManag = new UserManager(this);
        User user = UserManag.getUserInfos();
        UserID = user.getUser_id();
        APIkey = user.getUser_APIkey();

        Intent intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("bookID")) {
                bookID = extras.getString("bookID");
                //Get the book infos
                String[] URLString = {Constants.URL_GET_DESTINATION, "action=book&APIkey="+APIkey+"&value="+bookID, "POST", "application/x-www-form-urlencoded"};
                new getBookInfos().execute(URLString);
                //Get the users who have read the book
                String[] URLString2 = {Constants.URL_GET_DESTINATION, "action=hasread&APIkey="+APIkey+"&value="+bookID, "POST", "application/x-www-form-urlencoded"};
                new loadWhoRead().execute(URLString2);
                //Get the users who want to read the book
                String[] URLString3 = {Constants.URL_GET_DESTINATION, "action=toread&APIkey="+APIkey+"&value="+bookID, "POST", "application/x-www-form-urlencoded"};
                new whoInterested().execute(URLString3);
                //Load the exemplaries
                String[] URLString4 = {Constants.URL_GET_DESTINATION, "action=exemplaries&APIkey="+APIkey+"&value="+bookID, "POST", "application/x-www-form-urlencoded"};
                new loadExemplaries().execute(URLString4);
                //Load the ratings
                String[] URLString5 = {Constants.URL_GET_DESTINATION, "action=ratings&APIkey="+APIkey+"&value="+bookID, "POST", "application/x-www-form-urlencoded"};
                new loadRatings().execute(URLString5);
                //Load the comments
                String[] URLString6 = {Constants.URL_GET_DESTINATION, "action=comments&APIkey="+APIkey+"&value="+bookID, "POST", "application/x-www-form-urlencoded"};
                new loadComments().execute(URLString6);

                haveread.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String boolCheck;
                        if(haveread.isChecked()){ boolCheck = "true"; }else{ boolCheck = "false"; }
                        String[] URLString = {Constants.URL_POST_DESTINATION, "action=hasread&APIkey="+APIkey+"&isChecked="+boolCheck+"&bookID="+bookID+"&userID="+UserID, "POST", "application/x-www-form-urlencoded"};
                        new simplePostRequests().execute(URLString);
                    }
                });
                imInterested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String boolCheck;
                        if(imInterested.isChecked()){ boolCheck = "true"; }else{ boolCheck = "false"; }
                        String[] URLString = {Constants.URL_POST_DESTINATION, "action=toread&APIkey="+APIkey+"&isChecked="+boolCheck+"&bookID="+bookID+"&userID="+UserID, "POST", "application/x-www-form-urlencoded"};
                        new simplePostRequests().execute(URLString);
                    }
                });
                yourRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        String[] URLString = {Constants.URL_POST_DESTINATION, "action=addrating&APIkey="+APIkey+"&value="+rating+"&bookID="+bookID+"&userID="+UserID, "POST", "application/x-www-form-urlencoded"};
                        new simplePostRequests().execute(URLString);
                    }
                });
                sendComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(commentArea.getText().toString().equals(null) || commentArea.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "Commentaire vide", Toast.LENGTH_LONG).show();
                        }
                        else{
                            String[] URLString7 = {Constants.URL_POST_DESTINATION, "action=addcomment&APIkey="+APIkey+"&user_id="+UserID+"&book_id="+bookID+"&content="+commentArea.getText().toString(), "POST", "application/x-www-form-urlencoded"};
                            new simplePostRequests().execute(URLString7);
                        }
                    }
                });
            }
        }
    }

    public class getBookInfos extends HttpPostRequest{
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
            try{
                JSONArray json = new JSONArray(s);
                Book book = new Book().hydrateFromJson(json.getJSONObject(0));
                title.setText(book.getTitle());
                author.setText(book.getAuthor());
                editor.setText(book.getEditor());
                language.setText(book.getLanguage());
                pages.setText(book.getPages()+" pages, ");
                description.setText(book.getDescription());
                isbn10.setText(book.getIsbn10());
                isbn13.setText(book.getIsbn13());
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
    public class loadExemplaries extends HttpPostRequest{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONArray json = new JSONArray(s);
                for (int j = 0; j < json.length(); j++) {
                    JSONObject exemplariesDB = json.getJSONObject(j);
                    TextView exemp = new TextView(BookInfos.this);
                    exemp.setText("Exemplaire "+(j+1)+" : "+
                            exemplariesDB.getString("description")+", "
                            +exemplariesDB.getString("room")+", "+
                            exemplariesDB.getString("floor")+", "+
                            exemplariesDB.getString("city")+", "+
                            exemplariesDB.getString("country"));
                    exemp.setTextColor(Color.WHITE);
                    exemp.setOnLongClickListener(new View.OnLongClickListener(){
                        @Override
                        public boolean onLongClick(View v) {
                            PopUpModal popUpModal = null;
                            try {
                                popUpModal = new PopUpModal(BookInfos.this, exemplariesDB.getInt("exemplary_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            popUpModal.showPopupWindow(v);
                            return false;
                        }
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 15);
                    exemp.setLayoutParams(params);
                    exemplaries.addView(exemp);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }
    public class loadWhoRead extends HttpPostRequest{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String names = new String();
            try{
                JSONArray json = new JSONArray(s);
                for(int i=0; i<json.length(); i++){
                    JSONObject name = json.getJSONObject(i);
                    names += name.getString("name")+", ";
                    if(name.getString("user_id").equals(String.valueOf(UserID))){
                        haveread.setChecked(true);
                    }
                }
                readby.setText(names);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
    public class whoInterested extends HttpPostRequest{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String names = new String();
            try{
                JSONArray json = new JSONArray(s);
                for(int i=0; i<json.length(); i++){
                    JSONObject name = json.getJSONObject(i);
                    names += name.getString("name")+", ";
                    if(name.getString("user_id").equals(String.valueOf(UserID))){
                        imInterested.setChecked(true);
                    }
                }
                interested.setText(names);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
    public class simplePostRequests extends HttpPostRequest{
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
            if(s.contains("Comment")){
                Toast.makeText(getApplicationContext(), "Commenté !", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        }
    }
    public class loadRatings extends HttpPostRequest{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ratings = s;
            Float sum = 0.0f;
            try{
                JSONArray json = new JSONArray(s);
                for(int i=0; i<json.length(); i++){
                    JSONObject user = json.getJSONObject(i);
                    Float rating = Float.parseFloat(user.getString("rating"));
                    if(user.getInt("user_id") == UserID){
                        yourRating.setRating(rating);
                    }
                    sum += rating;
                }
                Float average = sum/json.length();
                RatingBar averageRating = findViewById(R.id.rb_bookInfos_avg_rating);
                averageRating.setRating(average);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
    public class loadComments extends HttpPostRequest{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONArray json = new JSONArray(s);
                for(int i=0; i<json.length(); i++){
                    Comment comment = new Comment().hydrateFromJson(json.getJSONObject(i));
                    comments.add(comment);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            RecyclerView commentsRecyclerView = (RecyclerView) findViewById(R.id.bookInfos_comments_recyclerView);
            commentsRecyclerView.setLayoutManager(new LinearLayoutManager(BookInfos.this));
            commentAdapter = new CommentAdapter(commentsRecyclerView, comments, BookInfos.this);
            commentsRecyclerView.setAdapter(commentAdapter);
        }
    }

}
