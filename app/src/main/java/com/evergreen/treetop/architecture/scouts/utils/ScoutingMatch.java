package com.evergreen.treetop.architecture.scouts.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ScoutingMatch {
    private long m_timerStart;
    private long m_matchTime;
    private MatchID m_id;

    private static ScoutingMatch m_current;

    public ScoutingMatch(MatchID id) {
        m_id = id;

        Log.i("DATA_OBJECT", "Initialized new ScoutingMatch " + m_id.toString());
    }

    public static ScoutingMatch getCurrent() {
        return m_current;
    }
    public static void setCurrent(ScoutingMatch currentMatch) {
        m_current = currentMatch;
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

    public DocumentReference getDocRef(int team) {
        return new MatchDB(team).getRef();
    }

    public String getMatchPath() {
        return m_id.toString();
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
        return "ScoutingMatch " + m_id.toString();
    }
}
