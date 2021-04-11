package com.evergreen.treetop.architecture.users;

import com.google.firebase.auth.FirebaseUser;

public class _User {

    private String m_email;
    private String m_name;
    private String m_uid;

    public _User(String email, String name, String uid) {
        m_email = email;
        m_name = name;
        m_uid = uid;
    }

    public _User(FirebaseUser user) {
        this(user.getEmail(), user.getDisplayName(), user.getUid());
    }

    public String getEmail() {
        return m_email;
    }

    public void setEmail(String email) {
        m_email = email;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getUid() {
        return m_uid;
    }

    public void setUid(String uid) {
        m_uid = uid;
    }

    @Override
    public String toString() {
        return m_name;
    }
}
