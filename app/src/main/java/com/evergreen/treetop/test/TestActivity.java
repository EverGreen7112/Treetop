package com.evergreen.treetop.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_DasboardActivity;
import com.evergreen.treetop.activities.tasks.TM_GoalEditorActivity;
import com.evergreen.treetop.activities.tasks.TM_TaskViewActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);


//        startActivity(
//                new Intent(this, TM_TaskEditorActivity.class)
//                        .putExtra(TM_TaskEditorActivity.PARENT_GOAL_EXTRA_KEY, "parent-goal")
//                        .putExtra(TM_TaskEditorActivity.TASK_ID_EXTRA_KEY, "7DqUvfdUvrQs6muuRfKM")
//        );


//        startActivity(
//                new Intent(this, TM_TaskViewActivity.class)
//                        .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, "7DqUvfdUvrQs6muuRfKM")
//        );

//        startActivity(
//                new Intent(this, TM_GoalEditorActivity.class)
//                    .putExtra(TM_GoalEditorActivity.GOAL_ID_EXTRA_KEY, "BVkyjKBLVutgXFFP3471")
//        );

//        startActivity(new Intent(this, TM_GoalEditorActivity.class));

        startActivity(new Intent(this, TM_DasboardActivity.class));
    }
}