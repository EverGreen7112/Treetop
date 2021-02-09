package com.evergreen.treetop.architecture.scouts.handlers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeamDB {

    private TeamDB() {}
    private static  final TeamDB m_instance = new TeamDB();

    public static TeamDB getInstance()  {
        return m_instance;
    }

    private final DatabaseReference m_teamDB =
            FirebaseDatabase.getInstance().getReference("scouting_forms");


    public DatabaseReference getTeamDB() {
        return m_teamDB;
    }
}
