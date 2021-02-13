package com.evergreen.treetop.architecture.scouts.form;

import android.util.Log;
import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.data.HitMissAction;

public class HitMissCounter extends FormObject {

    private final Counter m_hitCounter;
    private final Counter m_missCounter;

    public HitMissCounter(String label, String path, TextView labelView, TextView hitIncrement,
                          TextView hitDecrement, TextView missIncrement, TextView missDecrement) {
        super(label, path);
        labelView.setText(label);
        m_hitCounter = new Counter("Hit", hitIncrement, hitDecrement);
        m_missCounter = new Counter("Miss", missIncrement, missDecrement);
    }

    @Override
    public void submit() {
        HitMissAction res = new HitMissAction(getLabel(), m_hitCounter.getCounter(), m_missCounter.getCounter());
        Log.v("FORM_RESULT", "Submitting object \"" + getLabel() + "\"; Merging counter "
        + m_hitCounter + " and " + m_missCounter.toString() + " into " + res);

        getRef().setValue(res);

        Log.i("DB_EVENT", "Submitted HitMiss Counter \"" + getLabel() + "\"" + " to " +
                "path " + getPath() + " under value " + m_missCounter.getCounter() + "-" + m_missCounter);
    }
}
