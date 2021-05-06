package com.evergreen.treetop.architecture.tasks.utils;

import com.evergreen.treetop.architecture.tasks.data.Goal;

import java.util.List;

public class FirebaseGoal {
    private int m_priority;
    private String m_id;
    private String m_title;
    private String m_description;
    private String m_unitId;
    private List<String> subtaskIds;

    public FirebaseGoal() {}

    public FirebaseGoal(int priority, String id, String title, String description, String unitId) {
        m_priority = priority;
        m_id = id;
        m_title = title;
        m_description = description;
        m_unitId = unitId;
    }

    public static FirebaseGoal of(Goal goal) {
        return new FirebaseGoal(
              goal.getPriority(),
              goal.getId(),
              goal.getTitle(),
              goal.getDescription(),
              goal.getUnit().getId()
        );
    }

    public int getPriority() {
        return m_priority;
    }

    public void setPriority(int priority) {
        this.m_priority = priority;
    }

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        this.m_id = id;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public String getUnitId() {
        return m_unitId;
    }

    public void setUnitId(String unitId) {
        this.m_unitId = unitId;
    }

    public List<String> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<String> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

}
