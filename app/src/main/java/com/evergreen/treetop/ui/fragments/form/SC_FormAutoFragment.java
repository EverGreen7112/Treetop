package com.evergreen.treetop.ui.fragments.form;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.GameStage;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.form.HitMissCounter;
import com.evergreen.treetop.architecture.scouts.form.SequenceSwitch;

public class SC_FormAutoFragment extends Fragment {

    GameStage GAME_STAGE = GameStage.AUTO;
    int m_matchSeconds = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        initObjects();
        return inflater.inflate(R.layout.activity_tab_auto_sc, container, false);
    }

    private void initObjects() {

        initCounters();

        String pathPrefix = GAME_STAGE.getName().toLowerCase();
        new SequenceSwitch(
                "Functions",
                pathPrefix + "/function",
                getView().findViewById(R.id.sc_form_auto_function_switch),
                getView().findViewById(R.id.sc_form_auto_function_label)
        );

        new FormSwitch(
                "Passed the Line",
                pathPrefix + "/line-pass",
                getView().findViewById(R.id.sc_form_auto_line_passed_switch),
                getView().findViewById(R.id.sc_form_auto_line_passed_label)
        );


        TextView matchTimer = getView().findViewById(R.id.sc_form_auto_match_timer);
        Handler handler = new Handler();

        Runnable counter = new Runnable() {
            @Override
            public void run() {
                m_matchSeconds++;
                matchTimer.setText(formatTimer(m_matchSeconds));
                if (m_matchSeconds < 15) {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(counter, 1000);

    }

    private String formatTimer(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return mins + ":" + secs;
    }



    public void initCounters() {
        String pathPrefix = GAME_STAGE.getName().toLowerCase();

        new HitMissCounter(
                "Bottom",
                pathPrefix + "/bottom",
                getView().findViewById(R.id.sc_text_form_counters_bot_label),
                getView().findViewById(R.id.sc_text_form_counters_bot_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_bot_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_bot_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_bot_miss_decrement)
        );

        new HitMissCounter(
                "Outer",
                pathPrefix + "/outer",
                getView().findViewById(R.id.sc_text_form_counters_out_label),
                getView().findViewById(R.id.sc_text_form_counters_out_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_out_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_out_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_out_miss_decrement)
        );

        new HitMissCounter(
                "Inner",
                pathPrefix + "/inner",
                getView().findViewById(R.id.sc_text_form_counters_in_label),
                getView().findViewById(R.id.sc_text_form_counters_in_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_in_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_in_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_in_miss_decrement)
        );

        new HitMissCounter(
                "Load",
                pathPrefix + "/load",
                getView().findViewById(R.id.sc_text_form_counters_load_label),
                getView().findViewById(R.id.sc_text_form_counters_load_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_load_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_load_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_load_miss_decrement)
        );

        new HitMissCounter(
                "Collect",
                pathPrefix + "/collection",
                getView().findViewById(R.id.sc_text_form_counters_coll_label),
                getView().findViewById(R.id.sc_text_form_counters_coll_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_coll_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_coll_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_coll_miss_decrement)
        );

    }

}
