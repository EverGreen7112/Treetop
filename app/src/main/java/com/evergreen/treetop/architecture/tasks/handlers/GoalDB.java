package com.evergreen.treetop.architecture.tasks.handlers;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseGoal;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class GoalDB {

    private GoalDB() {}
    private static final GoalDB m_instance = new GoalDB();
    public static GoalDB getInstance() {
        return m_instance;
    }

    private CollectionReference m_goals = FirebaseFirestore.getInstance().collection("goals");

    public CollectionReference getRef() {
        return m_goals;
    }

    public DocumentReference newDoc() {
        return m_goals.document();
    }

    public Task<DocumentSnapshot> requestGoal(String id) {
        return  m_goals.document(id).get();
    }

    public Goal awaitGoal(String id) throws ExecutionException, InterruptedException {
        FirebaseGoal firebaseGoal = Tasks.await(m_goals.document(id).get()).toObject(FirebaseGoal.class);
        return Goal.of(firebaseGoal);
    }
}
