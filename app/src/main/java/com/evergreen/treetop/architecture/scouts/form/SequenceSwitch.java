package com.evergreen.treetop.architecture.scouts.form;

import android.util.Log;
import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.data.TimedAction;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class SequenceSwitch extends FormObject  {

    private final SwitchMaterial m_switch;
    private final ArrayList<TimedAction> m_sequence = new ArrayList<>();
    /**
     * Keep the time the switch was turned on. If the switch is turned off, should be -1.
     */
    private int m_onStart = -1;

    public SequenceSwitch(String label, String path, SwitchMaterial switchView, TextView labelView) {
        super(label, path);
        m_switch = switchView;
        labelView.setText(label);

        Log.i("FORM OBJECT", "Initialized new SequenceSwitch \"" + getLabel()
                + "\" for path " + getPath());

        m_switch.setOnClickListener((v) -> {
            SwitchMaterial s = (SwitchMaterial)v;
            if (s.isChecked()) {
                m_onStart = ScoutingMatch.getCurrent().getTimeSinceStart();
                Log.i("FORM_EVENT", "SequenceSwitch " + getLabel() + " turned ON. " +
                        "saving on time to " + m_onStart);
            } else {
                TimedAction action = new TimedAction(getLabel(), m_onStart, ScoutingMatch.getCurrent().getTimeSinceStart());
                Log.d("SEQUENCE_SWITCH", "Timed action " + action.toString() + " created");
                m_sequence.add(action);
                Log.d("SEQUENCE_SWITCH", "Timed action " + action.toString() + "added to list.");
                Log.i("FORM_EVENT", "SequenceSwitch \"" + getLabel() + "\" turned OFF. " +
                        "saving on time to " + m_onStart);
                m_onStart = -1;

            }
        });
    }

    @Override
    protected Object getValue() {
        return m_sequence;
    }

    @Override
    protected String getType() {
        return "Sequence Switch";
    }

}
