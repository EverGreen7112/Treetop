package com.evergreen.treetop.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskHolder> {

    private final List<Task> m_data;

    public TaskListAdapter(List<Task> data) {
        m_data = data;

        Log.v("UI_EVENT", "Created a new TaskList with values " + Utilities.stringify(m_data));
    }


    public TaskListAdapter(Task[] data) {
        this(Arrays.asList(data));
    }


    public TaskListAdapter() {
        this(new ArrayList<>());
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
        Log.v("UI_EVENT", "Bound new TaskHolder to " + m_data.get(position));
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView m_textMain;
        private final TextView m_textComplete;

        public TaskHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            m_textMain = view.findViewById(R.id.tm_task_list_text_title);
            m_textComplete = view.findViewById(R.id.tm_task_list_text_complete_icon);
        }

        public void load(Task task) {
            m_textMain.setText(task.listDisplayString());
            m_textComplete.setText(task.getIcon());
        }
    }

    public void add(Task... tasks) {
        add(Arrays.asList(tasks));
    }

    public void add(List<Task> tasks) {

        int init = getItemCount();

        m_data.addAll(tasks);
        notifyItemRangeInserted(init, getItemCount());
    }

}
