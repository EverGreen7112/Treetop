package com.evergreen.treetop.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.stats.GeneralStats;
import com.evergreen.treetop.activities.tasks.TM_GoalViewActivity;
import com.evergreen.treetop.activities.tasks.TM_TaskViewActivity;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.evergreen.treetop.activities.scouts.form.SC_ResultsForm;
import com.evergreen.treetop.activities.scouts.form.SC_TeamStrategyForm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        startActivity(new Intent(this, GeneralStats.class));
    }
}