package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.data.TimedAction;
import com.evergreen.treetop.architecture.scouts.utils.RepeatListener;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;

import java.util.Locale;
import java.util.logging.Handler;

public class ContinuousStopwatch extends FormObject {

    private int m_counter = 0;
    private int m_start;
    private int m_end;
    private TextView m_view;

    @SuppressLint("ClickableViewAccessibility")
    public ContinuousStopwatch(String label, String path, TextView view, TextView labelView) {
        super(label, path);
        labelView.setText(label);
        m_view = view;
        m_view.setText("0.00s");


        if (m_view.getWidth() < 80 && m_view.getHeight() < 80) {
            m_view.setWidth(80);
            m_view.setHeight(80);
        } else if (m_view.getWidth() > m_view.getHeight()) {
            m_view.setHeight(m_view.getWidth());
        } else if (m_view.getHeight() > m_view.getWidth()) {
            m_view.setWidth(m_view.getHeight());
        }

        m_view.setOnTouchListener(new RepeatListener(
                getLabel() + " counter",
                20,
                10,
                v -> m_start = ScoutingMatch.getCurrent().getTimeSinceStart(),
                v -> {
                    m_counter++;
                    String newText = String.format(Locale.ENGLISH, "%01.2f", (double)m_counter/100) + "s"; // Format as 0.00s
                    m_view.setText(newText);
                    Log.v("FORM_EVENT", "Stopwatch \"" + getLabel() + "\" incremented to " + newText);
                },
                v -> m_end = ScoutingMatch.getCurrent().getTimeSinceStart()));
        // TODO Log message
    }

    @Override
    public void submit() {
        getRef().setValue(new TimedAction(getLabel(), m_start, m_end));
        // TODO Log message
    }
}
