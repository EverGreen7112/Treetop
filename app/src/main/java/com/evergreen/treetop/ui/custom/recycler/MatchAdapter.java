package com.evergreen.treetop.ui.custom.recycler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.MatchTeam;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.architecture.users.UserDB;
import com.evergreen.treetop.architecture.users._User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchHolder> {

    List<ScoutingMatch> m_data;
    Context m_context;

    public MatchAdapter(Context context, ScoutingMatch... data) {
        m_context = context;
        m_data = Arrays.asList(data);
    }

    @NonNull
    @Override
    public MatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i("UI_EVENT", "Initialized Match holder");


        return new MatchHolder(
                m_context,
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.holder_matches_sc, parent, false
                )
        );

    }

    @Override
    public void onBindViewHolder(@NonNull MatchHolder holder, int position) {
        holder.setMatch(m_data.get(position));
        Log.i("UI_EVENT", "Initializing Match list item: " + m_data.get(position).toString());
        Log.d("UI_DEBUG", "Recycler count: " + getItemCount());
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public static class MatchHolder extends RecyclerView.ViewHolder {

        private final TextView m_textId;
        private final TextView m_textTime;
        private final TextView m_textDay;
        private final TextView m_textUsers;
        private final Context m_context;

        public MatchHolder(Context context, @NonNull View itemView) {
            super(itemView);

            m_context = context;

            Log.d("MATCH RECYCLER", "View: " + itemView.toString());
            m_textDay = itemView.findViewById(R.id.sc_sched_text_match_holder_day);

            Log.d("MATCH RECYCLER", "Day View: " + m_textDay.toString());

            m_textTime = itemView.findViewById(R.id.sc_sched_text_match_holder_time);
            Log.d("MATCH RECYCLER", "Time View: " + m_textTime.toString());

            m_textUsers = itemView.findViewById(R.id.sc_sched_text_match_holder_users);
            Log.d("MATCH RECYCLER", "Users View: " + m_textUsers.toString());

            m_textId = itemView.findViewById(R.id.sc_sched_text_match_holder_id);
            Log.d("MATCH RECYCLER", "ID View: " + m_textId.toString());
        }

        public void setMatch(ScoutingMatch match) {

            MatchID id = match.getID();
            String prefix = String.valueOf(id.getType().toString().charAt(0));
            int number = id.getNumber();
            m_textId.setText(prefix + number);

            LocalDateTime time = match.getMatchTIme();
            m_textDay.setText(time.format(DateTimeFormatter.ofPattern("dd/MM")));
            m_textTime.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));

            List<String> userNames = new ArrayList<>();
            UserDB.getInstance().onUsers(

                    users -> {
                        String[] names =
                                users.stream()
                                     .filter(Objects::nonNull)
                                     .map(_User::getName)
                                     .map(name -> name.substring(0, name.indexOf(' ')))
                                     .toArray(String[]::new);

                        String text = String.join(", ", names);

                        m_textUsers.setText(text);
                    },

                    e -> {
                        Log.w("DB_ERROR", e.getMessage());
                        Toast.makeText(
                                m_context,
                                "Could not get users for match " + match.getID().toString(),
                                Toast.LENGTH_LONG)
                            .show();
                    },

                    match.getTeams().stream().map(MatchTeam::getScouterId).toArray(String[]::new)
            );

            m_textUsers.setText(String.join(", ", match.getTeams().stream().map(MatchTeam::getTeamNumber).map(String::valueOf).toArray(String[]::new)));




        }
    }

    public void add(ScoutingMatch... matches) {

        int init = getItemCount();

        m_data.addAll(Arrays.asList(matches));

        for (int i = init; i < getItemCount(); i++) {
            notifyItemInserted(getItemCount());
        }
    }

    public void remove(ScoutingMatch match) {
        int position = m_data.indexOf(match);
        m_data.remove(match);
        notifyItemRemoved(position);
    }

    public void remove(MatchID match) {
        int position = m_data.stream().map(ScoutingMatch::getID).collect(Collectors.toList()).indexOf(match);
        m_data.remove(position);
        notifyItemRemoved(position);
    }
}
