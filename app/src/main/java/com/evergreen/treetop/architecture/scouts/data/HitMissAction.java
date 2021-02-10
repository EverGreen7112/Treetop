package com.evergreen.treetop.architecture.scouts.data;

public class HitMissAction {
    private int m_hit;
    private int m_miss;

    public HitMissAction(int hit, int miss) {
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
}
