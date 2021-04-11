package com.evergreen.treetop.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.form.DropDown;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.ui.custom.spinner.BaseSpinner;

import java.sql.Struct;
import java.util.HashMap;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}