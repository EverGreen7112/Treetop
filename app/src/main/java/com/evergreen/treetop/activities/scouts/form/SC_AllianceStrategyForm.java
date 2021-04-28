package com.evergreen.treetop.activities.scouts.form;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.evergreen.treetop.architecture.scouts.utils.StrategyOptions;
import com.evergreen.treetop.ui.fragments.form.SC_FormStrategyFragment;

public class SC_AllianceStrategyForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stratrgy_form_sc);
        Log.i("UI_EVENT", "Created AllianceStrategyForm activity");

        SC_FormStrategyFragment frag =
                (SC_FormStrategyFragment)getSupportFragmentManager().findFragmentById(R.id.sc_form_strategy_frag);
        frag.loadOptions(StrategyOptions.ALLIANCE);
        frag.setOnClickListener(v -> MatchDB.submitActiveForm());
    }
}