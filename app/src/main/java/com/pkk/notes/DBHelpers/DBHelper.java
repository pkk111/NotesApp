package com.pkk.notes.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.pkk.notes.Models.NotesModel;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_NOTES = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_BLOB = "blob";
    private static final String KEY_TIME = "time";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT," + KEY_BODY + " TEXT," + KEY_BLOB + " BLOB," + KEY_TIME + " DATE" + ");";
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(sqLiteDatabase);
    }

    public void addNote(NotesModel model) throws SQLiteException {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, model.getTitle());
        cv.put(KEY_BODY, model.getBody());
        cv.put(KEY_BLOB, model.getImage());
        cv.put(KEY_TIME, model.getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NOTES, null, cv);
        db.close();
    }

    public List<NotesModel> getAllNotes() {
        List<NotesModel> modelList = new ArrayList<NotesModel>();
        String query = "SELECT  * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                NotesModel model = new NotesModel(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(4));
                byte[] blob = cursor.getBlob(3);
                if (blob != null)
                    model.setImage(blob);
                modelList.add(model);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return modelList;
    }

    public NotesModel getNote(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[] { KEY_ID,
                        KEY_TITLE, KEY_BODY, KEY_BLOB, KEY_TIME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        NotesModel model = new NotesModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(4));
        byte[] blob = cursor.getBlob(3);
        if (blob != null)
            model.setImage(blob);
        db.close();
        return model;
    }

    public void deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public void updateNote(NotesModel model){
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, model.getTitle());
        cv.put(KEY_BODY, model.getBody());
        cv.put(KEY_BLOB, model.getImage());
        cv.put(KEY_TIME, model.getTime());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NOTES, cv, KEY_ID+"=?", new String[]{String.valueOf(model.getId())});
        db.close();
    }
}
