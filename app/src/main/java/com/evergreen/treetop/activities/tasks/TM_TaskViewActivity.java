package com.evergreen.treetop.activities.tasks;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.ui.custom.recycler.TaskListRecycler;
import com.evergreen.treetop.ui.fragments.tasks.TM_TaskViewFragment;
import com.google.android.gms.tasks.Tasks;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.format.DateTimeFormatter;
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

        new Thread(() -> {
            getTask();
            Utilities.showLoading(this);
        });

        if (m_taskToDisplay == null) {
            Toast.makeText(this, "Could not retrieve the given task. Aborting.", Toast.LENGTH_SHORT).show();
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

        loadTask(m_taskToDisplay);
    }

    public void getTask() {
        try {
            m_taskToDisplay = TaskDB.getInstance().awaitTask(m_id);
        } catch (ExecutionException e) {
            Log.w("DB_ERROR", "Tried to display task from id " + m_id + ", but failed: \n" + ExceptionUtils.getStackTrace(e));
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (InterruptedException e) {
            finish();
        } catch (Utilities.NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to display task from id " + m_id + ", but there is no such document!");
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void loadTask(AppTask task) {
        m_id = task.getId();

        m_textTitle.setText(task.getTitle());
        m_textCompleted.setText(task.getIcon());
        m_textDescription.setText(task.getDescription());
        m_textStartDeadline.setText(task.getStartDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE));
        m_textEndDeadline.setText(task.getEndDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE));
        m_textPriority.setText(task.priorityChar());
        m_textUnit.setText(task.getUnit().getName());
        m_textAssigner.setText(task.getAssigner().getName());
        m_textAssignees.setText(task.getAssignees().stream().map(User::getName).collect(Collectors.joining(", ")));

        m_progressBar.setMax(task.getTaskCount());
        m_progressBar.setProgress(task.getCompletedCount());

        m_listSubtasks.loadTasks(task.getSubtasks());
    }
}