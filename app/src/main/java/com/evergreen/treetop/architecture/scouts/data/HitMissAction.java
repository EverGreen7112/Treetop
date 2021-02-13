package com.evergreen.treetop.architecture.scouts.data;

import android.util.Log;

import com.evergreen.treetop.architecture.scouts.utils.Loggable;

public class HitMissAction implements Loggable {
    private int m_hit;
    private int m_miss;
    private String m_label;

    public HitMissAction(String label, int hit, int miss) {
        m_label = label;
        m_hit = hit;
        m_miss = miss;
    }

    public int getHit() {
        return m_hit;
    }

    public int getMiss() {
        return m_miss;
    }

    public double getHitToMiss() {
        if (m_miss == 0) return -1;
        return (double) m_hit / m_miss;
    }

    public int getAttempts() {
        return m_hit + m_miss;
    }

    public double getHitToAttempts() {
        if (getAttempts() == 0) return -1;
        return (double) m_hit / getAttempts();
    }

    @Override
    public String getLabel() {
        return m_label;
    }

    @Override
    public String toString() {
        return "Hit-Miss Action \"" + getLabel() + "\" at " + m_hit + " / " + m_miss;
    }
}
