package com.evergreen.treetop.activities.tasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseTask;
import com.evergreen.treetop.ui.custom.spinner.BaseSpinner;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TM_TaskEditorActivity extends AppCompatActivity {

    private EditText m_editTitle;
    private EditText m_editDescription;
    private TextView m_textUnit;
    private TextView m_textAssignees;
    private TextView m_textStartDeadline;
    private TextView m_textEndDeadline;
    private TextView m_textSubmit;
    private BaseSpinner m_spinPriority;

    private ConstraintLayout m_form;
    private FrameLayout m_loadingOverlay;

    private String m_id;
    private String m_parentId;

    private final Calendar m_calendar = Calendar.getInstance();

    public static String PARENT_GOAL_EXTRA_KEY = "task-parent";
    public static String TASK_ID_EXTRA_KEY = "task-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor_tm);

        m_parentId = getIntent().getStringExtra(PARENT_GOAL_EXTRA_KEY);
        m_id = getIntent().getStringExtra(TASK_ID_EXTRA_KEY);

        if (m_parentId == null) {
            Toast.makeText(this, "Parent task not set. aborting.", Toast.LENGTH_SHORT).show();
            cancel();
            return;
        }

        m_editTitle = findViewById(R.id.tm_task_editor_edit_title);
        m_editDescription = findViewById(R.id.tm_task_editor_edit_description);
        m_textUnit = findViewById(R.id.tm_task_editor_text_unit);
        m_textAssignees = findViewById(R.id.tm_task_editor_text_Assignees);
        m_textStartDeadline = findViewById(R.id.tm_task_editor_text_start_deadline);
        m_textEndDeadline = findViewById(R.id.tm_task_editor_text_end_deadline);
        m_textSubmit = findViewById(R.id.tm_task_editor_button_submit);
        m_spinPriority = findViewById(R.id.tm_task_editor_spin_priority);

        m_form = findViewById(R.id.tm_task_editor_constr_form);
        m_loadingOverlay = findViewById(R.id.loading_overlay);

        m_spinPriority.loadOptions(new String[]{"", "A", "B", "C", "D", "E"});
        m_textStartDeadline.setOnClickListener(this::setDeadline);
        m_textEndDeadline.setOnClickListener(this::setDeadline);

        m_textSubmit.setOnClickListener(v -> {
            if (canSubmit()) {
                submit();
            } else {
                Toast.makeText(this, "Please fill out all fields before submitting", Toast.LENGTH_LONG).show();
                Log.i("TASK_FAIL", "User tried to submit task without filling details");
            }
        });

        if (m_id != null) {
            Utilities.animateView(m_loadingOverlay, View.VISIBLE, 1, 200);
            Utilities.animateView(m_form, View.VISIBLE, 0.4f, 200);

            new Thread(() -> {
                showTask(m_id);
                runOnUiThread(() -> {
                    Utilities.animateView(m_form, View.VISIBLE, 1, 200);
                    Utilities.animateView(m_loadingOverlay, View.GONE, 0, 200);
                });
            }).start();


        }
    }

    private void submit() {

        DocumentReference taskDoc;

        if (m_id != null) {
            taskDoc = TaskDB.getInstance().getRef().document(m_id);
        } else {
            taskDoc = TaskDB.getInstance().newDoc();
        }

        taskDoc.set(FirebaseTask.of(getTask(taskDoc.getId())));

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("task", (Serializable) getTask(taskDoc.getId())));
        }

        finish();
    }

    private boolean canSubmit() {
        return
                getTitle() != ""
                        && getPriority() != -1
                        && !m_textStartDeadline.getText().toString().equals("")
                        && !m_textEndDeadline.getText().toString().equals("")
                        && !m_textUnit.getText().toString().equals("")
//                        && !m_textAssignees.getText().toString().equals("");
                        ;
    }

    private AppTask getTask(String id) {

        AppTask result = new AppTask(
                getPriority(),
                id,
                getTitleText(),
                getDescription(),
                getUnit(),
                m_parentId,
                getStartDeadline(),
                getEndDeadline(),
                getAssigner()
        );

        getAssignees().forEach(result::addAssignee);

        return result;
    }


    private String getTitleText() {
        return m_editTitle.getText().toString();
    }

    private int getPriority() {
        return Utilities.priorityNum((String)m_spinPriority.getSelectedItem());
    }

    private String getDescription() {
        return m_editDescription.getText().toString();
    }

    private Unit getUnit() {
        return new Unit(m_textUnit.getText().toString());
    }

    private User getAssigner() {
        return UserDB.getInstance().getCurrentUser();
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
        m_textUnit.setText(task.getUnit().getName());
        m_textStartDeadline.setText(DateFormat.format("yyyy-MM-dd", Utilities.toDate(task.getStartDeadline())));
        m_textEndDeadline.setText(DateFormat.format("yyyy-MM-dd", Utilities.toDate(task.getEndDeadline())));
        m_textAssignees.setText(Utilities.stringify(task.getAssignees(), User::getName, false));
    }

    private void showTask(String id) {
        try {
            Looper.prepare();
            AppTask task = TaskDB.getInstance().awaitTask(id);
            runOnUiThread(() -> showTask(task));
        } catch (ExecutionException e) {
            Toast.makeText(this, "could not retrieve task, aborting", Toast.LENGTH_LONG).show();
            cancel();
        } catch (InterruptedException e) {
            cancel();
        } catch (Utilities.NoSuchDocumentException e) {
            Log.w("DB_ERROR", "Tried to edit task from id " + m_id + ", but there is no such document!");
            Toast.makeText(this, "Could not find the given task. Aborting.", Toast.LENGTH_SHORT).show();
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