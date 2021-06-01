package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;

public class UnitDB {

    private UnitDB() {}
    private static final UnitDB m_instance = new UnitDB();
    public static UnitDB getInstance() {
        return m_instance;
    }

    private CollectionReference m_units = FirebaseFirestore.getInstance().collection("units");

    public CollectionReference getRef() {
        return m_units;
    }

    public DocumentReference newDoc() {
        return m_units.document();
    }

    public Unit awaitUnit(String id) throws ExecutionException, InterruptedException, NoSuchDocumentException {
        Unit res = Tasks.await(m_units.document(id).get()).toObject(Unit.class);

        if (res == null) {
            throw new NoSuchDocumentException("Tried to retrieve unit by id " + id
                    + ", but no such unit exists!");
        }

        return res;
    }

    public Unit getUnitByName(String name) throws ExecutionException, InterruptedException {
        return Tasks.await(m_units.whereEqualTo("title", name).get()).getDocuments().get(0).toObject(Unit.class);
    }

}
