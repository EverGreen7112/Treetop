package com.evergreen.treetop.architecture.scouts.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StrategyOptions {

    private List<StrategyOption> m_options;

    public List<StrategyOption> getOptions() {
        return m_options;
    }

    public StrategyType getType() {
        return m_type;
    }

    private  StrategyType m_type;

    public static StrategyOptions TEAM;
    public static StrategyOptions ALLIANCE;

    static {
        Map<String, String> options = new HashMap<>();

        options.put("Defence", "Tried to physically prevent other robots from preforming tasks");
        options.put("Bottom Shoots", "Tried to give a burst of shots to the bottom target, no aiming");
        options.put("Quantity Shoots", "Tried to collect and throw as many power cells as they without a very good aim");
        TEAM = new StrategyOptions(StrategyType.TEAM, options);

        options.clear();
        options.put("Guarded Shooting", "Tried to hit target with one robot doing defense");
        options.put("Multi Climb", "Relied on multi robot climbing");
        options.put("Third Stage", "Tried to reach higher stages");
        ALLIANCE = new StrategyOptions(StrategyType.ALLIANCE, options);
    }

    private StrategyOptions(StrategyType type, Map<String, String> options) {
        m_options = options.entrySet().stream()
                .map(entry -> new StrategyOption(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        m_type = type;
    }

    public static class StrategyOption {
        private String m_key;
        private String m_description;

        public String getKey() {
            return m_key;
        }

        public String getDescription() {
            return m_description;
        }

        public StrategyOption(String key, String description) {
            m_key = key;
            m_description = description;
        }

    }

    public enum StrategyType {
        TEAM,
        ALLIANCE
    }
}
