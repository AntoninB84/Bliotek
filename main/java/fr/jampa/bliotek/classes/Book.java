package fr.jampa.bliotek.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {

    private  int book_id, published_timestamp;
    private String title, author, editor, language, description, isbn10, isbn13, pages;
    SQLiteDataBaseHelper db;

    public Book(){
        // Empty for now
    }

    /**
     *  No book in local DB
     * **/
    /*public Book hydrateFromCursor(Cursor DataFromDB){
        book_id = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.BOOK_ID_FIELD));
        isbn10 = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.BOOK_ISBN10_FIELD));
        isbn13 = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.BOOK_ISBN13_FIELD));
        title = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.BOOK_TITLE_FIELD));
        editor = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.BOOK_EDITOR_ID_FIELD));
        published_timestamp = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.BOOK_PUBLISHED_FIELD));
        pages = DataFromDB.getInt(DataFromDB.getColumnIndexOrThrow(db.BOOK_PAGES_FIELD));
        language = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.BOOK_LANGUAGE_ID_FIELD));
        description = DataFromDB.getString(DataFromDB.getColumnIndexOrThrow(db.BOOK_DESCRIPTION_FIELD));
        return this;
    }*/

    /**
     *  Turning JSON Object to Book Object from HttpRequests
     * **/
    public Book hydrateFromJson(JSONObject json){
        try{
            book_id = json.getInt("book_id");
            isbn10 = json.getString("isbn10");
            isbn13 = json.getString("isbn13");
            title = json.getString("title");
            author = json.getString("author");
            editor = json.getString("editor");
            //published_timestamp = json.getInt("published_timestamp"); // Ne gère pas NULL
            pages = json.getString("pages");
            language = json.getString("language");
            description = json.getString("description");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return this;
    }

    /**
     *  Turning Book Object to JSON Object
     */

    public JSONObject bookToJson(){
        JSONObject book = new JSONObject();
        try{
            book.put("isbn10", isbn10);
            book.put("isbn13", isbn13);
            book.put("title", title);
            book.put("author", author);
            book.put("editor", editor);
            book.put("published_timestamp", published_timestamp);
            book.put("pages", pages);
            book.put("language", language);
            book.put("description", description);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return book;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public int getPublished_timestamp() {
        return published_timestamp;
    }

    public void setPublished_timestamp(int published_timestamp) {
        this.published_timestamp = published_timestamp;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
