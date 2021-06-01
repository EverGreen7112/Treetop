package com.evergreen.treetop.activities.tasks.units;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.LoggingUtils;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TM_UnitViewActivity extends AppCompatActivity {

    private TextView m_textTitle;
    private TextView m_textDescription;
    private RecyclerView m_listSubunits;

    private String m_id;
    private Unit m_unitToDisplay;
    private MenuItem m_meniToggleJoin;
    private Menu m_menuOptions;

    public static final String UNIT_ID_EXTRA_KEY = "unit-id";
    /**
     * marks that the edit mode buttons should go back rather than open a new one
     */
    public static final String COMING_FROM_EDIT_EXTRA_KEY = "come-from-edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_view_tm);

        m_textTitle = findViewById(R.id.tm_unit_view_text_title);
        m_textDescription = findViewById(R.id.tm_unit_view_text_description);
        m_listSubunits = findViewById(R.id.tm_unit_view_recycler_subunits);

        m_id = getIntent().getStringExtra(UNIT_ID_EXTRA_KEY);

        if (m_id == null) {
            Toast.makeText(this, "Unit ID not given. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            new Thread(() -> {
                Looper.prepare();
                initUnit(m_id);
                showUnit(m_unitToDisplay);
            }).start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_unitToDisplay != null) {
            UIUtils.showLoading(this, R.id.tm_unit_view_constr_layout);
            new Thread(() -> {
                Looper.prepare();
                reloadUnit();
                UIUtils.hideLoading(this, R.id.tm_unit_view_constr_layout);
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_units_options, menu);
        m_menuOptions = menu;
        menu.removeItem(R.id.tm_units_options_meni_submit);
        m_meniToggleJoin = menu.findItem(R.id.tm_unit_options_meni_join_leave);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.tm_unit_options_meni_join_leave) {
            new Thread(() -> {
                Looper.prepare();
                if (!UserDB.getInstance().getCurrentUser().isIn(m_id)) {
                    try {
                        UserDB.getInstance().joinUnit(m_unitToDisplay);
                        runOnUiThread(() -> m_meniToggleJoin.setIcon(R.drawable.ic_leave));
                        Toast.makeText(this, "Joined unit successfully.", Toast.LENGTH_SHORT).show();
                        Log.i("DB_EVENT", UserDB.getInstance().getCurrentUser().toString() + " joined " + m_unitToDisplay.toString());
                    } catch (ExecutionException | NoSuchDocumentException e) {
                        Toast.makeText(this, "Could not join unit: Database error", Toast.LENGTH_SHORT).show();
                        Log.w("DB_ERROR", "Failed to join "
                                + m_unitToDisplay.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
                    } catch (InterruptedException e) {
                        Log.w("DB_ERROR", "Cancelled joining "
                                + m_unitToDisplay.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
                    }
                } else {
                    if (m_unitToDisplay.hasChildren()) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                        alertBuilder.setMessage("Are you sure you want to leave the unit?\n" +
                                "You will also automatically leave any child units you have joined.");
                        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(this::tryLeaving).start();
                        });
                        alertBuilder.setNegativeButton("No", null);
                        runOnUiThread(() -> alertBuilder.create().show());
                    } else {
                        tryLeaving();
                    }
                }
            }).start();
        } else if (itemId == R.id.tm_unit_options_meni_edit_mode) {
            if (getIntent().getBooleanExtra(COMING_FROM_EDIT_EXTRA_KEY, false)) {
                finish();
            } else {
                startActivity(
                        new Intent(this, TM_UnitEditorActivity.class)
                        .putExtra(TM_UnitEditorActivity.UNIT_ID_EXTRA_KEY, m_id)
                        .putExtra(TM_UnitEditorActivity.PARENT_ID_EXTRA_KEY, m_unitToDisplay.getParentId())
                );
            }
        } else if (itemId == R.id.tm_unit_options_meni_delete) {
            UIUtils.deleteUnitDialouge(this, m_unitToDisplay);
        }
        return true;
    }

    private void tryLeaving() {
        try {
            UserDB.getInstance().leaveUnit(m_unitToDisplay);
            runOnUiThread(() -> m_meniToggleJoin.setIcon(R.drawable.ic_join));
            Toast.makeText(this, "Left unit successfully.", Toast.LENGTH_SHORT).show();
            Log.i("DB_EVENT", UserDB.getInstance().getCurrentUser().toString() + " left " + m_unitToDisplay.toString());
        } catch (ExecutionException e) {
            Toast.makeText(this, "Could not leave unit: Database error", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Failed to leave "
                    + m_unitToDisplay.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Cancelled leaving "
                    + m_unitToDisplay.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    private void initUnit(String id) {
        try {
            m_unitToDisplay = UnitDB.getInstance().awaitUnit(id);
            runOnUiThread(() -> {
                if (UserDB.getInstance().getCurrentUser().isIn(m_id)) {
                    m_meniToggleJoin.setIcon(R.drawable.ic_leave);
                } else {
                    m_meniToggleJoin.setIcon(R.drawable.ic_join);
                }

                if (!UserDB.getInstance().getCurrentUser().isLeading(m_unitToDisplay)
                && !UserDB.getInstance().getCurrentUser().isLeading(m_unitToDisplay.getParentId())) {
                    m_menuOptions.removeItem(R.id.tm_unit_options_meni_edit_mode);
                    m_menuOptions.removeItem(R.id.tm_unit_options_meni_delete);
                }

            });
        } catch (ExecutionException e) {
            Toast.makeText(this, "Could not retrieve unit: Database error. Aborting.", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Failed to retrieve unit by a id " + id + ":\n" + ExceptionUtils.getStackTrace(e));
            finish();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Tried to view a unit by a non-existent id " + id + ":\n"
                    + ExceptionUtils.getStackTrace(e));
            finish();
        } catch (NoSuchDocumentException e) {
            Toast.makeText(this, "Could not identify unit. Aborting.", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to view a unit by a non-existent id:\n" + ExceptionUtils.getStackTrace(e));
            finish();
        }
    }

    private void reloadUnit() {
        try {
            m_unitToDisplay = UnitDB.getInstance().awaitUnit(m_id);
            showUnit(m_unitToDisplay);
        } catch (ExecutionException e) {
            Toast.makeText(this, "Could not reload unit. Info could be outdated", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Failed to reload " + LoggingUtils.stringify(m_unitToDisplay) + ":\n" + ExceptionUtils.getStackTrace(e));
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Cancelled reload of " + LoggingUtils.stringify(m_unitToDisplay) + ":\n"
                    + ExceptionUtils.getStackTrace(e));
        } catch (NoSuchDocumentException e) {
            Toast.makeText(this, "Unit no longer exists. Aborting.", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to reload a unit from non-existent id "
                    + LoggingUtils.stringify(m_unitToDisplay) + ":\n" + ExceptionUtils.getStackTrace(e));
            finish();
        }
    }

    private void showUnit(Unit unit) {

        runOnUiThread(() -> {
            m_textTitle.setText(unit.getName());
            m_textDescription.setText(unit.getDescription());
        });

        try {
            List<Unit> data = unit.getChildren();

            Adapter<ViewHolder> adapter = new Adapter<ViewHolder>() {
                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    Log.v("UI_EVENT", "Created a Unit ViewHolder for subunit");
                    return new ViewHolder(getLayoutInflater().inflate(R.layout.listrow_unit_list, parent, false)) {};
                }

                @Override
                public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                    ((TextView)holder.itemView).setText(data.get(position).getName());
                    holder.itemView.setOnClickListener(v ->
                            startActivity(
                                    new Intent(TM_UnitViewActivity.this, TM_UnitViewActivity.class)
                                    .putExtra(UNIT_ID_EXTRA_KEY, data.get(position).getId())
                            )
                    );
                }

                @Override
                public int getItemCount() {
                    return data.size();
                }

            };

            runOnUiThread(() -> {
                m_listSubunits.setAdapter(adapter);
                m_listSubunits.setLayoutManager(new LinearLayoutManager(this));
            });

        } catch (ExecutionException e) {
            Toast.makeText(this, "Database Error: could not retrieve task children", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Failed to retrieve subunits from " + unit.toString() +
                    ":\n" + ExceptionUtils.getStackTrace(e));
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Cancelled retrieval of subunits from " + unit.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
        }

    }
}