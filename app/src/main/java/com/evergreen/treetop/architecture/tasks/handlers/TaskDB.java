package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseTask;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;

public class TaskDB {

    private TaskDB() {}
    private static final TaskDB m_instance = new TaskDB();
    public static TaskDB getInstance() {
        return m_instance;
    }

    private CollectionReference m_tasks = FirebaseFirestore.getInstance().collection("tasks");

    public CollectionReference getRef() {
        return m_tasks;
    }

    public DocumentReference newDoc() {
        return m_tasks.document();
    }

    public AppTask awaitTask(String id) throws ExecutionException, InterruptedException, Utilities.NoSuchDocumentException {
        FirebaseTask firebaseTask = Tasks.await(m_tasks.document(id).get()).toObject(FirebaseTask.class);

        if (firebaseTask == null) {
            throw new Utilities.NoSuchDocumentException("Tried to retrieve task by id " + id
                    + ", but there was no such document!");
        }

        return AppTask.of(firebaseTask);
    }

    public Task<DocumentSnapshot> requestTask(String id) {
        return  m_tasks.document(id).get();
    }

}
