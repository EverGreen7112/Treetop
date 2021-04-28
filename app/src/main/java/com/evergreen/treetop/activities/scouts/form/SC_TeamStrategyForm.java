package com.evergreen.treetop.activities.scouts.form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.utils.StrategyOptions;
import com.evergreen.treetop.ui.fragments.form.SC_FormCountersFragment;
import com.evergreen.treetop.ui.fragments.form.SC_FormStrategyFragment;

public class SC_TeamStrategyForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stratrgy_form_sc);
        Log.i("UI_EVENT", "Created TeamStrategyForm activity");

        SC_FormStrategyFragment frag =
                (SC_FormStrategyFragment)getSupportFragmentManager().findFragmentById(R.id.sc_form_strategy_frag);
        frag.loadOptions(StrategyOptions.TEAM);
    }
}