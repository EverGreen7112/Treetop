package com.evergreen.treetop.architecture.tasks.data;

import java.util.List;

public class User {

    String m_id;
    String m_name;
    List<String> unitIds;


    public User(String id, String name) {
        m_id = id;
        m_name = name;
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
        return unitIds;
    }

}
