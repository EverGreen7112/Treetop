package com.evergreen.treetop.activities.tasks.units;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;
import com.evergreen.treetop.ui.adapters.UnitPickerAdapter;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class TM_UnitPickerActivity extends AppCompatActivity {

    public static final String ROOT_UNIT_EXTRA_KEY = "root-unit";
    public static final String USER_FILTER_EXTRA_KEY = "user-filter";
    public static final String RESULT_PICKED_EXTRA_KEY = "picked-unit";

    private static final int ADD_UNIT_MENI_ID = 0;

    RecyclerView m_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_picker_tm);

        m_list = findViewById(R.id.tm_unit_picker_recycler_picker);

        boolean filterForUser = getIntent().getBooleanExtra(USER_FILTER_EXTRA_KEY, false);
        Serializable dbUnit = getIntent().getSerializableExtra(USER_FILTER_EXTRA_KEY);


        new Thread(() -> {
            try {
                User user = filterForUser? UserDB.getInstance().getCurrentUser() : null;
                Unit unit = dbUnit != null? Unit.of((DBUnit)dbUnit) : null;
                UnitPickerAdapter adapter = new UnitPickerAdapter(this, user);
                runOnUiThread(() -> {
                    m_list.setAdapter(adapter);
                    m_list.setLayoutManager(new LinearLayoutManager(this));
                });
            } catch (ExecutionException e) {
                Toast.makeText(this, "Could not retrieve units, aborting.", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrive units for picking, but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w("DB_ERROR", "Cancelled retrieving units for picking, but failed:\n" + ExceptionUtils.getStackTrace(e));
            }
        }).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // If no filters are given, meaning we're coming from dashboard,
        // Add option to add new unit.
        if (!getIntent().getBooleanExtra(USER_FILTER_EXTRA_KEY, false)
                &&  getIntent().getSerializableExtra(USER_FILTER_EXTRA_KEY) != null) {
            MenuItem item = menu.add("New Unit");
            item.setIcon(R.drawable.ic_add);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, TM_UnitPickerActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        ((UnitPickerAdapter)m_list.getAdapter()).onBackPressed();
    }
}