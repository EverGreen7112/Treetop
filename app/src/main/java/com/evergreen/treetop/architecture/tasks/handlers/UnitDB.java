package com.evergreen.treetop.architecture.tasks.handlers;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.utils.DBGoal;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit.UnitDBKey;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    public DocumentReference getUnitRef(String id) {
        return m_units.document(id);
    }

    private List<Unit> convert(Task<QuerySnapshot> query) throws ExecutionException, InterruptedException {
        return Tasks.await(query)
                .getDocuments().stream().map(doc -> Unit.of(doc.toObject(DBUnit.class)))
                .collect(Collectors.toList());
    }

    public List<Unit> getByIds(List<String> ids) throws ExecutionException, InterruptedException {
        return convert(m_units.whereIn(UnitDBKey.ID.getKey(), ids).get());
    }

    public List<Unit> getAll() throws ExecutionException, InterruptedException {
        return  convert(getRef().get());
    }


    public List<Unit> getUserUnits(User user) throws ExecutionException, InterruptedException {
        return getByIds(user.getUnitIds());
    }

    public List<Unit> getRootUnits() throws ExecutionException, InterruptedException {
        return convert(getRef().whereEqualTo(UnitDBKey.IS_ROOT.getKey(), true).get());
    }

    public List<Unit> getUserUnits() throws InterruptedException, ExecutionException, NoSuchDocumentException {
        return getUserUnits(UserDB.getInstance().getCurrentUser());
    }

    public Unit awaitUnit(String id) throws ExecutionException, InterruptedException, NoSuchDocumentException {
        DBUnit res = Tasks.await(m_units.document(id).get()).toObject(DBUnit.class);

        if (res == null) {
            throw new NoSuchDocumentException("Tried to retrieve unit by id " + id
                    + ", but no such unit exists!");
        }

        return Unit.of(res);
    }

    public Unit getUnitByName(String name) throws ExecutionException, InterruptedException {
        return Tasks.await(m_units.whereEqualTo("title", name).get()).getDocuments().get(0).toObject(Unit.class);
    }

}
