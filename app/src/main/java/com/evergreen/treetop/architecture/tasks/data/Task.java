package com.evergreen.treetop.architecture.tasks.data;

import android.widget.ArrayAdapter; // This one is for the javadoc in displayStr()
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Task extends Goal {
    private Goal m_parent;
    private LocalDateTime m_startDeadline;
    private LocalDateTime m_endDeadline;
    private User m_assigner;
    private Set<User> m_assignees;

    public Task(int priority, String id, String title, String description, Unit unit, Goal parent,
                LocalDateTime startDeadline, LocalDateTime endDeadline, User assigner) {
        super(priority, id, title, description, unit);
        m_parent = parent;
        m_startDeadline = startDeadline;
        m_endDeadline = endDeadline;
        m_assigner = assigner;
        m_assignees = new HashSet<>();
    }


    @Deprecated
    /**
     * Use for testing only, should remove once tasks can be marked done via app
     */
    public Task(int priority, String id, String title, String description, Unit unit, Goal parent,
                LocalDateTime startDeadline, LocalDateTime endDeadline, User assigner, boolean completed) {
        this(priority, id, title, description, unit, parent, startDeadline, endDeadline, assigner);
        setCompleted(completed);
    }

    /**
     * By default {@link ArrayAdapter} uses {@link #toString} to determine how to display the
     * object as text. However, toString is conventionally used for debugging, where we might want
     * to add other details. Thus, this method is used to provide a textual representation of the
     * task to display on listViews, etc.
     *
     * @return a textual representation of the task to display in list views.
     */
    public String listDisplayString() {
        return getTitle();
    }

    public LocalDateTime getStartDeadline() {
        return m_startDeadline;
    }

    public void setStartDeadline(LocalDateTime startDeadline) {
        m_startDeadline = startDeadline;
    }

    public LocalDateTime getEndDeadline() {
        return m_endDeadline;
    }

    public void setEndDeadline(LocalDateTime endDeadline) {
        m_endDeadline = endDeadline;
    }
    public Goal getParent() {
        return m_parent;
    }

    public Goal getGoal() {

        Goal iterationGoal = this;

        while (iterationGoal instanceof Task) {
            iterationGoal = ((Task)iterationGoal).getParent();
        }

        return iterationGoal;
    }

    public User getAssigner() {
        return m_assigner;
    }

    public Set<User> getAssignees() {
        return m_assignees;
    }

    public void addAssignee(User assignee) {
        m_assignees.add(assignee);
    }

    public void removeAssignee(User assignee) {
        m_assignees.remove(assignee);
    }

}
