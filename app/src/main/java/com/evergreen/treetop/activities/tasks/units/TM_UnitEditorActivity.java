package com.evergreen.treetop.activities.tasks.units;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.DBTask;
import com.evergreen.treetop.architecture.tasks.utils.DBTask.TaskDBKey;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit.UnitDBKey;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;
import com.evergreen.treetop.ui.adapters.AdapterWithEditHeader;
import com.google.firebase.firestore.FieldValue;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TM_UnitEditorActivity extends AppCompatActivity {

    private EditText m_editTitle;
    private EditText m_editDescription;
    private RecyclerView m_listSubunits;

    private Menu m_menuOptions;

    private String m_id;
    private String m_parentId;
    private Unit m_givenUnit;
    private List<Unit> m_children;

    private boolean m_new;

    public static final String PARENT_ID_EXTRA_KEY = "parent-id";
    public static final String UNIT_ID_EXTRA_KEY = "unit-id";
    public static final String RESULT_UNIT_EXTRA_KEY = "result-unit";

    private static final int OPTION_ADD_SUBUNIT_ID = 0;


    ActivityResultLauncher<Intent> m_subunitLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    assert result.getData() != null : "If result is OK then it should contain data!";

                    DBUnit newUnit = (DBUnit) result.getData().getSerializableExtra(RESULT_UNIT_EXTRA_KEY);

                    UnitDB.getInstance().update(
                            m_id,
                            UnitDBKey.CHILDREN_IDS,
                            FieldValue.arrayUnion(newUnit.getId())
                    );

                    m_children.add(Unit.of(newUnit));
                    m_listSubunits.getAdapter().notifyItemChanged(m_children.size() + 1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_editor_tm);

        m_editTitle = findViewById(R.id.tm_unit_editor_edit_title);
        m_editDescription = findViewById(R.id.tm_unit_editor_edit_description);
        m_listSubunits = findViewById(R.id.tm_unit_editor_recycler_subunits);

        m_parentId = getIntent().getStringExtra(PARENT_ID_EXTRA_KEY);
        m_id = getIntent().getStringExtra(UNIT_ID_EXTRA_KEY);

        if (m_id == null) {
            m_id = UnitDB.getInstance().newDoc().getId();
            m_new = true;
            m_children = new ArrayList<>();
        } else {
            m_new = false;
            initEdit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_units_options, menu);
         m_menuOptions = menu;
         menu.removeItem(R.id.tm_unit_options_meni_join_leave);
         menu.removeItem(R.id.tm_unit_options_meni_delete);

         if (m_new) {
             menu.removeItem(R.id.tm_goal_options_meni_edit_mode);
         } else {
             menu.findItem(R.id.tm_unit_options_meni_edit_mode).setIcon(R.drawable.ic_view).setTitle("View Mode");
             menu.add(Menu.NONE, OPTION_ADD_SUBUNIT_ID, Menu.NONE, "Add Subunit").setIcon(R.drawable.ic_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
         }

         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.tm_units_options_meni_submit) {
            if (canSubmit()) {
                submit();
            } else {
                Toast.makeText(this, "Please fill out unit name before submitting", Toast.LENGTH_LONG).show();
                Log.i("TASK_FAIL", "User tried to submit unit without filling details");
            }
        } else if (itemId == R.id.tm_unit_options_meni_edit_mode) {
            startActivity(
                    new Intent(this, TM_UnitViewActivity.class)
                    .putExtra(TM_UnitViewActivity.UNIT_ID_EXTRA_KEY, m_id)
            );
        } else if (itemId == OPTION_ADD_SUBUNIT_ID) {

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean canSubmit() {
        return !m_editTitle.getText().toString().equals("");
    }

    private void submit() {

       new Thread(() -> {
           Looper.prepare();
           Unit result = getUnit();

           try {
               if (m_new) {
                   UnitDB.getInstance().create(result);
               } else {
                   UnitDB.getInstance().update(result);
               }

               Log.i("DB_EVENT", "Submitted " + result.toString());
               setResult(Activity.RESULT_OK,
                       new Intent().putExtra(RESULT_UNIT_EXTRA_KEY, DBUnit.of(result))
               );

               finish();
           } catch (InterruptedException e) {
               Log.w("DB_ERROR", "Cancelled submission of unit" + result.toString());
           } catch (ExecutionException e) {
               Log.w("DB_ERROR", "Attempted to submit " + result.toString() + ", but failed:\n"
                       + ExceptionUtils.getStackTrace(e));
               Toast.makeText(this, "Failed to submit unit: Database Error", Toast.LENGTH_SHORT).show();
           } catch (NoSuchDocumentException e) {
               Toast.makeText(this, "Could not identify unit parent; aborting.", Toast.LENGTH_SHORT).show();
               Log.w("DB_ERROR", "Tried to submit a unit, but it specified a non-existent parent: "
               + ExceptionUtils.getStackTrace(e));
               finish();
           }

       }).start();

    }

    private void initEdit() {
        UIUtils.showLoading(this, R.id.tm_unit_editor_constr_layout);
        new Thread(() -> {
            Looper.prepare();
            initUnit(m_id);
            runOnUiThread(() -> {
                showUnit(m_givenUnit);
                UIUtils.hideLoading(this, R.id.tm_unit_editor_constr_layout);
            });
        }).start();
    }

    private void initUnit(String id) {
        try {
            m_givenUnit = UnitDB.getInstance().awaitUnit(id);
        } catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to edit unit from id " + m_id + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "could not retrieve unit, aborting", Toast.LENGTH_LONG).show();
            cancel();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Tried to edit unit from id " + m_id + ", but the action was cancelled.");
            cancel();
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to edit unit from id " + m_id + ", but there is no such document!");
            Toast.makeText(this, "Could not find the given unit. Aborting.", Toast.LENGTH_SHORT).show();
            cancel();
        }

    }

    private void showUnit(Unit unit) {
        m_editTitle.setText(unit.getName());
        m_editDescription.setText(unit.getDescription());

        new Thread(() -> {
            Looper.prepare();
            try {
                m_children = unit.getChildren();

                runOnUiThread(() -> {
                    m_listSubunits.setLayoutManager(new LinearLayoutManager(TM_UnitEditorActivity.this));
                    m_listSubunits.setAdapter(new AdapterWithEditHeader(this) {
                        @Override
                        public void addAction(View headerView) {
                            m_subunitLauncher.launch(
                                    new Intent(TM_UnitEditorActivity.this, TM_UnitEditorActivity.class)
                                    .putExtra(PARENT_ID_EXTRA_KEY, m_id));
                        }

                        @Override
                        public int _getItemCount() {
                            return m_children.size();
                        }

                        @NonNull
                        @Override
                        public ViewHolder _onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new ViewHolder(
                                    LayoutInflater.from(TM_UnitEditorActivity.this)
                                            .inflate(R.layout.listrow_unit_list, parent, false)
                            ) {};
                        }

                        @Override
                        public void _onBindViewHolder(@NonNull ViewHolder holder, int position) {
                            ((TextView)holder.itemView).setText(m_children.get(position).getName());
                        }
                    });

                });
            } catch (ExecutionException e) {
                Toast.makeText(this, "Could not retrieve subtasks: DB error", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Failed to retrieve subtasks of " + unit.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w("DB_ERROR", "Cancelled retrieving subtasks of " + unit.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
            }
        }).start();

    }


    private Unit getUnit() {
        Unit res = new Unit(
                m_id,
                getName(),
                getDescription(),
                m_new ? UserDB.getInstance().getCurrentUserId() : m_givenUnit.getLeaderId(),
                m_parentId
        );

        getSubunitIds().forEach(res::addChild);

        return res;
    }

    private String getName() {
        return m_editTitle.getText().toString();
    }

    private String getDescription() {
        return m_editDescription.getText().toString();
    }

    private List<String> getSubunitIds() {
        return m_children.stream().map(Unit::getId).collect(Collectors.toList());
    }


    private void cancel() {

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }


}