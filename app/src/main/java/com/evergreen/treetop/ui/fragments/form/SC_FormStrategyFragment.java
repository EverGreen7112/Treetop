package com.evergreen.treetop.ui.fragments.form;

import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.form.StrategyDropDown;
import com.evergreen.treetop.architecture.scouts.utils.StrategyOptions;
import com.evergreen.treetop.ui.custom.spinner.OvalSpinner;

import java.util.Map;

public class SC_FormStrategyFragment extends Fragment {

    public void loadOptions(StrategyOptions options) {

        StrategyDropDown dropdown =
                new StrategyDropDown(StrategyOptions.TEAM, m_spinner, m_descriptionBox, m_spinnerLabel);

        m_submitButton.setOnClickListener(v -> dropdown.submit());
    }

    OvalSpinner m_spinner;
    TextView m_descriptionBox;
    TextView m_spinnerLabel;
    Button m_submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("UI_EVENT", "Initialize Strategy");

        View thisView = inflater.inflate(R.layout.fragment_form_strategy_sc, container, false);
        m_spinner = thisView.findViewById(R.id.sc_form_strategy_spinner_picker);
        m_descriptionBox = thisView.findViewById(R.id.sc_form_strategy_text_option_desc);
        m_spinnerLabel = thisView.findViewById(R.id.sc_form_strategy_text_picker_label);

        return thisView;

    }
}