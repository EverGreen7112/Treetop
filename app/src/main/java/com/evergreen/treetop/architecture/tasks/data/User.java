package com.evergreen.treetop.architecture.tasks.data;

import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.utils.DBUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class User {

    String m_id;
    String m_name;
    List<String> m_unitIds;
    List<String> m_leadingIds;


    public User(String id, String name) {
        m_id = id;
        m_name = name;
        m_unitIds = new ArrayList<>();
        m_leadingIds = new ArrayList<>();
    }

    public static User of(DBUser dbUser) {
        User res = new User(dbUser.getId(), dbUser.getName());

        res.m_unitIds.addAll(dbUser.getUnitIds());
        res.m_leadingIds.addAll(dbUser.getLeadingIds());

        return res;
    }

    public String getId() {
        return m_id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public List<String> getUnitIds() {
        return m_unitIds;
    }

    public boolean isLeading(Unit unit) {
        return unit.getLeaderId().equals(getId());
    }
    public List<String> getLeadingIds() {
        return m_leadingIds;
    }
    public List<Unit> getLeadingUnits() throws InterruptedException, ExecutionException, NoSuchDocumentException {
        List<Unit> res = new ArrayList<>();

        for (String unitId : m_leadingIds) {
            res.add(UnitDB.getInstance().awaitUnit(unitId));
        }

        return res;
    }

    public void addUnit(String unitId, boolean isLeading) {
        m_unitIds.add(unitId);
        if (isLeading) {
            m_leadingIds.add(unitId);
        }
    }

}
