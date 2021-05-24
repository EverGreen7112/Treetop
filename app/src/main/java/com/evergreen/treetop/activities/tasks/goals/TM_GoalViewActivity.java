package com.evergreen.treetop.activities.tasks.goals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_DasboardActivity;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.handlers.GoalDB;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal.GoalDBKey;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;
import com.evergreen.treetop.ui.views.recycler.TaskListRecycler;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TM_GoalViewActivity extends AppCompatActivity {



    private TextView m_textTitle;
    private TextView m_textPriority;
    private TextView m_textDescription;
    private ProgressBar m_progressBar;
    private TaskListRecycler m_listSubtasks;

    private String m_id;
    private Goal m_goalToDisplay;

    public static final String GOAL_ID_EXTRA_KEY = "goal-id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_view_tm);

        m_id = getIntent().getStringExtra(GOAL_ID_EXTRA_KEY);

        if (m_id == null) {
            Toast.makeText(this, "Goal not specified. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        m_textTitle = findViewById(R.id.tm_goal_view_text_title);
        m_textDescription = findViewById(R.id.tm_goal_view_text_description);
        m_textPriority = findViewById(R.id.tm_goal_view_text_priority);
        m_progressBar = findViewById(R.id.tm_goal_view_prog_progress);
        m_listSubtasks = findViewById(R.id.tm_goal_view_list_subtasks);

        UIUtils.showLoading(this, R.id.tm_goal_view_constr_layout);

        new Thread( () -> {
            UIUtils.showLoading(this, R.id.tm_goal_view_constr_layout);
            initGoal();
            runOnUiThread(() -> {
                showGoal();
                UIUtils.hideLoading(this, R.id.tm_goal_view_constr_layout);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (m_id != null) {
            reloadGoal();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_goals_dots, menu);
        menu.findItem(R.id.tm_goal_options_meni_edit_mode).setTitle("Edit Mode");
        menu.removeItem(R.id.tm_goal_options_meni_submit);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (m_goalToDisplay != null) {
            menu.findItem(R.id.tm_goal_options_meni_toggle_complete)
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
                    new Intent(this, TM_GoalEditorActivity.class)
                            .putExtra(TM_GoalEditorActivity.GOAL_ID_EXTRA_KEY, m_id)
            );

        } else if (itemId == R.id.tm_goal_options_meni_toggle_complete) {
            setCompleted(!m_goalToDisplay.isCompleted());

        } else if (itemId == R.id.tm_goal_options_meni_submit) {

        }

        return true;
    }

    private void setCompleted(boolean completed) {
        UIUtils.showLoading(this, R.id.tm_goal_view_constr_layout);
        GoalDB.getInstance().update(m_id, GoalDBKey.COMPLETED, completed)
                .addOnSuccessListener(aVoid -> {
                    m_goalToDisplay.setCompleted(completed);
                    getSupportActionBar().setTitle(m_goalToDisplay.getTitle() + "    " + (m_goalToDisplay.isCompleted() ? AppTask.COMPLETE_ICON : "◯"));
                    UIUtils.hideLoading(this, R.id.tm_goal_view_constr_layout);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Could not toggle task complete: Database error", Toast.LENGTH_SHORT).show();
                    Log.w("DB_ERROR", "Tried to set " + m_id + "(" +
                            m_goalToDisplay.getTitle() + "), but failed:\n" + ExceptionUtils.getStackTrace(e));
                    UIUtils.hideLoading(this, R.id.tm_goal_view_constr_layout);
                }).addOnCanceledListener(() -> {
                    Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show();
                    UIUtils.hideLoading(this, R.id.tm_goal_view_constr_layout);
                });
    }

    private void reloadGoal() {
        UIUtils.showLoading(this, R.id.tm_goal_view_constr_layout);
        new Thread( () -> {
            reinitGoal();
            runOnUiThread(() -> {
                showGoal();
                UIUtils.hideLoading(this, R.id.tm_goal_view_constr_layout);
            });
        }).start();
    }


    public void reinitGoal() {

        try {
            m_goalToDisplay = GoalDB.getInstance().awaitGoal(m_id);
        } catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to refresh goal from id " + m_id + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Could not refresh goal. Info possibly outdated.", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Canceled refreshing goal from id " + m_id + ":\n" + ExceptionUtils.getStackTrace(e));
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to reload goal from id " + m_id + ", but there is no such document.");
            Toast.makeText(this, "Error: Task no longer exists.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void initGoal() {

        try {
            m_goalToDisplay = GoalDB.getInstance().awaitGoal(m_id);
        }  catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to display goal from id " + m_id + ", but failed: \n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Could not find the given goal. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Canceled display goal from id " + m_id + ", but the action was cancelled.");
            finish();
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to display goal from id " + m_id + ", but there is no such document.");
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void showGoal() {

        Goal goal = m_goalToDisplay;

        getSupportActionBar().setIcon(R.drawable.ic_app);
        getSupportActionBar().setTitle(goal.getTitle() + "    " + (goal.isCompleted() ? AppTask.COMPLETE_ICON : "◯"));


        m_textTitle.setText(goal.getTitle());
        m_textDescription.setText(goal.getDescription());
        m_textPriority.setText(goal.priorityChar());

        m_progressBar.setMax(goal.getTaskCount());

        new Thread(() -> loadSubtasks(goal)).start();
    }

    private void loadSubtasks(Goal goal) {
        try {
            List<AppTask> subtasks = goal.getSubtasks();
            runOnUiThread( () -> {
                m_progressBar.setMax(subtasks.size());
                m_progressBar.setProgress((int)subtasks.stream().filter(AppTask::isCompleted).count());
                m_listSubtasks.getAdapter().clear();
                m_listSubtasks.loadTasks(subtasks);
            });
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(this, "Could not retrieve some subtasks", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to retrieve subtasks of " + goal.toString() + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
        } catch (NoSuchDocumentException e) {
            Toast.makeText(this, "Error: given tasks describes subtasks that do not exist", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to retrieve subtasks of " + goal.toString() + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
        }
    }


}