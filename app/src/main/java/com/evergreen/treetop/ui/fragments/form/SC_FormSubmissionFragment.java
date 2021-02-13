package com.evergreen.treetop.ui.fragments.form;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;

public class SC_FormSubmissionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_tab_form_submit_sc, container, false);
        thisView.findViewById(R.id.sc_form_button_submit).setOnClickListener(v -> MatchDB.submitActiveForm());
        return thisView;
    }
}