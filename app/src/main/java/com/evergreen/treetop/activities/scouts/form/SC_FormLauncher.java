package com.evergreen.treetop.activities.scouts.form;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.StatsLauncher;
import com.evergreen.treetop.activities.users.SignUpActivity;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.test.TestActivity;
import com.evergreen.treetop.ui.views.spinner.BaseSpinner;

public class SC_FormLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form_launcher_sc);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sc_stats:
                startActivity(new Intent(this, StatsLauncher.class));
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}