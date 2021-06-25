package com.evergreen.treetop.ui.fragments.form;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.form.SC_AllianceStrategyForm;
import com.evergreen.treetop.architecture.scouts.form.StrategyDropDown;
import com.evergreen.treetop.architecture.scouts.utils.StrategyOptions;
import com.evergreen.treetop.architecture.tasks.utils.UIUtils;
import com.evergreen.treetop.ui.views.spinner.OvalSpinner;

public class SC_FormStrategyFragment extends Fragment {

    public void loadOptions(StrategyOptions options) {
        StrategyDropDown dropdown =
                new StrategyDropDown(options, m_spinner, m_spinnerLabel,  m_descriptionBox);


        if (options == StrategyOptions.ALLIANCE) {
            m_submitButton.setText("Submit");
            m_submitButton.setOnClickListener(v -> dropdown.submit());
        } else {
            m_submitButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SC_AllianceStrategyForm.class)));
        }

        Log.v("UI_EVENT", "Loaded options " + options.getType() + " for strategy dropdown");
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        m_submitButton.setOnClickListener(onClickListener);
    }

    OvalSpinner m_spinner;
    TextView m_descriptionBox;
    TextView m_spinnerLabel;
    Button m_submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("UI_EVENT", "Initialize Strategy Fragment");

        View thisView = inflater.inflate(R.layout.fragment_form_strategy_sc, container, false);
        m_spinner = thisView.findViewById(R.id.sc_form_strategy_spinner_picker);
        m_descriptionBox = thisView.findViewById(R.id.sc_form_strategy_text_option_desc);
        m_spinnerLabel = thisView.findViewById(R.id.sc_form_strategy_text_picker_label);
        m_submitButton = thisView.findViewById(R.id.sc_form_strategy_button_submit);

        return thisView;
    }
}