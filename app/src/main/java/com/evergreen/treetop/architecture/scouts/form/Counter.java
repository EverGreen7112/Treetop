package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.widget.TextView;

public class Counter {

    private  int m_counter = 0;

    @SuppressLint("SetTextI18n")
    public Counter(String label, TextView counter, TextView decrementor) {
        counter.setText(m_counter + " " + label);

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
}
