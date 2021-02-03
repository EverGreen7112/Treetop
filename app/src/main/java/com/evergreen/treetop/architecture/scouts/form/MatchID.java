package com.evergreen.treetop.architecture.scouts.form;

import java.util.Objects;

public class MatchID {

    enum MatchType {
        QUAL,
        PLAYOFF;
    }

    private MatchType m_type;
    private int m_number;

    public MatchID(MatchType type, int number) {
        m_type = type;
        m_number = number;
    }

    public MatchType getType() {
        return  m_type;
    }

    public int getNumber() {
        return m_number;
    }

    public boolean after(MatchID match) {
        if (m_type.compareTo(match.getType()) == 0) {
            return m_number > match.getNumber();
        }

        return  m_type.compareTo(match.getType()) > 0;
    }

    public boolean before(MatchID match) {
        if (m_type.compareTo(match.getType()) == 0) {
            return m_number < match.getNumber();
        }

        return  m_type.compareTo(match.getType()) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchID)) return false;
        MatchID matchID = (MatchID) o;
        return m_number == matchID.m_number &&
                m_type == matchID.m_type;
    }

    @Override
    public String toString() {
        return m_type.toString() + "-" + m_number;
    }
}
