package com.evergreen.treetop.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.TM_ParentPickerActivity;
import com.evergreen.treetop.architecture.LoggingUtils;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GoalPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Goal> m_data;
    private final TM_ParentPickerActivity m_context;


    public GoalPickerAdapter(TM_ParentPickerActivity context, List<Goal> data) {
        m_data = data;
        m_context = context;
        setHasStableIds(true);
        Log.v("UI_EVENT", "Created a new GoalList with values " + LoggingUtils.stringify(m_data));
    }


    public GoalPickerAdapter(TM_ParentPickerActivity context, Goal[] data) {
        this(context, Arrays.asList(data));
    }


    public GoalPickerAdapter(TM_ParentPickerActivity context) {
        this(context, new ArrayList<>());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("UI_EVENT", "Created new GoalHolder");
        return new GoalHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GoalHolder holderGoal = (GoalHolder)holder;
        Goal goal = m_data.get(position);

        holderGoal.setContent(goal);


        holderGoal.getView().setOnClickListener(v -> {
            m_context.setResult(
                    Activity.RESULT_OK,
                    new Intent()
                        .putExtra(
                                TM_ParentPickerActivity.RESULT_PICKED_EXTRA_KEY,
                                DBGoal.of(m_data.get(position))
                        )
            );

            m_context.finish();
        });

        Log.v("UI_EVENT", "Bound new GoalHolder to " + m_data.get(position));
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public class GoalHolder extends RecyclerView.ViewHolder {

        private final TextView m_textMain;
        private final TextView m_textComplete;

        public GoalHolder(ViewGroup parent) {

            super(LayoutInflater.from(m_context)
                    .inflate(R.layout.listrow_recycler_task_list, parent, false)
            );

            m_textMain = itemView.findViewById(R.id.tm_unit_picker_text_title);
            m_textComplete = itemView.findViewById(R.id.tm_unit_picker_text_more_icon);
        }

        public void setContent(Goal goal) {
            m_textMain.setText(goal.getTitle());
            m_textComplete.setText(goal.getIcon());
        }

        public View getView() {
            return itemView;
        }
        public TextView getMainView() {
            return m_textMain;
        }

    }

    public void add(Goal... goals) {
        add(Arrays.asList(goals));
    }

    public void add(List<Goal> goals) {

        int init = getItemCount();

        m_data.addAll(goals);
        notifyItemRangeInserted(init, getItemCount());
    }

    public List<String> getGoalIds() {
        return m_data.stream().map(Goal::getId).collect(Collectors.toList());
    }

    public List<Goal> getData() {
        return m_data;
    }

    public void clear() {
        m_data.clear();
        notifyDataSetChanged();
    }



}


