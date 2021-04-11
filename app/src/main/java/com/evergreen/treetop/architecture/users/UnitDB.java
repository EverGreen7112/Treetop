package com.evergreen.treetop.architecture.users;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;

public class UnitDB {

    private final DatabaseReference m_ref = FirebaseDatabase.getInstance().getReference("units");
    private static final UnitDB m_instance = new UnitDB();


    private UnitDB() { }

    public DatabaseReference getRef() {
        return m_ref;
    }



    public void add(Unit unit) {
        getRef().child(unit.getID()).setValue(unit);
    }

    public boolean contains(String id) {

        final Set<String>[] ids = new Set[1];

        getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ids[0] = snapshot.getValue(new GenericTypeIndicator<HashMap<String, String>>() {
                }).keySet();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return id.contains(id);
    }

    public static UnitDB getInstance() {
        return m_instance;
    }

}
