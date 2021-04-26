package com.evergreen.treetop.architecture.scouts.data;

import androidx.annotation.NonNull;

import com.evergreen.treetop.architecture.scouts.utils.Loggable;
//import com.evergreen.treetop.architecture.users.UserDB;
//import com.evergreen.treetop.architecture.users._User;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Consumer;

public class MatchTeam implements Loggable {
    private int m_teamNumber;
    private String m_name;

    public String getScouterId() {
        return m_scouterId;
    }

    private String m_scouterId;
    private boolean m_blueAlliance;

    public int getTeamNumber() {
        return m_teamNumber;
    }

    public String getName() {
        return m_name;
    }

    public boolean isBlueAlliance() {
        return m_blueAlliance;
    }

//    public void onUser(Consumer<_User> action, Consumer<Exception> onFailure) {
//        UserDB.getInstance().onUsers(
//                set -> action.accept(set.iterator().next()),
//                onFailure,
//                m_scouterId);
//    }

//    public void setUser(_User user) {
//        m_scouterId = user.getUid();
//    }

    public MatchTeam(int teamNumber, String name, boolean blueAlliance, String scouterId) {
        m_teamNumber = teamNumber;
        m_name = name;
        m_blueAlliance = blueAlliance;
        m_scouterId = scouterId;
    }

    @Override
    public String getLabel() {
        return getName();
    }

    @Override
    @NonNull
    public String toString() {
        return getName() + " #" + getTeamNumber();
    }

    public static int compare(MatchTeam teamA, MatchTeam teamB) {
        if (teamA.isBlueAlliance() && !teamB.isBlueAlliance()) {
            return 1;
        } else if (!teamA.isBlueAlliance() && teamB.isBlueAlliance()) {
            return -1;
        } else {
            return Integer.compare(teamA.getTeamNumber(), teamB.getTeamNumber());
        }
    }

}
