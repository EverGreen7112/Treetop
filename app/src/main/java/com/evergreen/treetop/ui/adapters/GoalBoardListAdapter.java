package com.evergreen.treetop.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.goals.TM_GoalViewActivity;
import com.evergreen.treetop.architecture.LoggingUtils;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.handlers.GoalDB;
import com.evergreen.treetop.ui.adapters.GoalBoardListAdapter.GoalHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GoalBoardListAdapter extends RecyclerView.Adapter<GoalHolder> {

    private final List<Goal> m_data;
    private final Context m_context;


    public GoalBoardListAdapter(Context context) {
        m_data = new ArrayList<>();
        m_context = context;
        Log.v("UI_EVENT", "Created a new GoalkList with values " + LoggingUtils.stringify(m_data));
    }


    @NonNull
    @Override
    public GoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("UI_EVENT", "Created new TaskHolder");
        return new GoalHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalHolder holder, int position) {
        holder.setContent(m_data.get(position));
        Log.v("UI_EVENT", "Bound new TaskHolder to " + m_data.get(position));
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public class GoalHolder extends RecyclerView.ViewHolder {

        public GoalHolder(ViewGroup parent) {
            super(LayoutInflater.from(m_context)
                    .inflate(R.layout.listrow_recycler_goal_dashboard_list, parent, false)
            );
        }

        public void setContent(Goal goal) {
            getView().setText(goal.getTitle());
            getView().setOnClickListener(v -> m_context.startActivity(
                    new Intent(m_context, TM_GoalViewActivity.class)
                            .putExtra(TM_GoalViewActivity.GOAL_ID_EXTRA_KEY, goal.getId())
            ));
        }

        public TextView getView() {
            return (TextView) itemView;
        }

    }

    public void refresh() throws ExecutionException, InterruptedException {
        m_data.clear();
        m_data.addAll(GoalDB.getInstance().getAll());
        notifyDataSetChanged();
    }


    public void add(Goal... goals) {
        add(Arrays.asList(goals));
    }

    public void add(List<Goal> goals) {

        int init = getItemCount();

        m_data.addAll(goals);
        notifyItemRangeInserted(init, getItemCount());
    }


}
