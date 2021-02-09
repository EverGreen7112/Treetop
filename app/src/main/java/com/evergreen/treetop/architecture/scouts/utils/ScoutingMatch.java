package com.evergreen.treetop.architecture.scouts.utils;

import android.util.Log;

import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoutingMatch {
    private long m_startTime;
    private MatchID m_id;
    private int[][] m_teams = new int[2][3]; //TODO add scouters HashMap.


    public ScoutingMatch(MatchID id, int[][] teams) {
        m_id = id;
        try {
            for (int i = 0; i < m_teams.length; i++) {
                for (int j = 0; j < m_teams[0].length; j++) {
                   m_teams[i][j] = teams[i][j];
                }
            }
        } catch (IndexOutOfBoundsException e ) {
            throw new IllegalArgumentException("Tried to initialize scouting match " + m_id.toString()
            + ", but was given a badly sized array. ");
        }

        Log.i(
                "DATA_OBJECT",
                "Initialized new ScoutingMatch " + m_id.toString() + ", with teams ("
                    + m_teams[0][0] + ", " + m_teams[0][1] + ", " + m_teams[0][2] + "), ("
                    + m_teams[1][0] + ", " + m_teams[1][1] + ", " + m_teams[1][2] + ")"
            );
    }


    /**
     * method for testing, when we need to get the database of the current team.
     * Later, this should be achieved using the scouter-team hashmap and the current user object.
     */
    @Deprecated
    public int getTeam() {
        return m_teams[0][0];
    }

    public void start() {
        m_startTime = System.currentTimeMillis();
        Log.i("FORM_EVENT", "Started clock on match " + m_id.toString());
    }

    public int getTimeSinceStart() {
        return  (int)(System.currentTimeMillis() - m_startTime);
    }

    public DatabaseReference getDBRef(int team) {
        return new MatchDB(team).getRef().child(m_id.toString());
    }

}
