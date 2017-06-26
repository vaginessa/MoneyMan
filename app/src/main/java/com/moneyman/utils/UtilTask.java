package com.moneyman.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moneyman.CustomConstants;
import com.moneyman.DatabaseHandler;
import com.moneyman.R;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * Created by chatRG.
 */

public class UtilTask {

    public static class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {

        private Context context;
        private MaterialDialog dialog;

        public ExportDatabaseCSVTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new MaterialDialog.Builder(context)
                    .title("Exporting to CSV")
                    .content("Please wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
            dialog.show();
        }


        protected Boolean doInBackground(final String... args) {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            File file = new File(exportDir,
                    context.getString(R.string.app_name) +
                            new Date().getTime() + ".csv");

            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
                Cursor curCSV = db.rawQuery("SELECT * FROM " + CustomConstants.TABLE_NAME, null);
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
                return false;
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            dialog.hide();

            if (success) {
                Toast.makeText(context, "Export success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

