package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.utils.Loggable;

public class Counter implements Loggable {

    private  int m_counter = 0;
    private String m_label;

    @SuppressLint("SetTextI18n")
    public Counter(String label, TextView counter, TextView decrementor) {
        counter.setText(m_counter + " " + label);
        m_label = label;

        counter.setOnClickListener((v) -> {
                    m_counter++;
                    counter.setText(m_counter + " " + label);
                }
        );

        decrementor.setText("-");
        decrementor.setOnClickListener((v) -> {
            m_counter = Math.max(0, m_counter - 1);
            counter.setText(m_counter + " " + label);
        });
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
