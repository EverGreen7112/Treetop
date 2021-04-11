package com.evergreen.treetop.architecture.users;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;
import java.util.stream.Collectors;

public class User extends Unit {

    private final FirebaseUserContainer m_dbInterface;

    User(FirebaseUserContainer dbInterface) {
        super(dbInterface);
        m_dbInterface = dbInterface;
    }

    public Set<Unit> getUnits() {
        return m_dbInterface.getUnitIds().stream()
                .map(Unit::of)
                .collect(Collectors.toSet());
    }

    public void addUnit(Unit unit) {
        if (! m_dbInterface.getUnitIds().contains(unit.getID())) {
            throw new IllegalArgumentException();
        }
        m_dbInterface.setLedUnitIds(m_dbInterface.getLedUnitIds());
    }

    public Set<Unit> getUnitsLed() {
        return m_dbInterface.getLedUnitIds().stream()
                .map(Unit::of)
                .collect(Collectors.toSet());
    }

    public void setUnitLed(Unit unit) {
        if (! m_dbInterface.getUnitIds().contains(unit.getID())) {
            throw new IllegalArgumentException("Tried to set " + unit.toString() + " as a led unit " +
                    "for " + toString() + ", but  it is in no such unit!");
        } else {
            m_dbInterface.addLedUnitId(unit.getID());
        }
    }

    public LocalDate getBirthday() {
        return LocalDate.ofEpochDay(m_dbInterface.getBirthdayEpochDays());
    }

    public int getAge() {
        return Period.between(getBirthday(), LocalDate.now()).getYears();
    }


    @Override
    public void addChild(Unit unit)  {
        throw new UnpermittedActionException("Tried to add children to a user (" + toString() + ")!");
    }


    @Override
    public void removeChild(Unit unit) {
        throw new UnpermittedActionException("Tried to add children to a user (" + toString() + ")!");
    }

    @Override
    @NonNull
    public String toString() {
        return "User \"" + getName() + "\", id " + getID();
    }

    public static User of(String id) {
        Unit unit = Unit.of(id);

        if (! (unit instanceof User)) {
            throw new IllegalArgumentException("Tried to get user by id " + id
                    + ", but it was a unit, not a user!");
        }

        return (User)unit;
    }


    public static class UnpermittedActionException extends RuntimeException {
        public UnpermittedActionException(String msg) {
            super(msg);
        }
    }

}
