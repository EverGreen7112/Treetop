package com.evergreen.treetop.architecture.users;

import java.util.List;

class FirebaseUserContainer extends FirebaseUnitContainer {
    private List<String> m_unitIds;
    private List<String> m_ledUnitIds;
    private long m_birthdayEpochDays;
    private String m_email;
    private String m_name;
    private String m_phoneNumber;

    public List<String> getUnitIds() {
        return m_unitIds;
    }

    public void setUnitIds(List<String> unitIds) {
        m_unitIds = unitIds;
    }

    public List<String> getLedUnitIds() {
        return m_ledUnitIds;
    }

    public void setLedUnitIds(List<String> ledUnitIds) {
        m_ledUnitIds = ledUnitIds;
    }

    public long getBirthdayEpochDays() {
        return m_birthdayEpochDays;
    }

    public void setBirthdayEpochDays(long birthdayMillis) {
        m_birthdayEpochDays = birthdayMillis;
    }

    public String getEmail() {
        return m_email;
    }

    public void setEmail(String email) {
        m_email = email;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public void setName(String name) {
        m_name = name;
    }

    public String getPhoneNumber() {
        return m_phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        m_phoneNumber = phoneNumber;
    }

    public void addLedUnitId(String id) {
        m_ledUnitIds.add(id);
    }

    public void removeLedUnitId(String id) {
        m_ledUnitIds.remove(id);
    }

    public void addUnitId(String id) {
        m_unitIds.add(id);
    }

    public void removeUnitId(String id) {
        m_unitIds.remove(id);
    }

    public FirebaseUserContainer(List<String> unitIds, List<String> ledUnitIds, long birthdayEpochDays, String email, String name, String phoneNumber) {
        m_unitIds = unitIds;
        m_ledUnitIds = ledUnitIds;
        m_birthdayEpochDays = birthdayEpochDays;
        m_email = email;
        m_name = name;
        m_phoneNumber = phoneNumber;
    }

    public FirebaseUserContainer() {}
}
