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

    private final ArrayList<TimedAction> m_sequence = new ArrayList<>();
    private int m_lastStart;
    private int m_counter = 0;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public SequenceStopwatch(String label, String path, TextView stopwatch, TextView labelView) {
        super(label, path);
        labelView.setText(label);
        stopwatch.setText("0.00s");

        if (stopwatch.getWidth() < 80 && stopwatch.getHeight() < 80) {
            stopwatch.setWidth(80);
            stopwatch.setHeight(80);
        } else if (stopwatch.getWidth() > stopwatch.getHeight()) {
            stopwatch.setHeight(stopwatch.getWidth());
        } else if (stopwatch.getHeight() > stopwatch.getWidth()) {
            stopwatch.setWidth(stopwatch.getHeight());
        }

        stopwatch.setOnTouchListener(new RepeatListener(
                getLabel() + " counter",
                0,
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
    protected Object getValue() {
        return m_sequence;
    }

    @Override
    protected String getType() {
        return "Sequence Stopwatch";
    }

}
