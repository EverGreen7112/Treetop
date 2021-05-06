package com.evergreen.treetop.architecture.tasks.data;

import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseGoal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Goal {
    private int m_priority;
    private boolean m_completed;
    private String m_id;
    private String m_title;
    private String m_description;
    private Set<String> m_subtaskIds;
    private Unit m_unit;

    public final static String COMPLETE_ICON = "✓"; // ✔
    public final static String INCOMPLETE_ICON = "";

    public static Goal of(String id) {
        return Utilities.dummyGoal(id);
    }
    
    public static Goal of(FirebaseGoal goal) throws ExecutionException, InterruptedException {
        Goal result = new Goal(
                goal.getPriority(),
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                UnitDB.getInstance().getUnitById(goal.getUnitId())
        );
        
        goal.getSubtaskIds().forEach(result::addTaskById);

        return result;
        
    }

    public Goal(int priority, String id, String title, String description, Unit unit) {
        m_priority = priority;
        m_id = id;
        m_title = title;
        m_description = description;
        m_unit = unit;
        m_subtaskIds = new HashSet<>();
        m_completed = false;
    }

    public int getPriority() {
        return m_priority;
    }
    public void setPriority(int priority) {
        m_priority = priority;
    }

    public boolean isCompleted() {
        return m_completed;
    }
    public void setCompleted(boolean completed) {
        this.m_completed = completed;
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

    public List<AppTask> getSubtasks() {
        // Return dummy tasks for testing
        return m_subtaskIds.stream().map(Utilities::dummyTask).collect(Collectors.toList()); //TODO actually implement
    }

    public void addTask(AppTask subtask) {
        m_subtaskIds.add(subtask.getId());
    }

    public void addTaskById(String taskId) {
        m_subtaskIds.add(taskId);
    }

    public void removeSubtask(AppTask subtask) {
        m_subtaskIds.remove(subtask.getId());
    }

    public int getCompletedCount() {
        return (int)getSubtasks().stream().filter(AppTask::isCompleted).count();
    }

    public int getTaskCount() {
        return m_subtaskIds.size();
    }

    public String priorityChar() {
        return Utilities.priorityChar(getPriority());
    }

    public String getIcon() {
        return isCompleted() ? COMPLETE_ICON : INCOMPLETE_ICON;
    }
}
