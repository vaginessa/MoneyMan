package com.moneyman.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.moneyman.CustomAdapter;
import com.moneyman.DatabaseHandler;
import com.moneyman.R;
import com.moneyman.models.ModelItem;

import java.util.Calendar;
import java.util.List;

/**
 * Created by chatRG.
 */

public class UtilDialog {

    private static Calendar calendar;

    public MaterialDialog addDialog(final Context context, final DatabaseHandler db,
                                    final CustomAdapter adapter, final ListView lv,
                                    final List<ModelItem> itemList, final TextView tv) {
        return new MaterialDialog.Builder(context)
                .title(R.string.add_transaction)
                .customView(R.layout.dialog_add, true)
                .neutralText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        ModelItem item = getValues(dialog);
                        itemList.add(0, item);
                        lv.post(new Runnable() {
                            @Override
                            public void run() {
                                lv.smoothScrollToPosition(0);
                            }
                        });
                        db.addListItem(item);
                        itemList.clear();
                        db.readFromDB(itemList);
                        adapter.notifyDataSetChanged();
                        String temp = UtilMisc.calculate(item.getAmount(),
                                tv.getText().toString(), item.getTransaction(), 0);
                        UtilMisc.setTotal(context, temp);
                        tv.setText(temp);
                    }
                })
                .cancelable(false)
                .build();
    }

    private static ModelItem getValues(MaterialDialog dialog) {
        String date = UtilMisc.getFormattedDate(calendar);
        String amount = ((EditText) dialog.findViewById(R.id.add_amount)).getText().toString();
        String desc = ((EditText) dialog.findViewById(R.id.add_desc)).getText().toString().trim();
        String type = ((Spinner) dialog.findViewById(R.id.add_spinner)).getSelectedItem().toString();
        return new ModelItem(0, amount, desc, type, date);
    }

    public Calendar datePickerDialog(Context context, final MaterialDialog dialog) {
        Calendar instance = Calendar.getInstance();
        final Calendar newDate = Calendar.getInstance();
        DatePickerDialog dpDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
                ((EditText) dialog.findViewById(R.id.add_date))
                        .setText(UtilMisc.getFormattedDate(calendar));
            }

        }, instance.get(Calendar.YEAR),
                instance.get(Calendar.MONTH),
                instance.get(Calendar.DAY_OF_MONTH));
        dpDialog.show();
        calendar = newDate;
        return newDate;
    }

    public static void exportDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title("Export")
                .content("To CSV format")
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new UtilTask.ExportDatabaseCSVTask(context).execute();
                    }
                })
                .neutralText(android.R.string.cancel)
                .show();
    }

    public static void deleteDialog(final Context context, final List<ModelItem> itemList,
                                    final CustomAdapter adapter, final TextView tv) {
        new MaterialDialog.Builder(context)
                .title("Are you sure?")
                .content("Deleting all transactions is undoable.")
                .positiveText(android.R.string.ok)
                .neutralText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new DatabaseHandler(context).deleteAllNote();
                        itemList.clear();
                        adapter.notifyDataSetChanged();
                        UtilMisc.setTotal(context, "0.0");
                        UtilMisc.updateTotal(context, tv);
                    }
                })
                .show();
    }
}
