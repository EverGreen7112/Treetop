package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.utils.DBUser;
import com.evergreen.treetop.architecture.tasks.utils.DBUser.UserDBKey;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class UserDB {


    private UserDB() {}
    private User m_user;

    private static final UserDB m_instance = new UserDB();
    public static UserDB getInstance() {
        return m_instance;
    }

    public void cacheCurrent() throws InterruptedException, ExecutionException, NoSuchDocumentException {
        try {
            m_user = awaitUser(getCurrentUserId());
        } catch (NoSuchDocumentException e) {
            FirebaseAuth.getInstance().getCurrentUser().delete();
            throw new NoSuchDocumentException(e.getMessage());
        }
    }

    private CollectionReference m_users = FirebaseFirestore.getInstance().collection("users");

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
        DBUser res = Tasks.await(m_users.document(id).get()).toObject(DBUser.class);

        if (res == null) {
            throw new NoSuchDocumentException("Tried to retrieve user by id " + id
                    + ", but not such user exists!");
        }

        return User.of(res);
    }

    public User getUserByName(String name) throws ExecutionException, InterruptedException {
        return Tasks.await(m_users.whereEqualTo("title", name).get()).getDocuments().get(0).toObject(User.class);
    }

    public User getCurrentUser() {
        return m_user;
    }

    public void registerCurrent() throws ExecutionException, InterruptedException {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        Tasks.await(getUserRef(current.getUid()).set(new DBUser(current.getUid(), current.getDisplayName())));
        m_user = new User(current.getUid(), current.getDisplayName());
    }

    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public List<User> getUnitUsers(String unitId) throws ExecutionException, InterruptedException {
        return Tasks.await(m_users.whereArrayContains(UserDBKey.UNIT_IDS.getKey(), unitId).get())
                .getDocuments().stream()
                .map(doc -> User.of(doc.toObject(DBUser.class)))
                .collect(Collectors.toList());
    }
}
