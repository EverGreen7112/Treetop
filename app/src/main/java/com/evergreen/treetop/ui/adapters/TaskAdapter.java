package com.evergreen.treetop.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Task;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(@NonNull Context context, @NonNull Task[] objects) {
        super(context, R.layout.listrow_listview_goal, R.id.task_list_text_title, objects);
    }
    public TaskAdapter(@NonNull Context context, @NonNull List<Task> objects) {
        super(context, R.layout.listrow_listview_goal, R.id.task_list_text_title, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Task task = getItem(position);

        View rootView = super.getView(position, convertView, parent);
        TextView m_textComplete = rootView.findViewById(R.id.task_list_text_complete_icon);
        TextView m_textTitle = rootView.findViewById(R.id.task_list_text_title);

        m_textComplete.setText(getCompleteIcon(task));
        m_textTitle.setText(task.listDisplayString());

        return rootView;
    }

    public String getCompleteIcon(Task task) {
        if (task.isCompleted()) {
            return "✔";
        } else {
            return "✖";
        }
    }
}
