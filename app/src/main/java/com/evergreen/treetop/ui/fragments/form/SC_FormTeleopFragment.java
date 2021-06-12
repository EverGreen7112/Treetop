package com.evergreen.treetop.ui.fragments.form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.GameStage;
import com.evergreen.treetop.architecture.scouts.form.FormCounter;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.form.SequenceStopwatch;

public class SC_FormTeleopFragment extends Fragment {

    final GameStage GAME_STAGE = GameStage.TELEOP;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View thisView =  inflater.inflate(R.layout.fragment_tab_teleop_sc, container, false);

        // === FORM OBJECT INIT ===
        String pathPrefix = GAME_STAGE.getName().toLowerCase();

        SC_FormCountersFragment counters =
                (SC_FormCountersFragment) getChildFragmentManager().findFragmentById(R.id.sc_form_frag_teleop_counters);
        counters.init(GAME_STAGE);

        // --- Initialize Defense Stopwatch ---
        new SequenceStopwatch(
                "Defence",
                pathPrefix + ".defence",
                thisView.findViewById(R.id.sc_form_teleop_defence_stopwatch),
                thisView.findViewById(R.id.sc_form_teleop_defence_label)
        );
        // --- --- ---
        // --- Initialize Rotation Control Switch-Miss ---
        new FormSwitch(
                "Rotation Control",
                "rotation-control-hit",
                thisView.findViewById(R.id.sc_form_teleop_rotation_switch),
                thisView.findViewById(R.id.sc_form_teleop_rotation_label)
        );

        new FormCounter(
                "Miss",
                "rotation-control-miss",
                thisView.findViewById(R.id.sc_form_teleop_rotation_miss_value),
                thisView.findViewById(R.id.sc_form_teleop_rotation_miss_decrement)
        );
        // --- --- ---
        // --- Initialize Position Control Switch-Miss
        new FormSwitch(
                "Position Control",
                "position-control-hit",
                thisView.findViewById(R.id.sc_form_teleop_position_switch),
                thisView.findViewById(R.id.sc_form_teleop_position_label)
        );

        new FormCounter(
                "Miss",
                "position-control-miss",
                thisView.findViewById(R.id.sc_form_teleop_position_miss_value),
                thisView.findViewById(R.id.sc_form_teleop_position_miss_decrement)
        );
        // --- --- ---
        // === === ===

        return thisView;
    }
}
