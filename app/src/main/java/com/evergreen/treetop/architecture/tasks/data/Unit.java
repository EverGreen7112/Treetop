package com.evergreen.treetop.architecture.tasks.data;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.handlers.UserDB;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Unit {

    private final String m_id;
    private String m_name;
    private String m_description;
    private final String m_parentId;
    private final String m_leaderId;
    private final List<String> m_childrenIds;

    public Unit(String id, String name, String description, String leaderId) {
        this(id, name, description, leaderId, null);
    }

    public Unit(String id, String name, String description, String leaderId, String parentId) {
        m_description = description;
        m_name = name;
        m_id = id;
        m_leaderId = leaderId;
        m_parentId = parentId;
        m_childrenIds = new ArrayList<>();
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public String getId() {
        return m_id;
    }

    public String getLeaderId() {
        return m_leaderId;
    }

    public User getUser() throws InterruptedException, ExecutionException, NoSuchDocumentException {
        return UserDB.getInstance().awaitUser(m_leaderId);
    }

    public String getParentId() {
        return m_parentId;
    }

    public Unit getParent() throws InterruptedException, ExecutionException, NoSuchDocumentException {
        return UnitDB.getInstance().awaitUnit(getParentId());
    }

    public List<String> getChildrenIds() {
        return m_childrenIds;
    }

    public List<Unit> getChildren() throws ExecutionException, InterruptedException {
        if (getChildrenIds().size() == 0) return new ArrayList<>();
        return UnitDB.getInstance().getByIds(getChildrenIds());
    }

    public void addChild(String id) {
        m_childrenIds.add(id);
    }

    public void removeChild(String id) {
        m_childrenIds.remove(id);
    }

    public boolean isRootUnit() {
        return m_parentId == null;
    }

    public static Unit of(DBUnit dbUnit) {
        Unit res = new Unit(
                dbUnit.getId(),
                dbUnit.getName(),
                dbUnit.getDescription(),
                dbUnit.getLeaderId(),
                dbUnit.getParentId()
        );

        if (dbUnit.getChildrenIds() != null) {
            dbUnit.getChildrenIds().forEach(res::addChild);
        }

        return res;
    }
}
