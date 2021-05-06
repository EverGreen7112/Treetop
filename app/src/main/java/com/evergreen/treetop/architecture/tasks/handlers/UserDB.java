package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;

public class UserDB {


    private UserDB() {}

    private static final UserDB m_instance = new UserDB();
    public static UserDB getInstance() {
        return m_instance;
    }

    private CollectionReference m_users = FirebaseFirestore.getInstance().collection("units");

    public CollectionReference getRef() {
        return m_users;
    }

    public DocumentReference newDoc() {
        return m_users.document();
    }

    public User getUserById(String id) throws ExecutionException, InterruptedException {
        return Tasks.await(m_users.document(id).get()).toObject(User.class);
    }

    public User getUserByName(String name) throws ExecutionException, InterruptedException {
        return Tasks.await(m_users.whereEqualTo("title", name).get()).getDocuments().get(0).toObject(User.class);
    }



    public User getCurrentUser() {
        return new User("Current");
    }
}
