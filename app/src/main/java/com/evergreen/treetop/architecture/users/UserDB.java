package com.evergreen.treetop.architecture.users;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

    public class UserDB {
    // TODO implement for custom user (now it's just with AuthUI)

    private UserDB() { }

    private static final UserDB m_instant = new UserDB();
    private final DatabaseReference _m_ref = FirebaseDatabase.getInstance().getReference("users/");
    private final CollectionReference m_ref = FirebaseFirestore.getInstance().collection("users");
    private Set<_User> m_userCache;
    private Set<_User> m_userSetHolder;

    public void registerCurrent() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            throw new IllegalArgumentException("Tried to register the current user, " +
                    "but no user is actually logged in!");
        }

        m_ref.document(user.getUid()).set(new _User(user));
    }

    public void removeUser(String id, Runnable onSuccess, Consumer<Exception> onFailure, Runnable onCancelled) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalArgumentException("Tried to remove a user (" + id + "), but no" +
                    "user is currently logged in");
        }

        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(id)) {
            throw new IllegalArgumentException("Tried to remove user " + id + ", but someone else " +
                    "is logged in!");
        }

        _m_ref.child(id).removeValue();

        Task<Void> deletion = m_ref.document(id).delete();
        deletion.addOnSuccessListener(res -> onSuccess.run());
        deletion.addOnFailureListener(onFailure::accept);
        deletion.addOnCanceledListener(onCancelled::run);

    }

    public void removeUser(String id, Runnable onSuccess, Consumer<Exception> onFailure) {
        removeUser(id, onSuccess, onFailure, () -> {});
    }

    public void removeUser(String id) {
        removeUser(id, () -> {}, e -> {}, () -> {});
    }

    public void onUsers(Consumer<Set<_User>> onSuccess, Consumer<Exception> onFailure, Runnable onCanceled, String... userIds) {

        Task<QuerySnapshot> userRequest = m_ref.whereIn("uid", Arrays.asList(userIds)).get();


        userRequest.addOnSuccessListener(snapshot -> {
            Set<_User> users = new HashSet<_User>();

            for (QueryDocumentSnapshot document : snapshot) {
                users.add(document.toObject(_User.class));
            }

            onSuccess.accept(users);
        });

        userRequest.addOnFailureListener(onFailure::accept);
        userRequest.addOnCanceledListener(onCanceled::run);
    }

    public void onUsers(Consumer<Set<_User>> onSuccess, Consumer<Exception> onFailure, String... userIds) {
        onUsers(onSuccess, onFailure, () -> {}, userIds);
    }

    public void onUsers(Consumer<Set<_User>> onSuccess, Consumer<Exception> onFailure, Runnable onCanceled) {
        Task<QuerySnapshot> userRequest = m_ref.get();

        userRequest.addOnSuccessListener(snapshot -> {
                Set<_User> users = new HashSet<_User>();

                for (QueryDocumentSnapshot document : snapshot) {
                    users.add(document.toObject(_User.class));
                }

                onSuccess.accept(users);
        });

        userRequest.addOnFailureListener(onFailure::accept);
        userRequest.addOnCanceledListener(onCanceled::run);
    }

    public void onUsers(Consumer<Set<_User>> onSuccess, Consumer<Exception> onFailure) {
        onUsers(onSuccess, onFailure, () -> {});
    }

    public static UserDB getInstance() {
        return m_instant;
    }
}
