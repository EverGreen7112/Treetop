package com.evergreen.treetop.activities.tasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.evergreen.treetop.ui.views.recycler.TaskEditListRecycler;
import com.evergreen.treetop.ui.views.spinner.BaseSpinner;
import com.google.firebase.firestore.FieldValue;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TM_TaskEditorActivity extends AppCompatActivity {

    private EditText m_editTitle;
    private EditText m_editDescription;
    private TextView m_textUnit;
    private TextView m_textAssignees;
    private TextView m_textStartDeadline;
    private TextView m_textEndDeadline;
    private BaseSpinner m_spinPriority;
    private TaskEditListRecycler m_listSubtasks;

    private String m_id;
    private String m_parentId;
    private String m_rootId;
    private String m_goalId;
    private boolean m_new;

    private  AppTask m_givenTask;
    private final Calendar m_calendar = Calendar.getInstance();

    public static final String PARENT_GOAL_EXTRA_KEY = "task-parent";
    public static final String TASK_ID_EXTRA_KEY = "task-id";
    public static final String IS_ROOT_TASK_EXTRA_KEY = "is-root-task";
    public static final String RESULT_TASK_EXTRA_KEY = "task-child";

    ActivityResultLauncher<Intent> m_subtaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    assert result.getData() != null : "If result is OK then it should contain data!";

                    DBTask newTask = (DBTask) result.getData().getSerializableExtra(RESULT_TASK_EXTRA_KEY);

                    TaskDB.getInstance().update(
                            m_id,
                            TaskDBKey.SUBTASK_IDS,
                            FieldValue.arrayUnion(newTask.getId())
                    );
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor_tm);

        m_parentId = getIntent().getStringExtra(PARENT_GOAL_EXTRA_KEY);
        m_id = getIntent().getStringExtra(TASK_ID_EXTRA_KEY);


        if (m_id == null) {
            m_id = TaskDB.getInstance().newDoc().getId();
            m_new = true;
        } else {
            m_new = false;
        }

        if (m_parentId == null) {
            Toast.makeText(this, "Parent task not set. aborting.", Toast.LENGTH_SHORT).show();
            cancel();
            return;
        } else {
            initRoots(); // Should be called only if m_parent is set. Initialize m_rootId and m_goalId
        }

        locateViews(); // Initializes view objects. Must be called after setContentView.
        initViewActions(); // Should be called only after m_new is set and locateViews() was called.

        if (!m_new) {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_givenTask != null) { // Required check for first method calls
            reloadTask();
        }
    }

    @Override
    public void onBackPressed() {
        discardDialouge();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_dots, menu);
        menu.findItem(R.id.tm_task_options_meni_edit_mode).setTitle("View Mode");
        menu.findItem(R.id.tm_task_options_meni_edit_mode).setIcon(R.drawable.ic_view);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (m_givenTask != null) { // Required check since it's not initialized in the first run
            menu.findItem(R.id.tm_task_options_meni_toggle_complete).setTitle(
                    m_givenTask.isCompleted() ? "Mark Incomplete" : "Mark Completed"
            );

            if (m_givenTask.isRootTask()) {
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
            viewMode();

        } else if (itemId == R.id.tm_task_options_meni_toggle_complete) {
            setCompleted(!m_givenTask.isCompleted());

        } else if (itemId == R.id.tm_task_options_meni_view_goal) {
            startActivity(new Intent(this, TM_GoalViewActivity.class)
                    .putExtra(TM_GoalViewActivity.GOAL_ID_EXTRA_KEY, m_givenTask.getGoalId()));

        } else if (itemId == R.id.tm_task_options_meni_view_root_task) {
            startActivity(new Intent(this, TM_TaskViewActivity.class)
                    .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, m_givenTask.getRootTaskId())
            );

        } else if (itemId == R.id.tm_task_options_meni_delete) {
            UIUtils.deleteTaskDialouge(this, m_givenTask);

        } else if (itemId == R.id.tm_task_options_meni_submit) {
            if (canSubmit()) {
                submit();
            } else {
                Toast.makeText(this, "Please fill out all fields before submitting", Toast.LENGTH_LONG).show();
                Log.i("TASK_FAIL", "User tried to submit task without filling details");
            }
        }

        return true;
    }


    private void setCompleted(boolean completed) {

        UIUtils.showLoading(this); // , R.id.tm_task_editor_constr_layout); // Shows loading
        TaskDB.getInstance().update(m_id, TaskDBKey.COMPLETED, completed)
                .addOnSuccessListener(aVoid -> {
                    m_givenTask.setCompleted(completed);
                    Toast.makeText(this, "Succefully marked " + (completed ? "in" : "") + "complete", Toast.LENGTH_SHORT).show();
                    UIUtils.hideLoading(this);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Could not toggle task complete: Database error", Toast.LENGTH_SHORT).show();
                    Log.w("DB_ERROR", "Tried to set task " + m_id + "(" +
                            m_givenTask.getTitle() + "), but failed:\n" + ExceptionUtils.getStackTrace(e));
                    UIUtils.hideLoading(this);
                }).addOnCanceledListener( () -> {
                    Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show();
                    UIUtils.hideLoading(this);
                });
    }

    private void viewMode() {
        startActivity(new Intent(this, TM_TaskViewActivity.class)
                .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, m_id)
        );
    }


    private void discardDialouge() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Discard Changes?");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
            cancel();
        });
        alertBuilder.setNegativeButton("No", (dialog, which) -> {});
        alertBuilder.create().show();
    }

    private void locateViews() {

        m_editTitle = findViewById(R.id.tm_task_editor_edit_title);
        m_editDescription = findViewById(R.id.tm_task_editor_edit_description);
        m_textUnit = findViewById(R.id.tm_task_editor_text_unit);
        m_textAssignees = findViewById(R.id.tm_task_editor_text_Assignees);
        m_textStartDeadline = findViewById(R.id.tm_task_editor_text_start_deadline);
        m_textEndDeadline = findViewById(R.id.tm_task_editor_text_end_deadline);
        m_spinPriority = findViewById(R.id.tm_task_editor_spin_priority);
        m_listSubtasks = findViewById(R.id.tm_task_editor_recycler_subtasks);
    }

    private void initViewActions() {

        m_spinPriority.loadOptions(new String[]{"", "A", "B", "C", "D", "E"});
        m_textStartDeadline.setOnClickListener(this::setDeadline);
        m_textEndDeadline.setOnClickListener(this::setDeadline);

        if (m_new) {
            m_listSubtasks.setAddAction(v ->
                    Toast.makeText(this, "You must submit the task before adding subtasks to it", Toast.LENGTH_SHORT).show()
            );
        } else {
            m_listSubtasks.setAddAction(v ->
                    m_subtaskLauncher.launch(new Intent(this, getClass()).putExtra(PARENT_GOAL_EXTRA_KEY, m_id))
            );
        }
    }

    private void initRoots() {
        if (getIntent().getBooleanExtra(IS_ROOT_TASK_EXTRA_KEY, false)) {
            m_goalId = m_parentId;
            m_rootId = m_id;
        } else {
            new Thread( () -> {
                Looper.prepare();
                try {
                    AppTask parent = TaskDB.getInstance().awaitTask(m_parentId);
                    m_rootId = parent.getRootTaskId();
                    m_goalId = parent.getGoalId();
                    Log.d("TASK_EDITOR_EVENT", "ROOTID is up: " + m_rootId);
                } catch (ExecutionException e) {
                    Toast.makeText(this, "Could not identify parent task. aborting.", Toast.LENGTH_SHORT).show();
                    Log.w("DB_ERROR", "Failed to retrieve parent task " + m_parentId + ";\n" + ExceptionUtils.getStackTrace(e));
                    cancel();
                } catch (InterruptedException e) {
                    cancel();
                } catch (NoSuchDocumentException e) {
                    Toast.makeText(this, "Could not identify parent task. aborting.", Toast.LENGTH_SHORT).show();
                    Log.w("DB_ERROR","specified a parent task that does not exist: " + m_parentId);
                    cancel();
                }
            }).start();
        }
    }

    private void submit() {


        DBTask result = DBTask.of(getTask());
        TaskDB.getInstance().getTaskRef(m_id).set(result);

        setResult(
                Activity.RESULT_OK,
                new Intent().putExtra(
                        RESULT_TASK_EXTRA_KEY,
                        DBTask.of(getTask())
                )
        );


        finish();
    }

    private boolean canSubmit() {
        return
                getTitle() != ""
                        && getPriority() != -1
                        && !m_textStartDeadline.getText().toString().equals("")
                        && !m_textEndDeadline.getText().toString().equals("")
//                        && !m_textUnit.getText().toString().equals("")
//                        && !m_textAssignees.getText().toString().equals("");
                        ;
    }

    private AppTask getTask() {

        AppTask result = new AppTask(
                getPriority(),
                m_id,
                getTitleText(),
                getDescription(),
                getUnitId(),
                m_parentId,
                getStartDeadline(),
                getEndDeadline(),
                getAssignerId(),
                m_goalId,
                m_rootId
        );

        getAssignees().forEach(result::addAssignee);
        m_listSubtasks.getAdapter().getTaskIds().forEach(result::addSubtaskById);

        return result;
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
        return "unit-" + getTitleText();
    }

    private String getAssignerId() {

        if (m_givenTask != null && m_givenTask.getAssignerId() != null) {
            return m_givenTask.getId();
        }

        return  "test-assigner"; // return UserDB.getInstance().getCurrentUserId();
    }

    private List<User> getAssignees() {
        return new ArrayList<>();
    }

    private LocalDate getStartDeadline() {
        return LocalDate.parse(m_textStartDeadline.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private LocalDate getEndDeadline() {
        return LocalDate.parse(m_textEndDeadline.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private void setDeadline(View deadlineView) {

        // creates a new date picker dialogue.
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // changes the m_calendar to save the variables.
                    m_calendar.set(year, month, dayOfMonth);
                    ((TextView)deadlineView).setText(DateFormat.format("yyyy-MM-dd", m_calendar));
                },

                // sets the current date for the date picker, so it'll show today as the default value.
                m_calendar.get(Calendar.YEAR),
                m_calendar.get(Calendar.MONTH),
                m_calendar.get(Calendar.DAY_OF_MONTH)
        );



        // shows the dialogue.
        datePicker.show();
    }

    private void showTask(AppTask task) {

        m_spinPriority.setSelection(task.getPriority() + 1);
        m_editTitle.setText(task.getTitle());
        m_editDescription.setText(task.getDescription());
        m_textStartDeadline.setText(DateFormat.format("yyyy-MM-dd", Logging.toDate(task.getStartDeadline())));
        m_textEndDeadline.setText(DateFormat.format("yyyy-MM-dd", Logging.toDate(task.getEndDeadline())));

        new Thread( () -> {
            Looper.prepare();
            try {
                String assignees = Logging.stringify(task.getAssignees(), User::getName, false);
                runOnUiThread(() -> m_textAssignees.setText(assignees));
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(this, "Failed to retrieve task assignees", Toast.LENGTH_LONG).show();
                Log.w("DB_ERROR", "Tried to retrieve assignees for " + task.toString()
                        + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: failed to identify task assignees", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve unit of " + task.toString() +
                        ", but it specifies assignees that do not exist:\n" + Logging.stringify(task.getAssigneesIds()));
            }
        }).start();

        new Thread( () -> {
            Looper.prepare();
            try {
                String unitName = task.getUnit().getName();
                runOnUiThread(() -> m_textUnit.setText(unitName));
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(this, "Failed to retrieve task unit", Toast.LENGTH_LONG).show();
                Log.w("DB_ERROR", "Tried to retrieve unit for task " + task.toString()
                        + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: failed to identify task unit", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve unit of " + task.toString() + ", but it specifies a unit that does not exist.");
            }
        }).start();

        new Thread( () -> {
            try {
                List<AppTask> subtasks = task.getSubtasks();
                runOnUiThread(() -> {
                    m_listSubtasks.getAdapter().clear();
                    m_listSubtasks.getAdapter().add(subtasks);
                });
            } catch (InterruptedException | ExecutionException e) {
                Toast.makeText(this, "Failed to retrieve some subtasks", Toast.LENGTH_LONG).show();
                Log.w("DB_ERROR", "Tried to retrieve subtasks for task " + task.toString()
                        + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Toast.makeText(this, "Error: given tasks describes subtasks that do not exist", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve subtasks of " + task.toString()
                        + ", but it specifies subtasks that do not exist.");
            }
        }).start();
    }

    private void initTask(String id) {

        try {
            m_givenTask = TaskDB.getInstance().awaitTask(id);
            Log.d("TASK_EDIT_EVENT", "GIVEN " + Logging.stringify(m_givenTask));
        } catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to edit task from id " + m_id + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "could not retrieve task, aborting", Toast.LENGTH_LONG).show();
            cancel();
        } catch (InterruptedException e) {
            Log.w("DB_ERROR", "Tried to edit task from id " + m_id + ", but the action was cancelled.");
            cancel();
        } catch (NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to edit task from id " + m_id + ", but there is no such document!");
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
            cancel();
        }

        Log.d("TASK_EDIT_EVENT", "GIVEN " + Logging.stringify(m_givenTask));
    }


    private void reloadTask() {
        UIUtils.showLoading(this, R.id.tm_task_editor_constr_layout); // Shows loading
        new Thread( () -> {
            Looper.prepare();

            try {
                m_givenTask = TaskDB.getInstance().awaitTask(m_id);
                runOnUiThread(() -> {
                    showTask(m_givenTask);
                    UIUtils.hideLoading(this, R.id.tm_task_editor_constr_layout);
                });
            } catch (ExecutionException e) {
                Log.w("DB_ERROR", "Tried to refresh task from id " + m_id + ", but failed: \n" + ExceptionUtils.getStackTrace(e));
                Toast.makeText(this, "Could not refresh task. Info possibly outdated.", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Log.w("DB_ERROR", "Canceled refreshing task from id " + m_id + ": \n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Log.w("DB_ERROR", "Tried to reload task from id " + m_id + ", but there is no such document.");
                Toast.makeText(this, "Error: Task no longer exists.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).start();
    }

    private void init() {
        UIUtils.showLoading(this, R.id.tm_task_editor_constr_layout); // Shows loading
        new Thread(() -> {
            Looper.prepare();
            initTask(m_id); // Must be called after Looper.prepare()
            runOnUiThread(() -> {
                showTask(m_givenTask); // Must be called after Looper.prepare()
                UIUtils.hideLoading(this, R.id.tm_task_editor_constr_layout);
            });
        }).start();
    }

    private void cancel() {

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }
}