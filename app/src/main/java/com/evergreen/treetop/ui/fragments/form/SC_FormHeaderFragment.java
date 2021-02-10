package com.evergreen.treetop.ui.fragments.form;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;

import java.util.Locale;

public class SC_FormHeaderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form_header, container, false);
    }
}