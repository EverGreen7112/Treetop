package com.evergreen.treetop.ui.views.recycler;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;
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
        m_adapter = new TaskGridAdapter((Activity) context);
        setAdapter(m_adapter);
        setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
        m_adapter.refresh();


    }

    public void invalidate() {
        setAdapter(m_adapter);
    }



    @Override
    public TaskGridAdapter getAdapter() {
        return m_adapter;
    }
}
