package com.evergreen.treetop.architecture.scouts.handlers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeamDB {

    private TeamDB() {}
    private static  final TeamDB m_instance = new TeamDB();

    public static TeamDB getInstance()  {
        return m_instance;
    }

    private final FirebaseFirestore m_ref =
            FirebaseFirestore.getInstance();

    public FirebaseFirestore getRef() {
        return m_ref;
    }
}
