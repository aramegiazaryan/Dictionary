package com.example.aram.dictionary;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "MY.DB";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE dictionary(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data TEXT, " +
                "letter TEXT, " +
                "word TEXT, " +
                "translation TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insert(String data, String letter, String word, String translation) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("data", data);
        cv.put("letter", letter);
        cv.put("word", word);
        cv.put("translation", translation);
        long rowId = db.insert("dictionary", null, cv);
        db.close();
        return rowId;

    }

    public int delete(String word) {
        SQLiteDatabase db = getWritableDatabase();
        int affectedRows = db.delete("dictionary", "word=?", new String[]{word});
       // String k="delete from dictionary where word=='"+word+"'";
       // db.execSQL("delete from dictionary where word=='"+word+"'");
        db.close();

        return affectedRows;
    }

    public int update(String editWord,String data, String letter, String word, String translation) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("data", data);
        cv.put("letter", letter);
        cv.put("word", word);
        cv.put("translation", translation);
        int affectedRows = db.update("dictionary", cv, "word=?", new String[]{editWord});
        db.close();
        return affectedRows;
    }

    public Cursor query (String letter) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c=db.query(
                "dictionary" /* table */,
                new String[]{"word","translation"} /* columns */,
                  "letter = ?" /* where or selection */,
                new String[]{letter} /* selectionArgs i.e. value to replace ? */,
                null /* groupBy */,
                null /* having */,
                null /* orderBy */
        );
   //    db.close();
      return  c;
    }
}
