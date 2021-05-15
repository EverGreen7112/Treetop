package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal.GoalDBKey;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ExecutionException;

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

    public DocumentReference getGoalRef(String id) {
        return m_goals.document(id);
    }

    public Query tasksWhereEqual(GoalDBKey key, Object value) {
        return m_goals.whereEqualTo(key.getKey(), value);
    }

    public Task<Void> update(String id, GoalDBKey key, Object value) {
        return getGoalRef(id).update(key.getKey(), value);
    }

    public Task<Void> delete(String id) {
        return getGoalRef(id).delete();
    }

    public Goal awaitGoal(String id) throws ExecutionException, InterruptedException, NoSuchDocumentException {
        DBGoal dbGoal = Tasks.await(m_goals.document(id).get()).toObject(DBGoal.class);

        if (dbGoal == null) {
            throw new NoSuchDocumentException("Tried to retrieve task by id " + id
                    + ", but there was no such document!");
        }

        return Goal.of(dbGoal);
    }
}
