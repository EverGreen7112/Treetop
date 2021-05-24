package com.evergreen.treetop.activities.tasks;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.goals.TM_GoalEditorActivity;
import com.evergreen.treetop.activities.tasks.units.TM_UnitPickerActivity;
import com.evergreen.treetop.activities.tasks.units.TM_UnitViewActivity;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;

public class TM_DasboardActivity extends AppCompatActivity {

    private static TM_DasboardActivity runningInstance = null;
    private ActivityResultLauncher<Intent> m_unitListLauncher = registerForActivityResult(
            new StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    Unit picked = Unit.of(
                            (DBUnit)result.getData()
                                .getSerializableExtra(TM_UnitPickerActivity.RESULT_PICKED_EXTRA_KEY));

                    startActivity(
                            new Intent(this, TM_UnitViewActivity.class)
                                    .putExtra(TM_UnitViewActivity.UNIT_ID_EXTRA_KEY, picked.getId())
                    );
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard_tm);
    }

    @Override
    protected void onResume() {
        runningInstance = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        runningInstance = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard_options, menu);

        if (UserDB.getInstance().getCurrentUser().getLeadingIds().size() == 0) {
            menu.removeItem(R.id.tm_dashboard_options_meni_new_goal);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.tm_dashboard_options_meni_new_goal) {
            startActivity(new Intent(this, TM_GoalEditorActivity.class));
        } else if (itemId == R.id.tm_dashboard_options_meni_unit_view) {
            m_unitListLauncher.launch(new Intent(this, TM_UnitPickerActivity.class));
        }

        return true;
    }

    public static TM_DasboardActivity getRunningInstance() {
        return runningInstance;
    }
}