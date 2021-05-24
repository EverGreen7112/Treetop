package com.evergreen.treetop.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.tasks.TM_TaskViewActivity;
import com.evergreen.treetop.architecture.LoggingUtils;
import com.evergreen.treetop.architecture.tasks.data.AppTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AppTask> m_data;
    private final Context m_context;


    public TaskListAdapter(Context context, List<AppTask> data) {
        m_data = data;
        m_context = context;
        Log.v("UI_EVENT", "Created a new TaskList with values " + LoggingUtils.stringify(m_data));
    }


    public TaskListAdapter(Context context, AppTask[] data) {
        this(context, Arrays.asList(data));
    }


    public TaskListAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.v("UI_EVENT", "Created new TaskHolder");
        return new TaskHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskHolder holderTask = (TaskHolder)holder;
        holderTask.setContent(m_data.get(position));

        OnClickListener action = v -> m_context.startActivity(
                new Intent(m_context, TM_TaskViewActivity.class)
                .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, m_data.get(position).getId())
        );

        holderTask.getView().setOnClickListener(action);
        // Scroll View ClickListeners need to be set manually: see
        // https://stackoverflow.com/questions/16776687/onclicklistener-on-scrollview/16776927#16776927
        holderTask.getMainView().setOnClickListener(action);
        Log.v("UI_EVENT", "Bound new TaskHolder to " + m_data.get(position));
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView m_textMain;
        private final TextView m_textComplete;

        public TaskHolder(ViewGroup parent) {

            super(LayoutInflater.from(m_context)
                    .inflate(R.layout.listrow_recycler_task_list, parent, false)
            );

            m_textMain = itemView.findViewById(R.id.tm_unit_picker_text_title);
            m_textComplete = itemView.findViewById(R.id.tm_unit_picker_text_more_icon);
        }

        public void setContent(AppTask task) {
            m_textMain.setText(task.listDisplayString());
            m_textComplete.setText(task.getIcon());
        }

        public View getView() {
            return itemView;
        }
        public TextView getMainView() {
            return m_textMain;
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

    public List<String> getTaskIds() {
        return m_data.stream().map(AppTask::getId).collect(Collectors.toList());
    }

    public List<AppTask> getData() {
        return m_data;
    }

    public void clear() {
        int init = getItemCount();
        m_data.clear();
        notifyItemRangeRemoved(0, init);
    }


}
