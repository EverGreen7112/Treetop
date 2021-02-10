package com.evergreen.treetop.ui.fragments.form;

import androidx.fragment.app.Fragment;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.GameStage;
import com.evergreen.treetop.architecture.scouts.form.HitMissCounter;

public class Utilities {

    public static void init(Fragment frag, GameStage stage) {
        String pathPrefix = stage.getName().toLowerCase();

        new HitMissCounter(
                "Bottom",
                pathPrefix + "/bottom",
                frag.getView().findViewById(R.id.sc_text_form_counters_bot_label),
                frag.getView().findViewById(R.id.sc_text_form_counters_bot_hit_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_bot_hit_decrement),
                frag.getView().findViewById(R.id.sc_text_form_counters_bot_miss_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_bot_miss_decrement)
        );

        new HitMissCounter(
                "Outer",
                pathPrefix + "/outer",
                frag.getView().findViewById(R.id.sc_text_form_counters_out_label),
                frag.getView().findViewById(R.id.sc_text_form_counters_out_hit_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_out_hit_decrement),
                frag.getView().findViewById(R.id.sc_text_form_counters_out_miss_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_out_miss_decrement)
        );

        new HitMissCounter(
                "Inner",
                pathPrefix + "/inner",
                frag.getView().findViewById(R.id.sc_text_form_counters_in_label),
                frag.getView().findViewById(R.id.sc_text_form_counters_in_hit_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_in_hit_decrement),
                frag.getView().findViewById(R.id.sc_text_form_counters_in_miss_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_in_miss_decrement)
        );

        new HitMissCounter(
                "Load",
                pathPrefix + "/load",
                frag.getView().findViewById(R.id.sc_text_form_counters_load_label),
                frag.getView().findViewById(R.id.sc_text_form_counters_load_hit_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_load_hit_decrement),
                frag.getView().findViewById(R.id.sc_text_form_counters_load_miss_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_load_miss_decrement)
        );

        new HitMissCounter(
                "Collect",
                pathPrefix + "/collection",
                frag.getView().findViewById(R.id.sc_text_form_counters_coll_label),
                frag.getView().findViewById(R.id.sc_text_form_counters_coll_hit_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_coll_hit_decrement),
                frag.getView().findViewById(R.id.sc_text_form_counters_coll_miss_value),
                frag.getView().findViewById(R.id.sc_text_form_counters_coll_miss_decrement)
        );

    }
}
