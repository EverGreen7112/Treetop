package com.evergreen.treetop.architecture.scouts.form;

import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class NumberBox extends FormObject {
    private final EditText m_inputBox;

    public NumberBox(String label, String path, EditText inputBox, TextView labelView) {
        super(label, path);
        labelView.setText(label);

        m_inputBox = inputBox;
        m_inputBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        Log.i("FORM_OBJECT", "Initialized new NumberBox \"" + getLabel() + "\" at path " + getPath());
    }

    @Override
    protected Object getValue() {
        return Integer.parseInt(m_inputBox.getText().toString());
    }

    @Override
    protected String getType() {
        return "Number-Box";
    }

}
