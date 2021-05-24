package com.evergreen.treetop.activities.tasks.units;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TM_UnitViewActivity extends AppCompatActivity {

    private TextView m_textTitle;
    private TextView m_textDescription;
    private RecyclerView m_listSubunits;

    private String m_id;
    private Unit m_unitToDisplay;

    public static final String UNIT_ID_EXTRA_KEY = "unit-id";

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

    private void initUnit(String id) {
        try {
            m_unitToDisplay = UnitDB.getInstance().awaitUnit(id);
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
                    Log.v("UI_EVENT", "Creared a Unit ViewHolder for subunit");
                    return new ViewHolder(getLayoutInflater().inflate(R.layout.listrow_unit_list, parent)) {};
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

            runOnUiThread(() -> m_listSubunits.setAdapter(adapter));

        } catch (ExecutionException e) {
            Toast.makeText(this, "Database Error: could not retrieve task children", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Failed to retrieve subunits from " + unit.toString() +
                    ":\n" + ExceptionUtils.getStackTrace(e));
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Cancelled retrieval of subunits from " + unit.toString() + ":\n" + ExceptionUtils.getStackTrace(e));
        }

    }
}