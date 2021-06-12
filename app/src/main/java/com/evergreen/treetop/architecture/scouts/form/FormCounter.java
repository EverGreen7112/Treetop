package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

public class FormCounter extends  FormObject {

    final Counter m_counter;
    String m_label;

    @SuppressLint("SetTextI18n")
    public FormCounter(String label, String path, TextView counter, TextView decrementer) {
        super(label, path);
        m_counter = new Counter(label, counter, decrementer);
    }

    @Override
    protected Object getValue() {
        return m_counter.getCounter();
    }

    @Override
    protected String getType() {
        return "Counter";
    }
}
