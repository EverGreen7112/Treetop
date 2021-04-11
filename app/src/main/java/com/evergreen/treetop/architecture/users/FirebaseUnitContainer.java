package com.evergreen.treetop.architecture.users;

import java.util.List;

class FirebaseUnitContainer {

    private String m_name;
    private String m_id;
    private List<String> m_childrenIds;
    private String m_parentId;
    private String  m_leaderId;


    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getLeaderId() {
        return m_leaderId;
    }

    public void setLeader(String leaderId) {
        m_leaderId = leaderId;
    }

    public String getID() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;
    }

    public String getParentId() {
        return m_parentId;
    }

    public void setParentId(String id) {
        m_parentId = id;
    }

    public List<String> getChildrenID() {
        return m_childrenIds;
    }

    public void setChildrenIds(List<String> ids) {
        m_childrenIds = ids;
    }
    public void addChild(String id) {
        m_childrenIds.add(id);
    }
    public void removeChildId(String id) {
        m_childrenIds.remove(id);
    }


}
