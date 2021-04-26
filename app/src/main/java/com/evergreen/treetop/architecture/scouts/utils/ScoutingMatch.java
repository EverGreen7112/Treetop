package com.evergreen.treetop.architecture.scouts.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.scouts.data.MatchTeam;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoutingMatch {
    private long m_timerStart;
    private long m_matchTime;
    private MatchID m_id;

    public List<MatchTeam> getTeams() {
        return m_teams;
    }

    private List<MatchTeam> m_teams = new ArrayList<>(); // TODO add scouters HashMap.
    private static ScoutingMatch m_current = new ScoutingMatch(
            new MatchID(MatchID.MatchType.QUAL, 1),
            Arrays.asList(
                    new MatchTeam(1, "First FIRST", true, "abc1"),
                    new MatchTeam(2, "second FIRST", true, "abc2"),
                    new MatchTeam(3, "third FIRST", true, "abc3"),
                    new MatchTeam(4, "fourth FIRST", false, "abc4"),
                    new MatchTeam(5, "fifth FIRST", false, "abc5"),
                    new MatchTeam(6, "sixth FIRST", false, "abc6")
            )
    );


    public ScoutingMatch(MatchID id, List<MatchTeam> teams) {
        m_id = id;

        if (teams.size() != 6) {
            throw new IllegalArgumentException("Tried to initialize scouting match " + m_id.toString()
                    + ", but was given a badly sized array. ");
        }

        if (teams.stream().filter(MatchTeam::isBlueAlliance).toArray().length != 3) {
            throw new IllegalArgumentException("Tried to initialize scouting match " + m_id.toString()
                    + ", but was given a team list with bad alliance distribution");
        }

        if  (teams.stream().map(MatchTeam::getTeamNumber).collect(Collectors.toSet()).size() != 6) {
            throw new IllegalArgumentException("Tried to initialize scouting match " + m_id.toString()
                    + ", but was given a team list with duplicate numbers");
        }


        if  (teams.stream().map(MatchTeam::getScouterId).collect(Collectors.toSet()).size() != 6) {
            throw new IllegalArgumentException("Tried to initialize scouting match " + m_id.toString()
                    + ", but was given a team list with duplicate scouters");
        }

        m_teams.addAll(teams);
        teams.sort(MatchTeam::compare);

        Log.i("DATA_OBJECT",
              "Initialized new ScoutingMatch " + m_id.toString() +
               ", with teams " + teams.toString());
    }

    public static ScoutingMatch getCurrent() {
        return m_current;
    }

    /**
     * method for testing, when we need to get the database of the current team.
     * Later, this should be achieved using the scouter-team hashmap and the current user object.
     */
    public int getUserTeam() {

        for (MatchTeam team : m_teams) {
            if (team.getScouterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                return team.getTeamNumber();
            }
        }

        return -1;
    }

    public MatchID getID() {
        return m_id;
    }

    public void start() {
        m_timerStart = System.currentTimeMillis();
        Log.i("FORM_EVENT", "Started clock on match " + m_id.toString());
    }

    public int getTimeSinceStart() {
        return  (int)(System.currentTimeMillis() - m_timerStart);
    }

    public DocumentReference getDBRef(int team) {
        return new MatchDB(team).getRef().document(m_id.toString());
    }

    public long getMatchTimeEpoch() {
        return m_matchTime;
    }

    @Exclude
    public LocalDateTime getMatchTIme() {
        return LocalDateTime.ofEpochSecond(m_matchTime, 0, ZoneOffset.UTC);
    }

    public static void startNext() {
        m_current.m_id = m_current.m_id.next();
        m_current.start();
        Log.i("FORM_EVENT", "Increment current match to " + m_current.m_id.toString() + " and started its clock.");
    }

    @Override
    @NonNull
    public String toString() {
        return "ScoutingMatch "
                + m_id.toString()
                + ", teams " + Utilities.stringify(m_teams, MatchTeam::getTeamNumber);

    }
}
