package com.evergreen.treetop.ui.fragments.tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.data.Task;

public class TM_GoalViewFragment extends Fragment {

    public void loadGoal(Goal goal) {
        m_titleBox.setText(goal.getTitle());
        m_descriptionBox.setText(goal.getDescription());
        Utilities.setBackgroundColor(getContext(), m_priorityBox, priorityColor(goal.getPriority()));
        m_subtasksList.setAdapter(new ArrayAdapter<String>(
                getContext(),
                R.layout.listrow_listview_goal,
                goal.getSubtasks().stream().map(Task::getTitle).toArray(String[]::new)
        ));

        m_progressBar.setMax(goal.getTaskCount());
        m_progressBar.setProgress(goal.getCompletedCount());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView m_titleBox;
    TextView m_priorityBox;
    TextView m_descriptionBox;
    ProgressBar m_progressBar;
    ListView m_subtasksList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_task_view_tm, container, false);

        m_titleBox = thisView.findViewById(R.id.tm_view_goal_text_title);
        m_descriptionBox = thisView.findViewById(R.id.tm_view_goal_text_description);
        m_priorityBox = thisView.findViewById(R.id.tm_view_goal_text_priority);
        m_progressBar = thisView.findViewById(R.id.tm_view_goal_prog_subtask_progress);
        m_subtasksList = thisView.findViewById(R.id.tm_view_goal_list_subtasks);

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