package com.evergreen.treetop.architecture.scouts.form;

import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.evergreen.treetop.architecture.scouts.utils.Loggable;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.google.firebase.database.DatabaseReference;

public abstract class FormObject implements Loggable {

    private final String m_path;
    private final String m_label;

    public FormObject(String label, String path) {
        m_path = path;
        m_label = label;
        MatchDB.activateObject(this);
    }

    public abstract void submit();

    @Override
    public String toString() {
        return m_label + " at " + m_path;
    }

    public String getLabel() {
        return m_label;
    }

    public DatabaseReference getRef() {
        return ScoutingMatch.getCurrent().getDBRef(ScoutingMatch.getCurrent().getTeam()).child(m_path);
    }

    public String getPath() {
        return getRef().getKey();
    }
}
