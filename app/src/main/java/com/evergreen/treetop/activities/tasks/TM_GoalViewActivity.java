package com.evergreen.treetop.activities.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.ui.fragments.tasks.TM_GoalViewFragment;

public class TM_GoalViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_view_tm);

        @NonNull
        TM_GoalViewFragment frag =
                (TM_GoalViewFragment)getSupportFragmentManager().findFragmentById(R.id.tm_view_frag_goal);
        Goal goal = new Goal(0, "test-id", "Test Goal", "this is a test goal", new Unit());
        goal.addTask("hello");
        goal.addTask("test");
        goal.addTask("Long one");
        frag.loadGoal(goal);
    }
}