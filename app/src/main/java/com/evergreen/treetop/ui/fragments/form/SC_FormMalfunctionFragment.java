package com.evergreen.treetop.ui.fragments.form;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evergreen.treetop.R;

public class SC_FormMalfunctionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("FORM_EVENT", "Initialized a malfunction description fragment");
        return inflater.inflate(R.layout.fragment_malfunction_sc, container, false);
    }
}
