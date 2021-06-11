package com.evergreen.treetop.activities.scouts.form;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.LoggingUtils;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.form.FormRadio;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.form.NumberBox;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SC_ResultsForm extends AppCompatActivity {

    private final String TAG = "ResultsForm_sc";

    public final String RESULT_LOSS = "loss";
    public final String RESULT_WIN = "win";
    public final String RESULT_DRAW = "draw";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_form_sc);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "broadcast received");
                String action = intent.getAction();
                if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                    if (state % 10 == WifiManager.WIFI_STATE_ENABLED || state % 10 == WifiManager.WIFI_STATE_ENABLING) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setMessage("WARNING!\n" +
                                "You have your hotspot on, which is not allowed during competitions.");
                        alertBuilder.setNeutralButton("OK", (dialog, which) -> {});
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScoutingMatch.getCurrent().getDocRef(FormObject.getScoutedTeam()).set(LoggingUtils.PLACEHOLDER_OBJECT, SetOptions.merge());
        Map<Integer, Object> radioValueMap = new HashMap<>();
        radioValueMap.put(R.id.sc_form_results_radio_loss, RESULT_LOSS);
        radioValueMap.put(R.id.sc_form_results_radio_draw, RESULT_DRAW);
        radioValueMap.put(R.id.sc_form_results_radio_win, RESULT_WIN);

        new FormRadio(
                "Match Result",
                "result",
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
                "alliance-score",
                findViewById(R.id.sc_form_results_edit_score),
                findViewById(R.id.sc_form_results_text_score_label)
        );

        new FormObject(
                "Ranking Points",
                "ranking"
        ) {
            @Override
            protected Object getValue() {
                int addUp = 0;

                if (
                    ((RadioGroup)findViewById(R.id.sc_form_results_radio))
                    .getCheckedRadioButtonId() == R.id.sc_form_results_radio_draw)
                    addUp += 1;

                if (
                    ((RadioGroup)findViewById(R.id.sc_form_results_radio))
                    .getCheckedRadioButtonId() == R.id.sc_form_results_radio_win)
                    addUp += 2;

                if (((SwitchMaterial)findViewById(R.id.sc_form_results_switch_operational)).isChecked()) addUp += 1;

                if (((SwitchMaterial)findViewById(R.id.sc_from_results_switch_energized)).isChecked()) addUp += 1;

                return addUp;
            }

            @Override
            protected String getType() {
                return "Custom-Object";
            }
        };

        ((TextView)findViewById(R.id.sc_form_results_text_next_button)).setOnClickListener(
                v -> startActivity(new Intent(this, SC_TeamStrategyForm.class))
        );

    }
}
