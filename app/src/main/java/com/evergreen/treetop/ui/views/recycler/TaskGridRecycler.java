package com.evergreen.treetop.ui.views.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.ui.adapters.TaskGridAdapter;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TaskGridRecycler extends RecyclerView {

    private TaskGridAdapter m_adapter;


    public static final int SPAN_COUNT = 2;


    public TaskGridRecycler(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TaskGridRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskGridRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)  {
        m_adapter = new TaskGridAdapter(context);
        setAdapter(m_adapter);
        setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
        new Thread( () -> {
            try {

                List<AppTask> subtasks = TaskDB.getInstance().getRootTasks()
                        .stream().sorted(AppTask.PRIROITY_COMPARATOR)
                        .collect(Collectors.toList());


                post(() -> {
                    m_adapter.add(subtasks);

                    switch (m_adapter.getAssigneeResult()) {
                        case TaskGridAdapter.CODE_EXECUTE_ERROR:
                            Toast.makeText(context, "DB ERROR: Could not retrieve some task assignees", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskGridAdapter.CODE_NULL_ERROR:
                            Toast.makeText(context, "DB ERROR: some tasks contain non existent assignees", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    switch (m_adapter.getUnitResult()) {
                        case TaskGridAdapter.CODE_EXECUTE_ERROR:
                            Toast.makeText(context, "DB ERROR: Could not retrieve some task units", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskGridAdapter.CODE_NULL_ERROR:
                            Toast.makeText(context, "DB ERROR: some tasks contain a non existent unit", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
            } catch (ExecutionException e) {
                Toast.makeText(context, "DB ERROR: Failed to retrieve tasks", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Could not retrieve tasks for dashboard grid:\n" + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w("DB_ERROR", "Cancelled retrieval of root tasks for dashboard grid:\n" + ExceptionUtils.getStackTrace(e));
            }
        }).start();


    }

    public void invalidate() {
        setAdapter(m_adapter);
    }



    @Override
    public TaskGridAdapter getAdapter() {
        return m_adapter;
    }
}
