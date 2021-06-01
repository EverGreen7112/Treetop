package com.evergreen.treetop.architecture.tasks.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DBUser implements Serializable {

    String m_id;
    String m_name;
    List<String> unitIds;

    public DBUser() {}

    public DBUser(String id, String name) {
        m_id = id;
        m_name = name;
        unitIds = new ArrayList<>();
    }

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public List<String> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<String> unitIds) {
        this.unitIds = unitIds;
    }
}
