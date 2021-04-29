package com.evergreen.treetop.architecture.tasks.utils;

import java.util.List;

public class FirebaseGoal {
    private int m_priority;
    private int m_startDeadlineEpoch;
    private int m_endDeadlineEpoch;
    private String m_id;
    private String m_title;
    private String m_description;
    private String m_unitId;
    private String m_assignerId;
    private List<String> assigneeIds;
    private List<String> subtaskIds;

    public FirebaseGoal(int priority, int startDeadlineEpoch, int endDeadlineEpoch, String id, String title, String unitId, String assignerId) {
        m_priority = priority;
        m_startDeadlineEpoch = startDeadlineEpoch;
        m_endDeadlineEpoch = endDeadlineEpoch;
        m_id = id;
        m_title = title;
        m_unitId = unitId;
        m_assignerId = assignerId;
    }

    public int getPriority() {
        return m_priority;
    }

    public void setPriority(int priority) {
        this.m_priority = priority;
    }

    public int getStartDeadlineEpoch() {
        return m_startDeadlineEpoch;
    }

    public void setStartDeadlineEpoch(int startDeadlineEpoch) {
        this.m_startDeadlineEpoch = startDeadlineEpoch;
    }

    public int getEndDeadlineEpoch() {
        return m_endDeadlineEpoch;
    }

    public void setEndDeadlineEpoch(int endDeadlineEpoch) {
        this.m_endDeadlineEpoch = endDeadlineEpoch;
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

    public String getAssignerId() {
        return m_assignerId;
    }

    public void setAssignerId(String assignerId) {
        this.m_assignerId = assignerId;
    }

    public List<String> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<String> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }

    public List<String> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<String> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

}
