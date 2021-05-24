package com.evergreen.treetop.activities.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Goal;

public class TM_ParentPickerActivity extends AppCompatActivity {

    RecyclerView m_recyclerView;
    public static final String RESULT_PICKED_EXTRA_KEY = "picked-goal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_picker_tm);

    }
}