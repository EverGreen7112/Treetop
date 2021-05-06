package com.evergreen.treetop.ui.fragments.tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.ui.custom.recycler.TaskListRecycler;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class TM_TaskViewFragment extends Fragment {

    private TextView m_textTitle;
    private TextView m_textDescription;
    private TextView m_textCompleted;
    private TextView m_textStartDeadline;
    private TextView m_textEndDeadline;
    private TextView m_textAssigner;
    private TextView m_textAssignees;
    private TextView m_textPriority;
    private TextView m_textUnit;
    private ProgressBar m_progressBar;
    private TaskListRecycler m_listSubtasks;
    private String m_id;

    public void loadTask(AppTask task) {
        m_id = task.getId();

        m_textTitle.setText(task.getTitle());
        m_textCompleted.setText(task.getIcon());
        m_textDescription.setText(task.getDescription());
        m_textStartDeadline.setText(task.getStartDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE));
        m_textEndDeadline.setText(task.getEndDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE));
        m_textPriority.setText(task.priorityChar());
        m_textUnit.setText(task.getUnit().getName());
        m_textAssigner.setText(task.getAssigner().getName());
        m_textAssignees.setText(task.getAssignees().stream().map(User::getName).collect(Collectors.joining(", ")));

        m_progressBar.setMax(task.getTaskCount());
        m_progressBar.setProgress(task.getCompletedCount());

        m_listSubtasks.loadTasks(task.getSubtasks());


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_task_view_tm, container, false);

        m_textTitle = thisView.findViewById(R.id.tm_task_view_text_title);
        m_textDescription = thisView.findViewById(R.id.tm_task_view_text_description);
        m_textCompleted = thisView.findViewById(R.id.tm_task_view_text_completed);
        m_textStartDeadline = thisView.findViewById(R.id.tm_task_view_text_start_deadline);
        m_textEndDeadline = thisView.findViewById(R.id.tm_task_view_text_end_deadline);
        m_textAssigner = thisView.findViewById(R.id.tm_task_view_text_assigner);
        m_textAssignees = thisView.findViewById(R.id.tm_task_view_text_assignees);
        m_textPriority = thisView.findViewById(R.id.tm_task_view_text_priority);
        m_textUnit = thisView.findViewById(R.id.tm_task_view_text_unit);
        m_progressBar = thisView.findViewById(R.id.tm_task_view_prog_progress);
        m_listSubtasks = thisView.findViewById(R.id.tm_task_view_list_subtasks);

        return thisView;
    }
}