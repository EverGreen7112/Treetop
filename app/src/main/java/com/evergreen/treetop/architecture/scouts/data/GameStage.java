package com.evergreen.treetop.architecture.scouts.data;

import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;

public enum GameStage {
    AUTO("Autonomous", 0, 15_000),
    TELEOP("Teleop", 15_000, 2*60_000 + 30_000),
    ENDGAME("Endgame", 2*60_000 + 30_000, 3*60_000);

    private final String m_name;
    private final int m_start;
    private final int m_end;

    GameStage(String name, int start, int end) {
        m_name = name;
        m_start = start;
        m_end = end;
    }

    public String getName() {
        return m_name;
    }

    public int getStart() {
        return m_start;
    }

    public int getEnd() {
        return m_end;
    }

    public boolean contains(int matchMillis) {
        return m_start <= matchMillis && matchMillis < m_end;
    }

    public static GameStage stageIn(int matchMillis) {
        if (AUTO.contains(matchMillis)) return AUTO;
        if (TELEOP.contains(matchMillis)) return TELEOP;
        return ENDGAME;
    }

    public static GameStage getCurrentStage() {
        return stageIn(ScoutingMatch.getCurrent().getTimeSinceStart());
    }


}
