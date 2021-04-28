package com.evergreen.treetop.activities.scouts.form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.Utilities;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.handlers.TeamDB;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.test.TestActivity;
import com.evergreen.treetop.ui.custom.spinner.BaseSpinner;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.SetOptions;

public class SC_FormLauncher extends AppCompatActivity {

    private static final boolean TEST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_launcher_sc);


        if (TEST) {
            startActivity(new Intent(this, TestActivity.class));
            return;
        }

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
}