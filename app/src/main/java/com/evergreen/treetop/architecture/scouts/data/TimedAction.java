package com.evergreen.treetop.architecture.scouts.data;

import android.util.Log;

import java.util.Date;

public class TimedAction {
    private int m_startTime;
    private int m_endTime;
    private String m_label;

    public TimedAction(String label, int start, int end) {
        m_startTime = start;
        m_endTime = end;
        m_label = label;
        Log.i("DATA_OBJECT", "Created new TimedAction \"" + label + "\" , from " +  m_startTime + " to " + m_endTime);
    }

    public int getStart() {
        return m_startTime;
    }

    public int getEnd() {
        return m_endTime;
    }

    public int getDuration() {
        return m_endTime - m_startTime;
    }

    @Override
    public String toString() {
        return "TimedAction " + m_label + " from " + m_startTime + " to " + m_endTime;
    }
}
