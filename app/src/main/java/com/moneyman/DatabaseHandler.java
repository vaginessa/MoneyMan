package com.moneyman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moneyman.models.ModelItem;

import java.util.List;

/**
 * Created by chatRG.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, CustomConstants.DATABASE_NAME, null, CustomConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LIST_TABLE = "CREATE TABLE " + CustomConstants.TABLE_NAME + "("
                + CustomConstants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CustomConstants.KEY_AMOUNT + " TEXT NOT NULL, "
                + CustomConstants.KEY_DESC + " TEXT NOT NULL, "
                + CustomConstants.KEY_DATE + " TEXT NOT NULL, "
                + CustomConstants.KEY_TYPE + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CustomConstants.TABLE_NAME);
        onCreate(db);
    }

    public void addListItem(ModelItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CustomConstants.KEY_AMOUNT, item.getAmount());
        values.put(CustomConstants.KEY_DESC, item.getDesc());
        values.put(CustomConstants.KEY_DATE, item.getDate());
        values.put(CustomConstants.KEY_TYPE, item.getTransaction());
        db.insert(CustomConstants.TABLE_NAME, null, values);
        db.close();
    }

    public void readFromDB(List<ModelItem> itemList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(CustomConstants.TABLE_NAME,
                null, null, null, null, null,
                "date(" + CustomConstants.KEY_DATE + ") DESC, " +
                        CustomConstants.KEY_ID + " DESC Limit 10000");

        try {
            while (cursor.moveToNext()) {
                ModelItem item = new ModelItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(CustomConstants.KEY_ID)));
                item.setAmount(cursor.getString(cursor.getColumnIndex(CustomConstants.KEY_AMOUNT)));
                item.setDesc(cursor.getString(cursor.getColumnIndex(CustomConstants.KEY_DESC)));
                item.setDate(cursor.getString(cursor.getColumnIndex(CustomConstants.KEY_DATE)));
                item.setTransaction(cursor.getString(cursor.getColumnIndex(CustomConstants.KEY_TYPE)));
                itemList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void updateTransaction(ModelItem item) {
        ContentValues cv = new ContentValues();
        cv.put(CustomConstants.KEY_ID, item.getId());
        cv.put(CustomConstants.KEY_AMOUNT, item.getAmount());
        cv.put(CustomConstants.KEY_DESC, item.getDesc());
        cv.put(CustomConstants.KEY_DATE, item.getDate());
        cv.put(CustomConstants.KEY_TYPE, item.getTransaction());
        this.getWritableDatabase()
                .update(CustomConstants.TABLE_NAME, cv, "id = ?", new String[]{item.getId() + ""});
    }

    public void deleteNote(int id) {
        this.getWritableDatabase()
                .delete(CustomConstants.TABLE_NAME, "id = ?", new String[]{id + ""});
    }

    public void deleteAllNote() {
        this.getWritableDatabase()
                .delete(CustomConstants.TABLE_NAME, null, null);
    }
}