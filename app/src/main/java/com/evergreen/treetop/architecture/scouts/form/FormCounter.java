package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FormCounter extends  FormObject {

    Counter m_counter;
    String m_label;

    @SuppressLint("SetTextI18n")
    public FormCounter(String label, String path, TextView counter, TextView decrementor) {
        super(label, path);
        m_counter = new Counter(label, counter, decrementor);

        Log.i("FORM_OBJECT", "Initialized Counter \"" + getLabel() + "\" at path "
        + getPath());
    }

    @Override
    public void submit() {
        getRef().setValue(m_counter.getCounter());
    }

}
