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
import com.evergreen.treetop.architecture.scouts.form.ContinuousStopwatch;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.form.SequenceStopwatch;

public class SC_FormEndgameFragment extends Fragment {

    final GameStage GAME_STAGE = GameStage.ENDGAME;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View thisView =  inflater.inflate(R.layout.fragment_tab_end_sc, container, false);

        String pathPrefix = GAME_STAGE.getName().toLowerCase();

        SC_FormCountersFragment counters =
                (SC_FormCountersFragment) getChildFragmentManager().findFragmentById(R.id.sc_form_frag_end_counters);
        counters.init(GAME_STAGE);

        new FormSwitch(
                "Hanging",
                pathPrefix + ".hang",
                thisView.findViewById(R.id.sc_form_end_hanging_switch),
                thisView.findViewById(R.id.sc_form_end_hanging_label)
        );

        new ContinuousStopwatch(
                "Hang Attempt",
                pathPrefix + ".hang-attempt",
                thisView.findViewById(R.id.sc_form_end_hang_attempt_stopwatch),
                thisView.findViewById(R.id.sc_form_end_hang_attempt_stopwatch)
        );

        new FormSwitch(
                "Scales are Level",
                pathPrefix + ".level",
                thisView.findViewById(R.id.sc_form_end_level_switch),
                thisView.findViewById(R.id.sc_form_end_level_label)
        );

        new FormSwitch(
                "Multiple Robots Hang",
                pathPrefix + ".hang-many",
                thisView.findViewById(R.id.sc_form_end_multi_hang_switch),
                thisView.findViewById(R.id.sc_form_end_multi_hang_label)
        );

        new SequenceStopwatch(
                "Defence",
                pathPrefix + ".defense",
                thisView.findViewById(R.id.sc_form_end_defence_stopwatch),
                thisView.findViewById(R.id.sc_form_end_defence_label)
        );

        return  thisView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
