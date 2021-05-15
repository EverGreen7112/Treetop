package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.utils.DBUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public DocumentReference getUserRef(String id) {
        return m_users.document(id);
    }

    public User awaitUser(String id) throws ExecutionException, InterruptedException, NoSuchDocumentException {
        User res = Tasks.await(m_users.document(id).get()).toObject(User.class);

        if (res == null) {
            throw new NoSuchDocumentException("Tried to retrieve user by id " + id
                    + ", but not such user exists!");
        }

        return res;
    }

    public User getUserByName(String name) throws ExecutionException, InterruptedException {
        return Tasks.await(m_users.whereEqualTo("title", name).get()).getDocuments().get(0).toObject(User.class);
    }

    public User getCurrentUser() throws InterruptedException, ExecutionException, NoSuchDocumentException {
        return awaitUser(getCurrentUserId());
    }

    public void registerCurrent() {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        getUserRef(current.getUid()).set(new DBUser(current.getUid(), current.getDisplayName()));
    }

    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
