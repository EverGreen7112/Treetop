package com.evergreen.treetop.activities.tasks.goals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_DasboardActivity;
import com.evergreen.treetop.activities.tasks.tasks.TM_TaskEditorActivity;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.handlers.GoalDB;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal.GoalDBKey;
import com.evergreen.treetop.architecture.tasks.utils.DBTask;
import com.evergreen.treetop.architecture.tasks.utils.TaskUtils;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;
import com.evergreen.treetop.ui.views.recycler.TaskEditListRecycler;
import com.evergreen.treetop.ui.views.spinner.BaseSpinner;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TM_GoalEditorActivity extends AppCompatActivity {

    private EditText m_editTitle;
    private EditText m_editDescription;
    private TextView m_textUnit;
    private BaseSpinner m_spinPriority;
    private TaskEditListRecycler m_listSubtasks;

    private String m_id;
    private Goal m_goalToDisplay;

    public static final String GOAL_ID_EXTRA_KEY = "goal-id";
    public static final String RESULT_GOAL_EXTRA_KEY = "result-goal";


    private final ActivityResultLauncher<Intent> m_subtaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null : "If result is OK then it should contain data!";

                    m_listSubtasks.getAdapter().add(
                            AppTask.of((DBTask)result.getData().getSerializableExtra(TM_TaskEditorActivity.RESULT_TASK_EXTRA_KEY)));

                    GoalDB.getInstance().update(m_id, GoalDBKey.SUBTASK_IDS, m_listSubtasks.getAdapter().getTaskIds());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_editor_tm);

        m_id = getIntent().getStringExtra(GOAL_ID_EXTRA_KEY);

        m_editTitle = findViewById(R.id.tm_goal_editor_edit_title);
        m_editDescription = findViewById(R.id.tm_goal_editor_edit_description);
        m_textUnit = findViewById(R.id.tm_goal_editor_text_unit);
        m_spinPriority = findViewById(R.id.tm_goal_editor_spin_priority);
        m_listSubtasks = findViewById(R.id.tm_goal_editor_recycler_subtasks);

        m_spinPriority.loadOptions(new String[]{"", "A", "B", "C", "D", "E"});

        if (m_id != null) {

            m_listSubtasks.setAddAction(v ->
                    m_subtaskLauncher.launch(new Intent(this, TM_TaskEditorActivity.class)
                            .putExtra(TM_TaskEditorActivity.PARENT_GOAL_EXTRA_KEY, m_id)
                            .putExtra(TM_TaskEditorActivity.IS_ROOT_TASK_EXTRA_KEY, true)
                    )
            );

            UIUtils.showLoading(this, R.id.tm_goal_editor_constr_form);

            new Thread(() -> {
                showGoal(m_id);
                runOnUiThread(() -> UIUtils.hideLoading(this, R.id.tm_goal_editor_constr_form));
            }).start();

        } else {
            m_id = GoalDB.getInstance().newDoc().getId();

            m_listSubtasks.setAddAction( v->
                    Toast.makeText(this,
                        "You must submit the goal before adding subtasks to it",
                        Toast.LENGTH_SHORT).show()
            );
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_goals_dots, menu);
        menu.findItem(R.id.tm_goal_options_meni_edit_mode).setTitle("View Mode");
        menu.findItem(R.id.tm_goal_options_meni_edit_mode).setIcon(R.drawable.ic_view);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (m_goalToDisplay != null) {
            menu.getItem(R.id.tm_goal_options_meni_toggle_complete)
                    .setTitle("Mark " + (m_goalToDisplay.isCompleted() ? "in" : "") + "complete");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.tm_goal_options_meni_dashboard) {
            startActivity(new Intent(this, TM_DasboardActivity.class));

        } else if (itemId == R.id.tm_goal_options_meni_delete) {
            UIUtils.deleteGoalDialouge(this, m_goalToDisplay);
        } else if (itemId == R.id.tm_goal_options_meni_edit_mode) {
            startActivity(
                    new Intent(this, TM_GoalViewActivity.class)
                            .putExtra(TM_GoalViewActivity.GOAL_ID_EXTRA_KEY, m_id)
            );
        } else if (itemId == R.id.tm_goal_options_meni_toggle_complete) {
//            startAc
        } else if (itemId == R.id.tm_goal_options_meni_submit) {
            if (canSubmit()) {
                submit();
            } else {
                Toast.makeText(this, "Please fill out all fields before submitting", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    private void submit() {
        DBGoal result = DBGoal.of(getGoal());
        GoalDB.getInstance().getGoalRef(m_id).set(result)
                .addOnSuccessListener( aVoid -> {

                    Log.i("DB_EVENT", "Submitted " + result.toString());
                    setResult(
                            Activity.RESULT_OK,
                            new Intent().putExtra(
                                    RESULT_GOAL_EXTRA_KEY,
                                    result
                            )
                    );

                    finish();
                }).addOnFailureListener(e -> {
            Log.w("DB_ERROR", "Attempted to submit " + result.toString() + ", but failed:\n"
                    + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Failed to submit goal: Database Error", Toast.LENGTH_SHORT).show();
        }).addOnCanceledListener(() -> Log.w("DB_ERROR", "Cancelled submission of goal" + result.toString()));
    }

    private boolean canSubmit() {
        return
                getTitle() != ""
//                && !m_textUnit.getText().toString().equals("")
                && getPriority() != -1;
    }

    private Goal getGoal() {
        Goal res = new Goal(
                getPriority(),
                m_id,
                getTitleText(),
                getDescription(),
                getUnitId()
        );

        m_listSubtasks.getAdapter().getData().forEach(res::addSubtask);

        return res;
    }


    private String getTitleText() {
        return m_editTitle.getText().toString();
    }

    private int getPriority() {
        return TaskUtils.priorityNum((String)m_spinPriority.getSelectedItem());
    }

    private String getDescription() {
        return m_editDescription.getText().toString();
    }

    private String getUnitId() {
        return "Unit-" + getTitle();
    }


    private void showGoal(Goal goal) {
        m_spinPriority.setSelection(goal.getPriority() + 1);
        m_editTitle.setText(goal.getTitle());
        m_editDescription.setText(goal.getDescription());


        new Thread( () -> {
            Looper.prepare();
            try {
                String unitName = goal.getUnit().getName();
                runOnUiThread(() -> m_textUnit.setText(unitName));
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(this, "Failed to retrieve goal unit", Toast.LENGTH_LONG).show();
                Log.w("DB_ERROR", "Tried to retrieve unit for task " + goal.toString()
                        + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: failed to identify task unit", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve unit of " + goal.toString() + ", but it specifies a unit that does not exist.");
            }
        }).start();


        new Thread( () -> {
            Looper.prepare();
            try {
                List<AppTask> subtasks = goal.getSubtasks();
                runOnUiThread(() -> m_listSubtasks.getAdapter().add(subtasks));
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(this, "Failed to retrieve some subtasks", Toast.LENGTH_LONG).show();
                Log.w("DB_ERROR", "Tried to retrieve subtasks for goal " + goal.toString()
                        + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: given tasks describes subtasks that do not exist", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve subtasks of " + goal.toString()
                        + ", but it specifies subtasks that do not exist.");
            }
        }).start();
    }

    private void showGoal(String id) {
        try {
            Looper.prepare();
            Goal goal = GoalDB.getInstance().awaitGoal(id);
            runOnUiThread(() -> showGoal(goal));
        } catch (ExecutionException e) {
            Toast.makeText(this, "could not retrieve goal, aborting", Toast.LENGTH_LONG).show();
            cancel();
        } catch (InterruptedException e) {
            cancel();
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to edit goal from id " + m_id + ", but there is no such document!");
            Toast.makeText(this, "Could not find the given goal. Aborting.", Toast.LENGTH_SHORT).show();
            cancel();
        }

    }

    private void cancel() {

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }

}