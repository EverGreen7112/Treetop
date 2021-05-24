package com.evergreen.treetop.activities.tasks.units;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.utils.DBTask;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

public class TM_UnitEditorActivity extends AppCompatActivity {

    private EditText m_editTitle;
    private EditText m_editDescription;

    private String m_id;
    private String m_parentId;
    private boolean m_new;

    public static final String PARENT_ID_EXTRA_KEY = "parent-id";
    public static final String UNIT_ID_EXTRA_KEY = "unit-id";
    public static final String RESULT_UNIT_EXTRA_KEY = "result-unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_editor_tm);

        m_editTitle = findViewById(R.id.tm_unit_editor_edit_title);
        m_editDescription = findViewById(R.id.tm_unit_editor_edit_description);



        m_parentId = getIntent().getStringExtra(PARENT_ID_EXTRA_KEY);
        m_id = getIntent().getStringExtra(UNIT_ID_EXTRA_KEY);


        if (m_id == null) {
            m_id = UnitDB.getInstance().newDoc().getId();
            m_new = true;
        } else {
            m_new = false;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_units_options, menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.tm_task_options_meni_submit) {
            if (canSubmit()) {
                submit();
            } else {
                Toast.makeText(this, "Please fill out unit name before submitting", Toast.LENGTH_LONG).show();
                Log.i("TASK_FAIL", "User tried to submit unit without filling details");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean canSubmit() {
        return !m_editTitle.getText().toString().equals("");
    }

    private void submit() {

        DBUnit result = DBUnit.of(getUnit());

        UnitDB.getInstance().getUnitRef(m_id).set(result)
        .addOnSuccessListener( aVoid -> {
            Log.i("DB_EVENT", "Submitted " + result.toString());
            setResult(
                    Activity.RESULT_OK,
                    new Intent().putExtra(
                            RESULT_UNIT_EXTRA_KEY,
                            result
                    )
            );

            finish();

        }).addOnFailureListener(e -> {
            Log.w("DB_ERROR", "Attempted to submit " + result.toString() + ", but failed:\n"
            + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Failed to submit unit: Database Error", Toast.LENGTH_SHORT).show();

        }).addOnCanceledListener(() -> Log.w("DB_ERROR", "Cancelled submission of unit" + result.toString()));

    }

    private void showUnit(Unit unit) {
        m_editTitle.setText(unit.getName());
        m_editDescription.setText(unit.getDescription());
    }


    private Unit getUnit() {
        Unit res = new Unit(
                m_id,
                getName(),
                getDescription(),
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
        return new ArrayList<>(); // TODO user adapter
    }


    private void cancel() {

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }


}