package com.evergreen.treetop.activities.scouts.stats;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GeneralStats extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DocumentReference scoutDataDoc = db.document("7112/Scouting");

    private final BarChart scoreOverTimeChart = findViewById(R.id.sc_stats_general_score_over_time_chart);
    private final BarChart rankingOverTimeChart = findViewById(R.id.sc_stats_general_ranking_over_time_chart);
    private final PieChart scoreSourcesChart = findViewById(R.id.sc_stats_general_score_sources_chart);
    private final BarChart scorePortionChart = findViewById(R.id.sc_stats_general_score_portion_chart);
    private final PieChart shieldEnergizedChart = findViewById(R.id.sc_stats_general_energized_chart);
    private final PieChart shieldOperationalChart = findViewById(R.id.sc_stats_general_operational_chart);
    private final PieChart resultChart = findViewById(R.id.sc_stats_general_results_chart);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_general_sc);
    }

    private void updateCharts() {
        scoutDataDoc.get();
    }
}
