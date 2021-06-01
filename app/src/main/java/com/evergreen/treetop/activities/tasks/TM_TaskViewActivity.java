package com.evergreen.treetop.activities.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Logging;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.architecture.tasks.utils.DBTask;
import com.evergreen.treetop.architecture.tasks.utils.DBTask.TaskDBKey;
import com.evergreen.treetop.architecture.tasks.utils.TaskUtils;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;
import com.evergreen.treetop.ui.views.recycler.TaskListRecycler;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TM_TaskViewActivity extends AppCompatActivity {


    private TextView m_textTitle;
    private TextView m_textDescription;
    private TextView m_textCompleted;
    private TextView m_textStartDeadline;
    private TextView m_textEndDeadline;
    private TextView m_textAssigner;
    private TextView m_textAssignees;
    private TextView m_textPriority;
    private TextView m_textUnit;
    private ProgressBar m_progressBar;
    private TaskListRecycler m_listSubtasks;

    private String m_id;
    private AppTask m_taskToDisplay;


    private final ActivityResultLauncher<Intent> m_editThis = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    m_taskToDisplay = AppTask.of((DBTask)result.getData().getSerializableExtra(TM_TaskEditorActivity.RESULT_TASK_EXTRA_KEY));
                    showTask(m_taskToDisplay);
                }
            }
    );

    public static final String TASK_ID_EXTRA_KEY = "task-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view_tm);
        m_id = getIntent().getStringExtra(TASK_ID_EXTRA_KEY);

        if (m_id == null) {
            Toast.makeText(this, "Could not find task id. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        m_textTitle = findViewById(R.id.tm_task_view_text_title);
        m_textDescription = findViewById(R.id.tm_task_view_text_description);
        m_textCompleted = findViewById(R.id.tm_task_view_text_completed);
        m_textStartDeadline = findViewById(R.id.tm_task_view_text_start_deadline);
        m_textEndDeadline = findViewById(R.id.tm_task_view_text_end_deadline);
        m_textAssigner = findViewById(R.id.tm_task_view_text_assigner);
        m_textAssignees = findViewById(R.id.tm_task_view_text_assignees);
        m_textPriority = findViewById(R.id.tm_task_view_text_priority);
        m_textUnit = findViewById(R.id.tm_task_view_text_unit);
        m_progressBar = findViewById(R.id.tm_task_view_prog_progress);
        m_listSubtasks = findViewById(R.id.tm_task_view_list_subtasks);

        UIUtils.showLoading(this, R.id.tm_task_view_constr_layout);

        new Thread(() -> {
            initTask();
            // In order to not get NullPointerException when methods continue to run after cancel:
            if (m_taskToDisplay == null) return;

            Log.i("DB_EVENT", "Retrieved task " + m_taskToDisplay.toString());
            runOnUiThread(() -> {
                showTask(m_taskToDisplay);
                UIUtils.hideLoading(this, R.id.tm_task_view_constr_layout);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_taskToDisplay != null) { // Required check for first method calls
            new Thread(this::reloadTask).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_dots, menu);
        menu.findItem(R.id.tm_task_options_meni_edit_mode).setTitle("Edit Mode");
        menu.removeItem(R.id.tm_task_options_meni_submit);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (m_taskToDisplay != null) { // Required check since it's not initialized in the first run
            menu.findItem(R.id.tm_task_options_meni_toggle_complete).setTitle(
                    m_taskToDisplay.isCompleted() ? "Mark Incomplete" : "Mark Completed"
            );

            if (m_taskToDisplay.isRootTask()) {
                menu.removeItem(R.id.tm_task_options_meni_view_root_task);
            }
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Uses ifs instead of switch -- see https://sites.google.com/a/android.com/tools/tips/non-constant-fields
        if (itemId == R.id.tm_task_options_meni_dashboard) {
            startActivity(new Intent(this, TM_DasboardActivity.class));
        } else if (itemId == R.id.tm_task_options_meni_edit_mode) {
            m_editThis.launch(new Intent(this, TM_TaskEditorActivity.class)
                    .putExtra(TM_TaskEditorActivity.TASK_ID_EXTRA_KEY, m_id)
                    .putExtra(TM_TaskEditorActivity.PARENT_GOAL_EXTRA_KEY, m_taskToDisplay.getParentId())
                    .putExtra(TM_TaskEditorActivity.IS_ROOT_TASK_EXTRA_KEY, m_taskToDisplay.isRootTask())
            );

        } else if (itemId == R.id.tm_task_options_meni_toggle_complete) {
            setCompleted(!m_taskToDisplay.isCompleted());

        } else if (itemId == R.id.tm_task_options_meni_view_goal) {
            startActivity(new Intent(this, TM_GoalViewActivity.class)
                    .putExtra(TM_GoalViewActivity.GOAL_ID_EXTRA_KEY, m_taskToDisplay.getGoalId()));

        } else if (itemId == R.id.tm_task_options_meni_view_root_task) {
            startActivity(new Intent(this, TM_TaskViewActivity.class)
                    .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, m_taskToDisplay.getRootTaskId())
            );

        } else if (itemId == R.id.tm_task_options_meni_delete) {
            UIUtils.deleteTaskDialouge(this, m_taskToDisplay);
        }

        return true;
    }


    private void setCompleted(boolean completed) {

        UIUtils.showLoading(this, R.id.tm_task_view_constr_layout);
        TaskDB.getInstance().update(m_id, TaskDBKey.COMPLETED, completed)
            .addOnSuccessListener(aVoid -> {
                m_taskToDisplay.setCompleted(completed);
                m_textCompleted.setText(m_taskToDisplay.getIcon());
                UIUtils.hideLoading(this, R.id.tm_task_view_constr_layout);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Could not toggle task complete: Database error", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to set task " + m_id + "(" +
                        m_taskToDisplay.getTitle() + "), but failed:\n" + ExceptionUtils.getStackTrace(e));
                UIUtils.hideLoading(this, R.id.tm_task_view_constr_layout);
            }).addOnCanceledListener( () -> {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show();
                UIUtils.hideLoading(this, R.id.tm_task_view_constr_layout);
            });
}


    private void initTask() {
        Looper.prepare();
        try {
            m_taskToDisplay = TaskDB.getInstance().awaitTask(m_id);
        } catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to display task from id " + m_id + ", but failed: \n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Canceled display pf task from id " + m_id + ": \n" + ExceptionUtils.getStackTrace(e));
            finish();
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to display task from id " + m_id + ", but there is no such document.");
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void reloadTask() {
        Looper.prepare();
        try {
            m_taskToDisplay = TaskDB.getInstance().awaitTask(m_id);
            runOnUiThread( () -> showTask(m_taskToDisplay));
        } catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to refresh task from id " + m_id + ", but failed: \n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Could refresh task.", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Canceled refreshing task from id " + m_id + ": \n" + ExceptionUtils.getStackTrace(e));
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to reload task from id " + m_id + ", but there is no such document.");
            Toast.makeText(this, "Error: Task no longer exists.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void reloadSubtasks() {

        runOnUiThread(() -> {
            UIUtils.showLoading(this, R.id.tm_task_view_constr_layout);
            m_listSubtasks.getAdapter().clear();
        });

        try {
            List<AppTask> subtasks = m_taskToDisplay.getSubtasks();
            runOnUiThread(() -> {
                m_progressBar.setProgress((int) subtasks.stream().filter(AppTask::isCompleted).count());
                m_listSubtasks.loadTasks(subtasks);
                UIUtils.hideLoading(this, R.id.tm_task_view_constr_layout);
            });

        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(this, "Could not refresh subtasks. Info possibly incorrect", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to refresh subtasks of " + m_taskToDisplay.toString()
                    + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
        } catch (NoSuchDocumentException e) {
            Toast.makeText(this, "Error: given tasks describes subtasks that do not exist", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to refresh subtasks of " + m_taskToDisplay.toString()
                    + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    private void loadSubtasks(AppTask task) {
        try {
            List<AppTask> subtasks = task.getSubtasks();
            m_progressBar.setMax(task.getTaskCount());
            runOnUiThread( () -> {
                m_progressBar.setProgress((int)subtasks.stream().filter(AppTask::isCompleted).count());
                m_listSubtasks.getAdapter().clear();
                m_listSubtasks.loadTasks(subtasks);
            });
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(this, "Could not retrieve some subtasks", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to retrieve subtasks of " + task.toString() + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
        } catch (NoSuchDocumentException e) {
            Toast.makeText(this, "Error: given tasks describes subtasks that do not exist", Toast.LENGTH_SHORT).show();
            Log.w("DB_ERROR", "Tried to retrieve subtasks of " + task.toString() + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
        }
    }


    private void showTask(AppTask task) {
        m_id = task.getId();

        m_textPriority.setText(TaskUtils.priorityChar(task.getPriority()));
        m_textCompleted.setText(task.getIcon());
        m_textTitle.setText(task.getTitle());
        m_textCompleted.setText(task.getIcon());
        m_textDescription.setText(task.getDescription());
        m_textStartDeadline.setText(task.getStartDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE));
        m_textEndDeadline.setText(task.getEndDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE));
        m_textPriority.setText(task.priorityChar());

        new Thread( () -> {
            Looper.prepare();

            try {
                m_textUnit.setText(task.getUnit().getName());
            } catch (ExecutionException e) {
                Toast.makeText(this, "Error: could not retrieve unit.", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR",
                        "Tried to retrieve unit of " + task.toString()
                                + "from id " + task.getUnitId() + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w(
                        "DB_ERROR",
                        "Canceled display of unit " + task.getUnitId()
                                + " from " + task.toString()
                );
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: could not identify task's unit.", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR",
                        "Tried to retrieve unit of " + task.toString()
                                + "from id " + task.getUnitId() + ", but no such unit exists");
            }
        }).start();

        new Thread(() -> {
            Looper.prepare();

            try {
                m_textAssigner.setText(task.getAssigner().getName());
            } catch (ExecutionException e) {
                Toast.makeText(this, "Error: could not retrieve assigner.", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR",
                        "Tried to retrieve assigner of " + task.toString() + " from id "
                                + task.getAssignerId() + ", but failed:\n"
                                + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w(
                        "DB_ERROR",
                        "Canceled display of assigner " + task.getAssignerId() + " from "
                                + task.toString()
                );
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: could not identify task's unit.", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR",
                        "Tried to retrieve assigner of " + task.toString()
                                + "from id " + task.getAssignerId() + ", but no such user exists");
            }
        }).start();

        new Thread(() -> {
            Looper.prepare();

            try {
                m_textAssignees.setText(task.getAssignees().stream().map(User::getName).collect(Collectors.joining(", ")));
            } catch (ExecutionException e) {
                Toast.makeText(this, "Error: could not retrieve some assignees.", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR",
                        "Tried to retrieve assignees of " + task.toString() + "from id "
                                + Logging.stringify(task.getAssigneesIds()) + ", but failed:\n"
                                + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w(
                        "DB_ERROR",
                        "Canceled display of assignees from " + task.toString()
                );
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this,
                        "Error: could not identify some of s assignees.",
                        Toast.LENGTH_SHORT).show();

                Log.w("DB_ERROR",
                        "Tried to retrieve assignees of " + task.toString()
                                + ", but they contain non-existent users");
            }
        }).start();


        new Thread(() -> {
            Looper.prepare();
            loadSubtasks(task);
        }).start();
    }
}