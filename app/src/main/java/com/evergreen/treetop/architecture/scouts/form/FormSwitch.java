package com.evergreen.treetop.architecture.scouts.form;

import android.util.Log;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class FormSwitch extends FormObject {

    private SwitchMaterial m_switch;

    public FormSwitch(String label, String path, SwitchMaterial switchView, TextView labelView) {
        super(label, path);
        m_switch = switchView;
        labelView.setText(label);
        Log.i("FORM_OBJECT", "Initialized FormSwitch " + getLabel() + " to path " + getPath());
    }


    @Override
    public void submit() {
        setValue(m_switch.isChecked());
        setValue(m_switch.isChecked());
        Log.i("DB_EVENT", "Submitting switch \"" + getLabel() + "\" to path "
                + getPath() + " under value " + m_switch.isChecked());
    }
}
