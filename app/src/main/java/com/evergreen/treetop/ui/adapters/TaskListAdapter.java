package com.evergreen.treetop.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_TaskViewActivity;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.AppTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskHolder> {

    private final List<AppTask> m_data;
    private final Context m_context;

    public TaskListAdapter(Context context, List<AppTask> data) {
        m_data = data;
        m_context = context;
        Log.v("UI_EVENT", "Created a new TaskList with values " + Utilities.stringify(m_data));
    }


    public TaskListAdapter(Context context, AppTask[] data) {
        this(context, Arrays.asList(data));
    }


    public TaskListAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listrow_recycler_task_list, parent, false);

        Log.v("UI_EVENT", "Created new TaskHolder");
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        holder.load(m_data.get(position));
        holder.getView().setOnClickListener(v ->
                m_context.startActivity(new Intent(m_context, TM_TaskViewActivity.class)
                        .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, m_data.get(position).getId())));
        Log.v("UI_EVENT", "Bound new TaskHolder to " + m_data.get(position));
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView m_textMain;
        private final TextView m_textComplete;
        private final View m_view;

        public TaskHolder(View view) {
            super(view);
            m_view = view;
            m_textMain = view.findViewById(R.id.tm_task_list_text_title);
            m_textComplete = view.findViewById(R.id.tm_task_list_text_complete_icon);
        }

        public void load(AppTask task) {
            m_textMain.setText(task.listDisplayString());
            m_textComplete.setText(task.getIcon());
        }

        public View getView() {
            return m_view;
        }

    }

    public void add(AppTask... tasks) {
        add(Arrays.asList(tasks));
    }

    public void add(List<AppTask> tasks) {

        int init = getItemCount();

        m_data.addAll(tasks);
        notifyItemRangeInserted(init, getItemCount());
    }

}
