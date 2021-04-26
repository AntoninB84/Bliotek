package fr.jampa.bliotek.classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {

    private int user_id, book_id, comment_timestamp;
    private String user_name, comment_content;
    private Float rating;


    public Comment(){

    }

    /**
     *  Turning HttpRequest JSON to Comment Object
     */
    public Comment hydrateFromJson(JSONObject json){
        try{

            //book_id = json.getInt("book_id");
            user_id = json.getInt("user_id");
            comment_timestamp = json.getInt("comment_timestamp");
            user_name = json.getString("user_name");
            comment_content = json.getString("comment_content");
            rating = Float.parseFloat(json.getString("rating"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return this;
    }


    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getComment_timestamp() {
        return comment_timestamp;
    }

    public void setComment_timestamp(int comment_timestamp) {
        this.comment_timestamp = comment_timestamp;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }


}
