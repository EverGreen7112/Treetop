package com.evergreen.treetop.ui.fragments.tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.ui.custom.recycler.TaskListRecycler;

public class TM_GoalViewFragment extends Fragment {

    public void loadGoal(Goal goal) {
        m_textTitle.setText(goal.getTitle());
        m_textDescription.setText(goal.getDescription());
//        Utilities.setBackgroundColor(getContext(), m_textPriority, priorityColor(goal.getPriority()));
        m_textPriority.setText(goal.priorityChar());

        m_listSubtasks.loadTasks(goal.getSubtasks());

        m_progressBar.setMax(goal.getTaskCount());
        m_progressBar.setProgress(goal.getCompletedCount());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView m_textTitle;
    TextView m_textPriority;
    TextView m_textDescription;
    ProgressBar m_progressBar;
    TaskListRecycler m_listSubtasks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_goal_view_tm, container, false);

        m_textTitle = thisView.findViewById(R.id.tm_task_view_text_title);
        m_textDescription = thisView.findViewById(R.id.tm_task_view_text_description);
        m_textPriority = thisView.findViewById(R.id.tm_task_view_text_completed);
        m_progressBar = thisView.findViewById(R.id.tm_task_view_prog_progress);
        m_listSubtasks = thisView.findViewById(R.id.tm_task_view_list_subtasks);

        return thisView;
    }

    private static int priorityColor(int priority) {
        // TODO get actual colors
        switch (priority) {
            case 0:
                return R.color.purple_200;
            case 1:
                return R.color.purple_500;
            case 2:
                return R.color.purple_700;
            default:
                return -1;

        }
    }
}