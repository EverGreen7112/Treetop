package com.evergreen.treetop.activities.scouts.stats;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneralStats extends AppCompatActivity {
    private static final String TAG = "GeneralStats_sc";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DocumentReference scoutDataDoc = db.document("Scouting/7112");

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

    private void updateCharts(HashMap<String, Object> scoutData) {
        updateScoreOverTime(scoutData);
        updateRankingOverTime(scoutData);
        updateScoreSources(scoutData);
        updateScorePortion(scoutData);
        updateShieldEnergized(scoutData);
        updateShieldOperational(scoutData);
        updateResults(scoutData);
    }

    private Map<String, Object> getAllWhereKey(Map<String, Object> data, String key) {
        Map<String, Object> results = new HashMap<>();
        data.forEach((k, v) -> {
            v = (Map<String, Object>)v;
            results.put(k, ((Map) v).get(key));
        });

        return results;
    }

    private void updateScoreOverTime(Map<String, Object> raw) {
        Map<String, Object> data = getAllWhereKey(raw, "score");
        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);
        sortedKeys.forEach(key -> {
            dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "scoreOverTimeSet"));
        scoreOverTimeChart.setData(bars);
        scoreOverTimeChart.invalidate();
    }

    private void updateRankingOverTime(HashMap<String, Object> scoutData) {

    }

    private void updateScoreSources(HashMap<String, Object> scoutData) {

    }

    private void updateScorePortion(HashMap<String, Object> scoutData) {

    }

    private void updateShieldEnergized(HashMap<String, Object> scoutData) {

    }

    private void updateShieldOperational(HashMap<String, Object> scoutData) {

    }

    private void updateResults(HashMap<String, Object> scoutData) {

    }

    private HashMap<String, Object> getScoutingData() {
        final HashMap<String, Object> data = new HashMap<>();
        scoutDataDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "data retrieval task successful.");
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.exists()) {
                        Log.i(TAG, "scouting data exists, retrieving data.");
                        data.putAll(docSnap.getData());
                    }
                    else {
                        Log.d(TAG, "no data found");
                    }
                }
                else {
                    Log.d(TAG, "data retrieval task failed.");
                }
            }
        });

        return data;
    }
}
