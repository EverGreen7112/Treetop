package com.evergreen.treetop.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_TaskEditorActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        startActivity(
                new Intent(this, TM_TaskEditorActivity.class)
//                        .putExtra(TM_TaskEditorActivity.PARENT_GOAL_EXTRA_KEY, "parent-goal")
//                        .putExtra(TM_TaskEditorActivity.TASK_ID_EXTRA_KEY, "9tS4L9fdVis9jb7jrBlD")
        );
    }
}