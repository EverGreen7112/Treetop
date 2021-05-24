package com.evergreen.treetop.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.activities.tasks.TM_DasboardActivity;
import com.evergreen.treetop.activities.tasks.goals.TM_GoalEditorActivity;
import com.evergreen.treetop.activities.tasks.notes.TM_NotesActivity;
import com.evergreen.treetop.activities.tasks.tasks.TM_TaskEditorActivity;
import com.evergreen.treetop.activities.tasks.tasks.TM_TaskViewActivity;
import com.evergreen.treetop.activities.tasks.units.TM_UnitEditorActivity;
import com.evergreen.treetop.activities.tasks.units.TM_UnitPickerActivity;

public class TestActivity extends AppCompatActivity {

    public static final boolean TEST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);


//        startActivity(
//                new Intent(this, TM_TaskEditorActivity.class)
//                    .putExtra(TM_TaskEditorActivity.PARENT_GOAL_EXTRA_KEY, "BVkyjKBLVutgXFFP3471")
//                    .putExtra(TM_TaskEditorActivity.IS_ROOT_TASK_EXTRA_KEY, true)
//                    .putExtra(TM_TaskEditorActivity.TASK_ID_EXTRA_KEY, "HDuWnHPodOXW6HiZQYVQ")
//        );


//        startActivity(
//                new Intent(this, TM_TaskViewActivity.class)
//                        .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, "KKjeAXmdkfS6TdN3YGvK")
//        );

//        startActivity(
//                new Intent(this, TM_GoalEditorActivity.class)
//                    .putExtra(TM_GoalEditorActivity.GOAL_ID_EXTRA_KEY, "BVkyjKBLVutgXFFP3471")
//        );

//        startActivity(new Intent(this, TM_GoalEditorActivity.class));

        startActivity(new Intent(this, TM_DasboardActivity.class));

//        startActivity(new Intent(this, TM_UnitEditorActivity.class));


//        startActivity(new Intent(this, TM_UnitEditorActivity.class))
//        .putExtra(TM_UnitEditorActivity.PARENT_ID_EXTRA_KEY, "CoDCjMPVVg6tbXBFLwQ4"))
//        ;

//        startActivity(new Intent(this, TM_UnitPickerActivity.class));
//        startActivity(new Intent(this, TM_NotesActivity.class));
    }
}