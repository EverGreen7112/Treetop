package com.evergreen.treetop.architecture.scouts.form;

import android.util.Log;

import androidx.annotation.NonNull;

import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.evergreen.treetop.architecture.scouts.utils.Loggable;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Locale;

public abstract class FormObject implements Loggable {

    private final String m_path;
    private final String m_label;

    private static int s_scoutedTeam;

    public FormObject(String label, String path) {
        m_path = path;
        m_label = label;
        MatchDB.activateObject(this);
        Log.i("FORM_OBJECT", "Initialized " + getType() + " \"" + getLabel() + "\" to path " + getPath());
    }

    @Override
    @NonNull
    public String toString() {
        return m_label + " at " + m_path;
    }

    public String getLabel() {
        return m_label;
    }

    protected abstract Object getValue();
    protected abstract String getType();

    public void submit() {
        Object data = getValue();

        ScoutingMatch
        .getCurrent()
        .getDocRef(FormObject.getScoutedTeam())
        .update(ScoutingMatch.getCurrent().getMatchPath() + "." + m_path, data)
        .addOnSuccessListener( aVoid ->
                Log.d("DB_EVENT",
                        String.format(Locale.ENGLISH,
                                "Submitted %s \"%s\" to path %s under value %s",
                                getType(), getLabel(), getPath(), data.toString()
                        )
                )
        ).addOnFailureListener(e ->
                Log.w("DB_EVENT",
                        String.format(Locale.ENGLISH,
                                "Tried to submit %s \"%s\" to path %s under value %s but failed: \n%s",
                                getType(), getLabel(), getPath(), data.toString(), ExceptionUtils.getStackTrace(e)
                        )
                )
        ).addOnCanceledListener(() ->
                Log.w("DB_EVENT",
                        String.format(Locale.ENGLISH,
                                "Tried to submit %s \"%s\" to path %s under value %s, but the action was cancelled.",
                                getType(), getLabel(), getPath(), data.toString()
                        )
                )
        );
    }

    public String getPath() {
        String matchDoc =
            ScoutingMatch
            .getCurrent()
            .getDocRef(s_scoutedTeam)
            .getPath();

        return matchDoc + "/" + ScoutingMatch.getCurrent().getMatchPath() + "." + m_path;
    }

    public static int getScoutedTeam() {
        return s_scoutedTeam;
    }

    public static void setScoutedTeam(int scoutedTeam) {
        s_scoutedTeam = scoutedTeam;
    }
}
