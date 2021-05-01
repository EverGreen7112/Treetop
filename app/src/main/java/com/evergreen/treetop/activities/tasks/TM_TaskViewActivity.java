package com.evergreen.treetop.activities.tasks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.data.Task;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.ui.fragments.tasks.TM_GoalViewFragment;
import com.evergreen.treetop.ui.fragments.tasks.TM_TaskViewFragment;

import java.time.LocalDateTime;
import java.time.Period;

public class TM_TaskViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view_tm);

        @NonNull
        TM_TaskViewFragment frag =
                (TM_TaskViewFragment)getSupportFragmentManager().findFragmentById(R.id.tm_task_view_frag);

        // === TESTING DUMMY TASK ===
        Goal goal = new Goal(0, "test-id", "Test Goal", "this is a test goal", new Unit("TestUnit"));
        Task task =
                new Task(0,
                        "test-id",
                        "Test Task",
                        "this is a test task",
                        new Unit("TestTaskUnit"),
                        goal,
                        LocalDateTime.now().plus(Period.of(0, 0, 1)),
                        LocalDateTime.now().plus(Period.of(0, 0, 3)),
                        new User("Test assigner")
                );
        task.addTask("hello");
        task.addTask("test");
        task.addTask("Long one");
        task.addTask("and");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");
        task.addTask("see?");

        task.addAssignee(new User("Test Assignee 1"));
        task.addAssignee(new User("Test Assignee 2"));
        // ======

        frag.loadTask(task);
    }
}