package com.moneyman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moneyman.models.ModelItem;
import com.moneyman.utils.UtilDialog;
import com.moneyman.utils.UtilMisc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private MaterialDialog dialog;

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.spent_list)
    ListView mList;
    @BindView(R.id.total)
    TextView tvTotal;

    private List<ModelItem> mSpentList;
    private CustomAdapter mAdapter;
    private DatabaseHandler db;

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
        UtilMisc.updateTotal(getBaseContext(), tvTotal);
        db = new DatabaseHandler(this);
        db.readFromDB(mSpentList);
        mAdapter = new CustomAdapter(MainActivity.this, mSpentList);
        mList.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab)
    public void fabClick() {
        final UtilDialog utilDialog = new UtilDialog();
        dialog = utilDialog.addDialog(MainActivity.this, db, mAdapter, mList, mSpentList, tvTotal);
        (dialog.getCustomView().findViewById(R.id.add_date))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        utilDialog.datePickerDialog(MainActivity.this, dialog);
                    }
                });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    //
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                UtilMisc.updateTotal(getBaseContext(), tvTotal);
                break;

            case R.id.menu_export:
                UtilDialog.exportDialog(MainActivity.this);
                break;

            case R.id.menu_delete:
                UtilDialog.deleteDialog(MainActivity.this, mSpentList, mAdapter, tvTotal);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}