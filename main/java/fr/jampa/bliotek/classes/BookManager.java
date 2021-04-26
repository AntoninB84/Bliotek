package fr.jampa.bliotek.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookManager {
    Context myContext;
    SQLiteDataBaseHelper dbHelper;
    SQLiteDatabase db;
    Book book;

    /**
     * This manager is not currently needed.
     * It might get useful if I'm intending to store books in local DB
     * **/

    public BookManager(Context context){
        myContext = context;
        dbHelper = new SQLiteDataBaseHelper(myContext);
        db = dbHelper.getWritableDatabase();
        book = new Book();
    }

    public List<Book> getBooks(){
        String query = "SELECT * FROM "+dbHelper.BOOK_TABLE_NAME;
        try(Cursor result = db.rawQuery(query, null)) {
            List<Book> array = new ArrayList<Book>();
            int i = 0;
            while (result.moveToNext()) {
                //array.add(book.hydrateFromCursor(result));
                i++;
            }
            if(i>0) {
                return array;
            }else{
                return null;
            }
        }
    }
    public void addBook(JSONObject fromWeb){
        book.hydrateFromJson(fromWeb);
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.BOOK_ISBN10_FIELD, book.getIsbn10());
        contentValues.put(dbHelper.BOOK_ISBN13_FIELD, book.getIsbn13());
        contentValues.put(dbHelper.BOOK_TITLE_FIELD, book.getTitle());
        contentValues.put(dbHelper.BOOK_EDITOR_FIELD, book.getEditor());
        contentValues.put(dbHelper.BOOK_LANGUAGE_FIELD, book.getLanguage());
        contentValues.put(dbHelper.BOOK_PAGES_FIELD, book.getPages());
        contentValues.put(dbHelper.BOOK_PUBLISHED_FIELD, book.getPublished_timestamp());
        contentValues.put(dbHelper.BOOK_DESCRIPTION_FIELD, book.getDescription());
        db.insert(dbHelper.BOOK_TABLE_NAME, null, contentValues);
    }
    public void deleteBooks(){
        db.delete(dbHelper.BOOK_TABLE_NAME, dbHelper.BOOK_ID_FIELD, null);
    }

}
