package com.evergreen.treetop.activities.scouts.form;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.stats.StatsLauncher;
import com.evergreen.treetop.activities.tasks.users.TM_SignUpActivity;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.ui.views.spinner.BaseSpinner;

public class SC_FormLauncher extends AppCompatActivity {

    private final String TAG = "FormLauncher_sc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_launcher_sc);
        this.setTitle("Treetop");

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

        BaseSpinner matchTypeSpinner = findViewById(R.id.sc_form_launcher_spinner_match_type);
        matchTypeSpinner.loadOptions(MatchID.MatchType.values());

        EditText matchNumber = findViewById(R.id.sc_form_launcher_edit_match_number);
        EditText teamNumber = findViewById(R.id.sc_form_launcher_edit_team_number);
        TextView nextButton = findViewById(R.id.sc_form_launcher_text_submit_button);

        nextButton.setOnClickListener( v -> {
                    ScoutingMatch.setCurrent(new ScoutingMatch(new MatchID(
                            (MatchID.MatchType)matchTypeSpinner.getSelectedItem(),
                            Integer.parseInt(matchNumber.getText().toString()
                            )
                    )));

                    FormObject.setScoutedTeam(Integer.parseInt(teamNumber.getText().toString()));

                    startActivity(new Intent(this, SC_ScoutingForm.class));
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_options_menu, menu);
        menu.setGroupVisible(R.id.global_menu_tm, false);
        menu.setGroupVisible(R.id.global_menu_stats_pc, false);
        menu.setGroupVisible(R.id.global_menu_stats, false);
        menu.setGroupVisible(R.id.global_menu_stats_general, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sc_stats:
                startActivity(new Intent(this, StatsLauncher.class));
                break;
            case R.id.menu_sc_launch_tm:
                startActivity(new Intent(this, TM_SignUpActivity.class));
        }

        return true;
    }
}