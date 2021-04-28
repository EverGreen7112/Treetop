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
import com.evergreen.treetop.architecture.scouts.form.HitMissCounter;

public class SC_FormCountersFragment extends Fragment {

    private boolean m_initialized;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_powercell_counters_sc, container, false);
    }

    public void init(GameStage stage) {
        String pathPrefix = stage.getName().toLowerCase();

        new HitMissCounter(
                "Bottom",
                pathPrefix + ".bottom",
                getView().findViewById(R.id.sc_text_form_counters_bot_label),
                getView().findViewById(R.id.sc_text_form_counters_bot_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_bot_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_bot_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_bot_miss_decrement)
        );

        new HitMissCounter(
                "Outer",
                pathPrefix + ".outer",
                getView().findViewById(R.id.sc_text_form_counters_out_label),
                getView().findViewById(R.id.sc_text_form_counters_out_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_out_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_out_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_out_miss_decrement)
        );

        new HitMissCounter(
                "Inner",
                pathPrefix + ".inner",
                getView().findViewById(R.id.sc_text_form_counters_in_label),
                getView().findViewById(R.id.sc_text_form_counters_in_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_in_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_in_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_in_miss_decrement)
        );

        new HitMissCounter(
                "Load",
                pathPrefix + ".load",
                getView().findViewById(R.id.sc_text_form_counters_load_label),
                getView().findViewById(R.id.sc_text_form_counters_load_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_load_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_load_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_load_miss_decrement)
        );

        new HitMissCounter(
                "Collect",
                pathPrefix + ".collection",
                getView().findViewById(R.id.sc_text_form_counters_coll_label),
                getView().findViewById(R.id.sc_text_form_counters_coll_hit_value),
                getView().findViewById(R.id.sc_text_form_counters_coll_hit_decrement),
                getView().findViewById(R.id.sc_text_form_counters_coll_miss_value),
                getView().findViewById(R.id.sc_text_form_counters_coll_miss_decrement)
        );
    }
}
