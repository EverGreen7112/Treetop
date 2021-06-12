package com.evergreen.treetop.architecture.scouts.form;

import android.util.Log;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class FormSwitch extends FormObject {

    private final SwitchMaterial m_switch;

    public FormSwitch(String label, String path, SwitchMaterial switchView) {
        super(label, path);
        m_switch = switchView;
    }

    public FormSwitch(String label, String path, SwitchMaterial switchView, TextView labelView) {
        this(label, path, switchView);
        labelView.setText(label);
    }

    @Override
    protected Object getValue() {
        return m_switch.isChecked();
    }

    @Override
    protected String getType() {
        return "Switch";
    }

}
