package com.evergreen.treetop.activities.scouts.form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.utils.MatchID;
import com.evergreen.treetop.test.TestActivity;
import com.google.firebase.FirebaseApp;

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

        startActivity(new Intent(this, SC_ScoutingForm.class)); // For now.
    }
}