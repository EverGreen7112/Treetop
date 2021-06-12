package com.evergreen.treetop.architecture.scouts.form;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.evergreen.treetop.R;

import java.util.Map;

public class FormRadio extends FormObject {

    final RadioGroup m_radio;
    final Map<Integer, Object> m_idToValue;

    @SuppressLint("SetTextI18n")
    public FormRadio(String label, String path, RadioGroup radio, Map<Integer, Object> idToValue) {
        super(label, path);
        m_radio = radio;
        m_idToValue = idToValue;
    }

    @Override
    protected Object getValue() {
        return m_idToValue.get(m_radio.getCheckedRadioButtonId());
    }

    @Override
    protected String getType() {
        return "Radio";
    }
}
