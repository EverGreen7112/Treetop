package com.evergreen.treetop.architecture.tasks.data;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Goal {
    private int m_priority;
    private boolean completed;
    private String m_id;
    private String m_title;
    private String m_description;
    private Set<String> m_subtaskIds;
    private Unit m_unit;

    String COMPLETE_ICON = "✓"; //✔
    String INCOMPLETE_ICON = "";


    public Goal(int priority, String id, String title, String description, Unit unit) {
        m_priority = priority;
        m_id = id;
        m_title = title;
        m_description = description;
        m_unit = unit;
        m_subtaskIds = new HashSet<>();
    }

    public int getPriority() {
        return m_priority;
    }
    public void setPriority(int priority) {
        m_priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
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

    public Unit getUnit() {
        return m_unit;
    }
    public void setUnit(Unit unit) {
        m_unit = unit;
    }


    public String getId() {
        return m_id;
    }

    public List<Task> getSubtasks() {
        // Return dummy tasks for testing
        return m_subtaskIds.stream().map(id -> new Task(
                0,
                id,
                id,
                id,
                new Unit("TestUnit"),
                this,
                LocalDateTime.now().plus(Period.of(0, 0, 1)),
                LocalDateTime.now().plus(Period.of(0, 0, 2)),
                new User("TestUser"),
                id.length() > 5
        )).collect(Collectors.toList()); //TODO actually implement
    }

    public void addTask(Task subtask) {
        m_subtaskIds.add(subtask.getId());
    }

    /**
     * Use for testing only, until we'll actually be able interchange tasks and ids
     * @deprecated
     */
    @Deprecated
    public void addTask(String subtask) {
        m_subtaskIds.add(subtask);
    }

    public void removeSubtask(Task subtask) {
        m_subtaskIds.remove(subtask.getId());
    }

    public int getCompletedCount() {
        return (int)getSubtasks().stream().filter(Task::isCompleted).count();
    }

    public int getTaskCount() {
        return m_subtaskIds.size();
    }

    public String priorityChar() {

        switch (getPriority()) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            default:
                return null;
        }
    }

    public String getIcon() {
        return isCompleted() ? COMPLETE_ICON : INCOMPLETE_ICON;
    }
}
