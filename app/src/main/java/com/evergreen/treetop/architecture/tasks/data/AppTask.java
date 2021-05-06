package com.evergreen.treetop.architecture.tasks.data;

import android.widget.ArrayAdapter; // This one is for the javadoc in displayStr()

import androidx.annotation.NonNull;

import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.FirebaseTask;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class AppTask extends Goal {
    private String m_parentId;
    private LocalDate m_startDeadline;
    private LocalDate m_endDeadline;
    private User m_assigner;
    private Set<User> m_assignees;

    public AppTask(int priority, String id, String title, String description, Unit unit, String parentId,
                   LocalDate startDeadline, LocalDate endDeadline, User assigner) {
        super(priority, id, title, description, unit);
        m_parentId = parentId;
        m_startDeadline = startDeadline;
        m_endDeadline = endDeadline;
        m_assigner = assigner;
        m_assignees = new HashSet<>();
    }

    public static AppTask of(FirebaseTask task) throws ExecutionException, InterruptedException {
        AppTask res = new AppTask(
                task.getPriority(),
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                new Unit(task.getId()),  // UnitDB.getInstance().getUnitById(task.getUnitId()),
                task.getParentId(),
                LocalDate.ofEpochDay(task.getStartDeadlineEpoch()),
                LocalDate.ofEpochDay(task.getEndDeadlineEpoch()),
                UserDB.getInstance().getUserById(task.getAssignerId())
        );

        return res;
    }

    @Deprecated
    /**
     * Use for testing only, should remove once tasks can be marked done via app
     */
    public AppTask(int priority, String id, String title, String description, Unit unit, String parentId,
                   LocalDate startDeadline, LocalDate endDeadline, User assigner, boolean completed) {
        this(priority, id, title, description, unit, parentId, startDeadline, endDeadline, assigner);
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

    public LocalDate getStartDeadline() {
        return m_startDeadline;
    }

    public void setStartDeadline(LocalDate startDeadline) {
        m_startDeadline = startDeadline;
    }

    public LocalDate getEndDeadline() {
        return m_endDeadline;
    }

    public void setEndDeadline(LocalDate endDeadline) {
        m_endDeadline = endDeadline;
    }
    public Goal getParent() {
        return Goal.of(m_parentId);
    }

    public Goal getGoal() {

        Goal iterationGoal = this;

        while (iterationGoal instanceof AppTask) {
            iterationGoal = ((AppTask)iterationGoal).getParent();
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



    @Override
    @NonNull
    public String toString() {
        return "Task \"" + getTitle() + "\"  " + getIcon() + ", id" + getId();
    }
}
