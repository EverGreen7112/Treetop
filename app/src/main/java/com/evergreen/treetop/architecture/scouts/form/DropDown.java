package com.evergreen.treetop.architecture.scouts.form;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.evergreen.treetop.ui.views.spinner.BaseSpinner;

import java.util.Map;

public class DropDown extends FormObject {
    final BaseSpinner m_spinner;

    public DropDown(String label, String path, BaseSpinner spinner, TextView labelView,
                    TextView descriptionBox, Map<String, String> options) {
        super(label, path);
        labelView.setText(label);
        m_spinner = spinner;

        if (options.containsKey("")) {
            throw new IllegalArgumentException("Tried to initialize a DropDown \"" + getLabel()
            + "\", but was given an options list with an empty option!");
        }

        options.put("", "");

        String[] optionTitles =  options.keySet().toArray(new String[0]);
        spinner.loadOptions(optionTitles);

        m_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        descriptionBox.setText(
                                options.get(m_spinner.getSelectedItem().toString())
                        );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        descriptionBox.setText("");
                    }
                }
        );

    }

    public boolean itemSelected() {
        return !m_spinner.getSelectedItem().toString().equals("");
    }

    @Override
    protected Object getValue() {
        return m_spinner.getSelectedItem().toString();
    }

    @Override
    protected String getType() {
        return "Dropdown";
    }

    @Override
    public void submit() {

        if (!itemSelected()) {
            throw new EmptyOptionException("Tried to submit drop down \"" + getLabel() + "" +
                    "\", but no option was selected!");
        }

        super.submit();
    }

    public static class EmptyOptionException extends RuntimeException {

        public EmptyOptionException(String message) {
            super(message);
        }
    }
}
