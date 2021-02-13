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
import com.evergreen.treetop.architecture.scouts.data.GameStage;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;

public class SC_FormAutoFragment extends Fragment {

    GameStage GAME_STAGE = GameStage.AUTO;

    private FormSwitch m_linePass;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View thisView =  inflater.inflate(R.layout.activity_tab_auto_sc, container, false);

        // ===FORM OBJECT INIT===

        // --- Counter Init ---
        String pathPrefix = GAME_STAGE.getName().toLowerCase();
        SC_FormCountersFragment counters =
                (SC_FormCountersFragment) getChildFragmentManager().findFragmentById(R.id.sc_form_frag_auto_counters);

        if (!counters.isInitialized()) {
            counters.init(GAME_STAGE);
        }
        // --- --- ---
        // --- Line Pass Init ---
        if (m_linePass == null)
            new FormSwitch(
                    "Passed the Line",
                    pathPrefix + "/line-pass",
                    thisView.findViewById(R.id.sc_form_auto_line_passed_switch),
                    thisView.findViewById(R.id.sc_form_auto_line_passed_label)
            );
        // --- --- ---
        // --- Logging ---
        Log.i("FORM_EVENT", "Initialized Autonomous Tab");
        // --- --- ---
        return thisView;
    }


}
