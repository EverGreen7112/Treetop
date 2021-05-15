package com.evergreen.treetop.activities.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.evergreen.treetop.R;

public class TM_DasboardActivity extends AppCompatActivity {

    private static TM_DasboardActivity runningInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard_tm);
    }

    @Override
    protected void onResume() {
        runningInstance = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        runningInstance = null;
        super.onPause();
    }

    public static TM_DasboardActivity getRunningInstance() {
        return runningInstance;
    }
}