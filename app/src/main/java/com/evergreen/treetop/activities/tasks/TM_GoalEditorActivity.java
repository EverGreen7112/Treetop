package com.evergreen.treetop.activities.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.handlers.GoalDB;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseGoal;
import com.evergreen.treetop.ui.custom.spinner.BaseSpinner;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class TM_GoalEditorActivity extends AppCompatActivity {

    private EditText m_editTitle;
    private EditText m_editDescription;
    private TextView m_textUnit;
    private TextView m_textSubmit;
    private BaseSpinner m_spinPriority;

    private ConstraintLayout m_form;
    private FrameLayout m_loadingOverlay;

    private String m_id;

    public static String GOAL_ID_EXTRA_KEY = "goal-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_editor_tm);

        m_id = getIntent().getStringExtra(GOAL_ID_EXTRA_KEY);

        m_editTitle = findViewById(R.id.tm_goal_editor_edit_title);
        m_editDescription = findViewById(R.id.tm_goal_editor_edit_description);
        m_textUnit = findViewById(R.id.tm_goal_editor_text_unit);
        m_textSubmit = findViewById(R.id.tm_goal_editor_button_submit);
        m_spinPriority = findViewById(R.id.tm_goal_editor_spin_priority);

        m_form = findViewById(R.id.tm_goal_editor_constr_form);
        m_loadingOverlay = findViewById(R.id.loading_overlay);

        m_spinPriority.loadOptions(new String[]{"", "A", "B", "C", "D", "E"});

        m_textSubmit.setOnClickListener(v -> {
            if (canSubmit()) {
                submit();
            } else {
                Toast.makeText(this, "Please fill out all fields before submitting", Toast.LENGTH_SHORT).show();
            }
        });

        if (m_id != null) {
            Utilities.animateView(m_loadingOverlay, View.VISIBLE, 1, 200);
            Utilities.animateView(m_form, View.VISIBLE, 0.4f, 200);

            new Thread(() -> {
                showGoal(m_id);
                runOnUiThread(() -> {
                    Utilities.animateView(m_form, View.VISIBLE, 1, 200);
                    Utilities.animateView(m_loadingOverlay, View.GONE, 0, 200);
                });
            }).start();


        }
    }

    private void submit() {

        DocumentReference goalDoc;

        if (m_id != null) {
            goalDoc = GoalDB.getInstance().getRef().document(m_id);
        } else {
            goalDoc = GoalDB.getInstance().newDoc();
        }

        goalDoc.set(FirebaseGoal.of(getGoal(goalDoc.getId())));

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("goal", (Serializable) getGoal(goalDoc.getId())));
        }

        finish();
    }

    private boolean canSubmit() {
        return
                getTitle() != ""
//                && !m_textUnit.getText().toString().equals("")
                && getPriority() != -1;
    }

    private Goal getGoal(String id) {
        return new Goal(
                getPriority(),
                id,
                getTitleText(),
                getDescription(),
                getUnit()
        );
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
        return new Unit("Unit-" + getTitle());
    }


    private void showGoal(Goal goal) {
        m_spinPriority.setSelection(goal.getPriority() + 1);
        m_editTitle.setText(goal.getTitle());
        m_editDescription.setText(goal.getDescription());
        m_textUnit.setText(goal.getUnit().getName());
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
        }

    }

    private void cancel() {

        if (getCallingActivity() != null) {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }

}