package com.moneyman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by chatRG.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MoneyManDB";
    private static final String TABLE_NAME = "TransList";

    private static final String KEY_ID = "id";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DESC = "description";
    private static final String KEY_DATE = "date";
    private static final String KEY_TYPE = "type";

    private Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LIST_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_AMOUNT + " TEXT NOT NULL, "
                + KEY_DESC + " TEXT NOT NULL, "
                + KEY_DATE + " TEXT NOT NULL, "
                + KEY_TYPE + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addListItem(ListItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, item.getAmount());
        values.put(KEY_DESC, item.getDesc());
        values.put(KEY_DATE, item.getDate());
        values.put(KEY_TYPE, item.getTransaction());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void readFromDB(List<ListItem> itemList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                null, null, null, null, null, "date(" + KEY_DATE + ") DESC Limit 10000");

        try {
            while (cursor.moveToNext()) {
                ListItem item = new ListItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                item.setAmount(cursor.getString(cursor.getColumnIndex(KEY_AMOUNT)));
                item.setDesc(cursor.getString(cursor.getColumnIndex(KEY_DESC)));
                item.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                item.setTransaction(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                itemList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void updateTransaction(ListItem item) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, item.getId());
        cv.put(KEY_AMOUNT, item.getAmount());
        cv.put(KEY_DESC, item.getDesc());
        cv.put(KEY_DATE, item.getDate());
        cv.put(KEY_TYPE, item.getTransaction());
        this.getWritableDatabase().update(TABLE_NAME, cv, "id = ?", new String[]{item.getId() + ""});
    }

    public void deleteNote(int id) {
        this.getWritableDatabase().delete(TABLE_NAME, "id = ?", new String[]{id + ""});
    }

    public void deleteAllNote() {
        this.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    public void exportToCSV() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        File file = new File(exportDir,
                context.getString(R.string.app_name) +
                        new Date().getTime() + ".csv");

        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1),
                        curCSV.getString(2), curCSV.getString(3), curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e(getClass().getSimpleName(), sqlEx.getMessage(), sqlEx);
        }
    }
}