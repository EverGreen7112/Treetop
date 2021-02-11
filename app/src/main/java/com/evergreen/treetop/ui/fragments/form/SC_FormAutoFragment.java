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
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res =  inflater.inflate(R.layout.activity_tab_auto_sc, container, false);
        initObjects(res);
        Log.i("FORM_EVENT", "Initialized Autonomous Tab");
        return res;
    }

    private void initObjects(View thisView) {
        String pathPrefix = GAME_STAGE.getName().toLowerCase();
        SC_FormCountersFragment counters =
                (SC_FormCountersFragment) getChildFragmentManager().findFragmentById(R.id.sc_form_frag_auto_counters);
        counters.init(GAME_STAGE);

        new FormSwitch(
                "Passed the Line",
                pathPrefix + "/line-pass",
                thisView.findViewById(R.id.sc_form_auto_line_passed_switch),
                thisView.findViewById(R.id.sc_form_auto_line_passed_label)
        );





    }

}
