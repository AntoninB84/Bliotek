package fr.jampa.bliotek.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDataBaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Bliotek.db";
    public static final String USER_TABLE_NAME = "user";
        public static final String USER_ID_FIELD = "user_id";
        public static final String USER_NAME_FIELD = "name";
        public static final String USER_EMAIL_FIELD = "email";
        public static final String USER_APIKEY_FIELD = "APIkey";
        public static final String USER_SUBSCRIPTION_FIELD = "subscription_timestamp";
        public static final String USER_PRIVILEGES_FIELD = "privileges";

    public static final String BOOK_TABLE_NAME = "book";
        public static final String BOOK_ID_FIELD = "book_id";
        public static final String BOOK_ISBN10_FIELD = "isbn10";
        public static final String BOOK_ISBN13_FIELD = "isbn13";
        public static final String BOOK_TITLE_FIELD = "title";
        public static final String BOOK_AUTHOR_FIELD = "author";
        public static final String BOOK_EDITOR_FIELD = "editor";
        public static final String BOOK_LANGUAGE_FIELD = "language";
        public static final String BOOK_PAGES_FIELD = "pages";
        public static final String BOOK_PUBLISHED_FIELD = "published_timestamp";
        public static final String BOOK_DESCRIPTION_FIELD = "description";

        public static final String LAST_LOCATION_TABLE_NAME = "location";
            public static final String LAST_LOCATION_ID = "location_id";
            public static final String LAST_COUNTRY = "country";
            public static final String LAST_CITY = "city";
            public static final String LAST_FLOOR = "Floor";
            public static final String LAST_ROOM = "room";
            public static final String LAST_DESCRIPTION = "description";

    public SQLiteDataBaseHelper(Context context) { super(context, DB_NAME, null, 3); } // Change version

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "( " +
                USER_ID_FIELD+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME_FIELD+" TEXT, " +
                USER_EMAIL_FIELD+" TEXT, " +
                USER_APIKEY_FIELD+" TEXT, " +
                USER_SUBSCRIPTION_FIELD+" INTEGER, " +
                USER_PRIVILEGES_FIELD+" INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LAST_LOCATION_TABLE_NAME + "( " +
                LAST_LOCATION_ID+" INTEGER, "+
                LAST_COUNTRY+" TEXT, " +
                LAST_CITY+" TEXT, " +
                LAST_FLOOR+" TEXT, " +
                LAST_ROOM+" TEXT, " +
                LAST_DESCRIPTION+" TEXT)");
        /*ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_LOCATION_ID, 1);
        contentValues.put(LAST_COUNTRY, "test"); contentValues.put(LAST_CITY, "test"); contentValues.put(LAST_FLOOR, "test");
        contentValues.put(LAST_ROOM, "test"); contentValues.put(LAST_DESCRIPTION, "Test2");
        db.insert(LAST_LOCATION_TABLE_NAME, null, contentValues);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE "+ LAST_LOCATION_TABLE_NAME);
        //onCreate(db);
    }
}
