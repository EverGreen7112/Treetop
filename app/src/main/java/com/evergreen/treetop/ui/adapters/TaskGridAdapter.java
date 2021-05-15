package com.evergreen.treetop.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_DasboardActivity;
import com.evergreen.treetop.activities.tasks.TM_TaskViewActivity;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.Logging;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.User;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TaskGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AppTask> m_data;
    private final Context m_context;

    public static final int CODE_OK = 0;
    public static final int CODE_INTERRUPT = 1;
    public static final int CODE_EXECUTE_ERROR = 2;
    public static final int CODE_NULL_ERROR = 3;

    private int m_unitResCode = CODE_OK;
    private int m_assigneeResCode = CODE_OK;

    public TaskGridAdapter(Context context, List<AppTask> data) {
        m_data = data;
        m_context = context;
        Log.v("UI_EVENT", "Created a new TaskGrid with values " + Logging.stringify(m_data));
    }


    public TaskGridAdapter(Context context, AppTask[] data) {
        this(context, Arrays.asList(data));
    }


    public TaskGridAdapter(Context context) {
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
        holderTask.getView() .setOnClickListener(v -> m_context.startActivity(
                new Intent(m_context, TM_TaskViewActivity.class)
                .putExtra(TM_TaskViewActivity.TASK_ID_EXTRA_KEY, m_data.get(position).getId())
        ));

        Log.v("UI_EVENT", "Bound new TaskHolder to " + m_data.get(position));
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView m_textTitle;
        private final TextView m_textInfo;
        private final ProgressBar m_progSepLine;

        public TaskHolder(ViewGroup parent) {

            super(LayoutInflater.from(m_context)
                    .inflate(R.layout.listrow_recycler_task_grid, parent, false)
            );

            m_textTitle = itemView.findViewById(R.id.tm_task_grid_item_text_title);
            m_progSepLine = itemView.findViewById(R.id.tm_task_grid_item_prog_color);
            m_textInfo = itemView.findViewById(R.id.tm_task_grid_item_text_info);
        }

        public void setContent(AppTask task) {
            m_textTitle.setText(task.listDisplayString());

            new Thread(() -> {
                String unitName = null, assigneeString = null;

                try {
                    unitName = task.getUnit().getName();
                } catch (ExecutionException e) {
                    m_unitResCode = CODE_EXECUTE_ERROR;
                    Log.w("DB_ERROR", "Tried to retrieve " + task.toString() + " unit for grid, but failed:\n" + ExceptionUtils.getStackTrace(e));
                } catch (InterruptedException e) {
                    m_unitResCode = CODE_INTERRUPT;
                    Log.w("DB_ERROR", "Cancel retrieval of " + task.toString() + " unit for grid:\n" + ExceptionUtils.getStackTrace(e));
                } catch (NoSuchDocumentException e) {
                    m_unitResCode = CODE_NULL_ERROR;
                    Log.w("DB_ERROR", "Could not retrieve " + task.toString() + " unit for grid; It specifies a non-existent unit:\n" + ExceptionUtils.getStackTrace(e));
                }

                try {
                    assigneeString = task.getAssignees().stream().map(User::getName).collect(Collectors.joining(", "));
                    if (assigneeString.equals("")) {
                        assigneeString = "No Assignees";
                    }
                } catch (ExecutionException e) {
                    m_assigneeResCode = CODE_EXECUTE_ERROR;
                    Log.w("DB_ERROR", "Tried to retrieve assignees of " + task.toString() + " for grid, but failed:\n" + ExceptionUtils.getStackTrace(e));
                } catch (InterruptedException e) {
                    m_assigneeResCode = CODE_INTERRUPT;
                    Log.w("DB_ERROR", "Cancel retrieval of " + task.toString() + " assignees for grid:\n" + ExceptionUtils.getStackTrace(e));
                } catch (NoSuchDocumentException e) {
                    m_assigneeResCode = CODE_NULL_ERROR;
                    Log.w("DB_ERROR", "Could not retrieve " + task.toString() + " assignees for grid; It specifies a non-existent users:\n" + ExceptionUtils.getStackTrace(e));
                }

                if (TM_DasboardActivity.getRunningInstance() != null) { // TODO a more general solution.

                    // Dumb workarounds for lambda variables.
                    final String finalAssigneeString = assigneeString;
                    final String finalUnitName = unitName;

                    TM_DasboardActivity.getRunningInstance().runOnUiThread(() -> {
                        if (finalUnitName != null && finalAssigneeString != null) {
                            m_textInfo.setText(finalUnitName + "·" + finalAssigneeString);
                        } else if (finalUnitName != null) {
                            m_textInfo.setText(finalUnitName);
                            Log.d("TASK_GRID", "Set to " + finalUnitName);
                        } else if (finalAssigneeString != null) {
                            m_textInfo.setText(finalAssigneeString);
                        } else {
                            m_textInfo.setText("INFO ERROR");
                        }
                    });
                }


            }).start();

            m_progSepLine.setMax(1);
            m_progSepLine.setProgress(1);

            int color = task.isCompleted() ? Color.rgb(70, 159, 112) : Color.rgb(136, 10, 10);

            m_progSepLine.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            m_progSepLine.getProgressDrawable().setColorFilter(color
                    , PorterDuff.Mode.SRC_IN);
        }

        public View getView() {
            return itemView;
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

    public int getUnitResult() {
        return m_unitResCode;
    }

    public int getAssigneeResult() {
        return m_assigneeResCode;
    }

}
