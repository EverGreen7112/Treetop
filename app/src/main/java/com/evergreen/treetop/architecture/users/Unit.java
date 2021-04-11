package com.evergreen.treetop.architecture.users;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Unit {


    private final FirebaseUnitContainer m_dbInterface;

    Unit(FirebaseUnitContainer dbInterface) {
        m_dbInterface = dbInterface;
    }

    public String getName() {
        return m_dbInterface.getName();
    }

    public void setName(String name) {
        m_dbInterface.setName(name);
    }

    public User getLeader() {
        return User.of(m_dbInterface.getLeaderId());
    }

    public void setLeader(User leader) {
        m_dbInterface.setLeader(leader.getID());
    }


    public String getID() {
        return m_dbInterface.getID();
    }

    public Unit getParent() {
        return Unit.of(m_dbInterface.getParentId());
    }

    public List<Unit> getChildren() {
        return m_dbInterface.getChildrenID().stream()
                .map(Unit::of)
                .collect(Collectors.toList());
    }


   private Set<User> getUsers(Unit unit, Set<User> res) {
        if (unit instanceof User) {
            res.add((User)unit);
        } else {
            for (Unit child : getChildren()) {
                res.addAll(getUsers(child, res));
            }
        }

        return res;
   }

    public Set<User> getUsers() {
        return getUsers(this, new HashSet<>());
    }


    public void addChild(Unit unit) throws IllegalAccessException {
        if (UnitDB.getInstance().contains(unit.getID())) {
            throw new IllegalArgumentException("Tried to add child unit to " + toString()
                    + ", but its id, \"" + unit.getID() + "\", does not exist in the Unit databases");
        }

        m_dbInterface.addChild(unit.getID());

    }

   public void removeChild(Unit unit) {

        if (! m_dbInterface.getChildrenID().contains(unit.getID())) {
            throw new IllegalArgumentException("Tried to remove child " + unit.toString() +
                    " from " + toString() + ", but it has no such child!");
        }

        m_dbInterface.removeChildId(unit.getID());
   }


   public boolean isRoot() {
        return m_dbInterface.getParentId() == null && ! (this instanceof User);
   }

   public DatabaseReference getRef() {
        return UnitDB.getInstance().getRef().child(m_dbInterface.getID());
   }

   public void push() {
        getRef().setValue(m_dbInterface);
   }

    @Override
    @NonNull
    public String toString() {
        return "Unit \"" + getName() + "\", id " + getID();
    }

    public static Unit of(String id) {
        final Unit[] res = new Unit[1]; // Wrapper for res
        UnitDB.getInstance().getRef().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUnitContainer firebaseUnit = snapshot.getValue(FirebaseUnitContainer.class);
                res[0] = new Unit(firebaseUnit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return res[0];

    }
}
