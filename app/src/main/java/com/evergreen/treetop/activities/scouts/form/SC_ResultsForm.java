package com.evergreen.treetop.activities.scouts.form;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.form.FormRadio;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.form.NumberBox;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SC_ResultsForm extends AppCompatActivity {

    public final String RESULT_LOSS = "loss";
    public final String RESULT_WIN = "win";
    public final String RESULT_DRAW = "draw";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_form_sc);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScoutingMatch.getCurrent().getDocRef(FormObject.getScoutedTeam()).set(Utilities.PLACEHOLDER_OBJECT, SetOptions.merge());
        Map<Integer, Object> radioValueMap = new HashMap<>();
        radioValueMap.put(R.id.sc_form_results_radio_loss, RESULT_LOSS);
        radioValueMap.put(R.id.sc_form_results_radio_draw, RESULT_DRAW);
        radioValueMap.put(R.id.sc_form_results_radio_win, RESULT_WIN);

        new FormRadio(
                "Match Result",
                "team-won",
                findViewById(R.id.sc_form_results_radio),
                radioValueMap
        );

        new FormSwitch(
                "Shield Operational",
                "shield-operational",
                findViewById(R.id.sc_form_results_switch_operational)
        );

        new FormSwitch(
                "Shield Energized",
                "shield-energized",
                findViewById(R.id.sc_from_results_switch_energized)
        );

        new NumberBox(
                "Score",
                "score",
                findViewById(R.id.sc_form_results_edit_score),
                findViewById(R.id.sc_form_results_text_score_label)
        );

        ((TextView)findViewById(R.id.sc_form_results_text_next_button)).setOnClickListener(
                v -> startActivity(new Intent(this,SC_TeamStrategyForm.class))
        );

    }
}
