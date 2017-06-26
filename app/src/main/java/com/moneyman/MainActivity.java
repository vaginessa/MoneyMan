package com.moneyman;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.spent_list)
    ListView mList;
    @BindView(R.id.total)
    TextView tv_total;

    private List<SpentItem> mSpentList;
    private CustomAdapter mAdapter;
    private DatabaseHandler db;
    private static MaterialDialog dialog;

    private static int year;
    private static int month;
    private static int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setSupportActionBar(mToolbar);
        mSpentList = new ArrayList<>();
        Utils.updateTotal(getBaseContext(), tv_total);
        db = new DatabaseHandler(this);
        db.readFromDB(mSpentList);
        mAdapter = new CustomAdapter(MainActivity.this, mSpentList);
        mList.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab)
    public void fabClick() {
        dialog = new MaterialDialog.Builder(this)
                .title(R.string.add_transaction)
                .customView(R.layout.dialog_add, true)
                .neutralText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        SpentItem item = getValues(dialog);
                        mSpentList.add(0, item);
                        mList.post(new Runnable() {
                            @Override
                            public void run() {
                                mList.smoothScrollToPosition(0);
                            }
                        });
                        db.addListItem(item);
                        mSpentList.clear();
                        db.readFromDB(mSpentList);
                        mAdapter.notifyDataSetChanged();
                        String temp = Utils.calculate(item.getAmount(),
                                tv_total.getText().toString(), item.getTransaction(), 0);
                        Utils.setTotal(getBaseContext(), temp);
                        tv_total.setText(temp);
                    }
                })
                .cancelable(false)
                .build();

        (dialog.getCustomView().findViewById(R.id.add_date))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment newFragment = new DatePickerFragment();
                        newFragment.show(getFragmentManager(), "datepicker");
                    }
                });
        dialog.show();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            //dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dpDialog;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = i1 + 1;
            day = i2;

            String date = year + "-" + Utils.getFormattedMonth(month) + "-" + day;
            ((EditText) dialog.findViewById(R.id.add_date)).setText(date);
        }
    }

    private SpentItem getValues(MaterialDialog dialog) {
        String date = year + "-" + Utils.getFormattedMonth(month) + "-" + day;
        String amount = ((EditText) dialog.findViewById(R.id.add_amount)).getText().toString();
        String desc = ((EditText) dialog.findViewById(R.id.add_desc)).getText().toString().trim();
        String type = ((Spinner) dialog.findViewById(R.id.add_spinner)).getSelectedItem().toString();
        return new SpentItem(0, amount, desc, type, date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Utils.updateTotal(getBaseContext(), tv_total);
                break;

            case R.id.menu_delete:
                new MaterialDialog.Builder(this)
                        .title("Are you sure?")
                        .content("Deleting all transactions is undoable.")
                        .positiveText(android.R.string.ok)
                        .neutralText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new DatabaseHandler(MainActivity.this).deleteAllNote();
                                mSpentList.clear();
                                mAdapter.notifyDataSetChanged();
                                Utils.setTotal(MainActivity.this, "0.0");
                                Utils.updateTotal(getBaseContext(), tv_total);
                            }
                        })
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dialog.isShowing()) {
            dialog.hide();
        }
        super.onBackPressed();
    }
}