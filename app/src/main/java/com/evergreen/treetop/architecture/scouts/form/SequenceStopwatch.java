package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.data.TimedAction;
import com.evergreen.treetop.architecture.scouts.utils.RepeatListener;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;

import java.util.ArrayList;
import java.util.Locale;

public class SequenceStopwatch extends FormObject {

    private ArrayList<TimedAction> m_sequence = new ArrayList<>();
    private int m_lastStart;
    private int m_counter = 1500;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public SequenceStopwatch(String label, String path, TextView stopwatch, TextView labelView) {
        super(label, path);
        labelView.setText(label);
        stopwatch.setOnTouchListener(new RepeatListener(
                getLabel() + " counter",
                20,
                10,
                v -> m_lastStart = ScoutingMatch.getCurrent().getTimeSinceStart(),
                v -> {
                    m_counter++;
                    String newText = String.format(Locale.ENGLISH, "%01.2f", (double)m_counter/100) + "s"; // Format as 0.00s
                    stopwatch.setText(newText);
                    Log.v("FORM_EVENT", "Stopwatch \"" + getLabel() + "\" incremented to " + newText);
                },
                v -> {
                    m_sequence.add(new TimedAction(label, m_lastStart, ScoutingMatch.getCurrent().getTimeSinceStart()));
                    m_counter = 20;
                    stopwatch.setText("0.00s");
                }));
        // TODO Log message
    }

    @Override
    public void submit() {
        getRef().setValue(m_sequence);
        Log.i("DB_EVENT", "Submitted SequenceStopwatch \"" + getLabel() + "\" with value "
                + m_sequence.toString() + " under path " + getPath());
    }
}
