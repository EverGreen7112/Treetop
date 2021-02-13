package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.utils.Loggable;

public class Counter implements Loggable {

    private int m_counter = 0;
    private TextView m_view;
    private String m_label;
    private TextView m_valueVew;

    @SuppressLint("SetTextI18n")
    public Counter(String label, TextView counter, TextView decrementor) {
        counter.setText(m_counter + " " + label);
        m_label = label;
        m_view = counter;
        m_valueVew = counter;

        counter.setOnClickListener((v) -> {
                    m_counter++;
                    updateText();
                }
        );

        decrementor.setText("-");
        decrementor.setOnClickListener((v) -> {
            m_counter = Math.max(0, m_counter - 1);
            updateText();
        });

        Log.i("UI_EVENT", "Initialized " + toString());
    }

    public void setCounter(int value) {
        m_counter = value;
        Log.i("DATA_EVENT", toString() + " set to " + value);
        updateText();
    }

    private void updateText() {
        m_valueVew.setText(m_counter + " " + getLabel());
        Log.i("UI_EVENT", "Setting " + toString()  + " count text to " + m_counter);
    }

    public int getCounter() {
        return m_counter;
    }

    @Override
    public String getLabel() {
        return m_label;
    }

    @Override
    public String toString() {
        return "Counter \"" + getLabel() + "\" at " + m_counter ;
    }
}
