package com.evergreen.treetop.architecture.scouts.handlers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeamDB {

    private TeamDB() {}
    private static  final TeamDB m_instance = new TeamDB();

    public static TeamDB getInstance()  {
        return m_instance;
    }

    private final DatabaseReference m_teamDB =
            FirebaseDatabase.getInstance().getReference("scouting/forms");

    private final DocumentReference m_ref =
            FirebaseFirestore.getInstance().document("scouting-forms");

    public DatabaseReference getTeamDB() {
        return m_teamDB;
    }

    public DocumentReference getRef() {
        return m_ref;
    }
}
