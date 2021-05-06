package com.evergreen.treetop.architecture.tasks.utils;

import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FirebaseTask extends FirebaseGoal {

    private String m_parentId;
    private String m_assignerId;
    private long m_startDeadlineEpoch;
    private long m_endDeadlineEpoch;
    private List<String> m_assigneeIds;

    public FirebaseTask() {}

    public FirebaseTask(int priority, String id, String title, String description, String unitId, String parentId,
                long startDeadlineEpoch, long endDeadlineEpoch, String assignerId) {
        super(priority, id, title, description, unitId);
        m_parentId = parentId;
        m_startDeadlineEpoch = startDeadlineEpoch;
        m_endDeadlineEpoch = endDeadlineEpoch;
        m_assignerId = assignerId;
        m_assigneeIds = new ArrayList<>();
    }


    public FirebaseTask(int priority, String id, String title, String description, String unitId, String parentId,
                        long startDeadlineEpoch, long endDeadlineEpoch, String assignerId, List<String> assigneeIds) {
        this(priority, id, title, description, unitId, parentId, startDeadlineEpoch, endDeadlineEpoch, assignerId);
        m_assigneeIds = assigneeIds;
    }

    public static FirebaseTask of(AppTask task) {
        return new FirebaseTask(
                task.getPriority(),
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getUnit().getId(),
                task.getParent().getId(),
                task.getStartDeadline().toEpochDay(),
                task.getEndDeadline().toEpochDay(),
                task.getAssigner().getId(),
                task.getAssignees().stream().map(User::getId).collect(Collectors.toList())
        );
    }


    public long getStartDeadlineEpoch() {
        return m_startDeadlineEpoch;
    }

    public void setStartDeadlineEpoch(int startDeadlineEpoch) {
        this.m_startDeadlineEpoch = startDeadlineEpoch;
    }



    public long getEndDeadlineEpoch() {
        return m_endDeadlineEpoch;
    }

    public void setEndDeadlineEpoch(int endDeadlineEpoch) {
        this.m_endDeadlineEpoch = endDeadlineEpoch;
    }

    public String getAssignerId() {
        return m_assignerId;
    }

    public void setAssignerId(String assignerId) {
        this.m_assignerId = assignerId;
    }

    public List<String> getAssigneeIds() {
        return m_assigneeIds;
    }

    public void setAssigneeIds(List<String> assigneeIds) {
       m_assigneeIds = assigneeIds;
    }

    public String getParentId() {
        return m_parentId;
    }

    public void setParentId(String parentId) {
        m_parentId = parentId;
    }

}
