package com.evergreen.treetop.ui.views.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.handlers.GoalDB;
import com.evergreen.treetop.ui.adapters.GoalBoardListAdapter;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GoalBoardListRecycler extends RecyclerView {

    private GoalBoardListAdapter m_adapter;

    public GoalBoardListRecycler(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GoalBoardListRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoalBoardListRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)  {
        m_adapter = new GoalBoardListAdapter(context);
        setAdapter(m_adapter);
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        new Thread( () -> {
            try {
                List<Goal> goals = GoalDB.getInstance().getAll();
                post(() -> m_adapter.add(goals));
            } catch (ExecutionException e) {
                Toast.makeText(context, "DB ERROR: Failed to retrieve some goals", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Could not retrieve goals for dashboard grid:\n" + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w("DB_ERROR", "Cancelled retrieval of goals for dashboard grid:\n" + ExceptionUtils.getStackTrace(e));
            }
        }).start();


    }

    public void refresh() throws ExecutionException, InterruptedException {
        m_adapter.refresh();
    }

    @Override
    public GoalBoardListAdapter getAdapter() {
        return m_adapter;
    }
}
