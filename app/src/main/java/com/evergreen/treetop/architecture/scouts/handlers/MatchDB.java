package com.evergreen.treetop.architecture.scouts.handlers;

import android.util.Log;

import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import java.util.Stack;

public class MatchDB {
    /**The currently scouted team. If the scouting form is closed, should be set to -1.  */
    private static Stack<FormObject> m_activeObjects = new Stack<>();

    private int m_team;

    public MatchDB(int team) {
        m_team = team;
        getRef().set(Utilities.PLACEHOLDER_OBJECT, SetOptions.merge());
    }

    /**
     * Adds a form object to the list of currently active objects, such that it will be
     * submitted to the database upon calling {@link #submitActiveForm()}.
     * @param formObject
     */
    public static void activateObject(FormObject formObject) {
        m_activeObjects.add(formObject);
        Log.i("FORM_EVENT", "Activated object " + formObject.getLabel());
    }

    /**
     * Submit the currently active form and reset the active objects.
     */
    public static void submitActiveForm() {

        while (!m_activeObjects.isEmpty()) {
            Log.v("DB_EVENT", "submitting object " + m_activeObjects.peek().getLabel());
            m_activeObjects.pop().submit();
            Log.v("FORM_STATE", "Active object stack: " + m_activeObjects);
        }
    }

    public DocumentReference getRef() {
        return TeamDB.getInstance().getRef().document(Integer.toString(m_team));
    }
}
