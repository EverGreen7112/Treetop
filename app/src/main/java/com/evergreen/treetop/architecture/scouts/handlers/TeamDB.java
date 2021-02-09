package com.evergreen.treetop.architecture.scouts.handlers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeamDB {

    private final static DatabaseReference m_teamDB =
            FirebaseDatabase.getInstance().getReference("scouting_forms");


    public static DatabaseReference getTeamDB() {
        return m_teamDB;
    }
}
