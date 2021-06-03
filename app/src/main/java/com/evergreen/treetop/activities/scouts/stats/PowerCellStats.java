package com.evergreen.treetop.activities.scouts.stats;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerCellStats extends AppCompatActivity {
    private static final String TAG = "PowerCellStats_sc";

    public static DocumentReference scoutDataDoc;

    private PieChart attemptDistribution;
    private PieChart hitDistribution;
    private TextView averagePerBottomHit;
    private TextView averagePerTopHit;
    private BarChart hitsOverTime;
    private BarChart scoreOverTime;
    private BarChart outerHitsOverTime;
    private BarChart innerHitsOverTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_power_cells);

        if (scoutDataDoc == null) {
            Log.i(TAG, "no team chosen, showing stats for 7112");
            scoutDataDoc = new MatchDB(7112).getRef();
        }

        attemptDistribution = findViewById(R.id.stats_pc_attempt_distribution_chart);
        hitDistribution = findViewById(R.id.stats_pc_hit_distribution_chart);
        averagePerBottomHit = findViewById(R.id.stats_pc_average_per_bottom_value);
        averagePerTopHit = findViewById(R.id.stats_pc_average_per_top_value);
        hitsOverTime = findViewById(R.id.stats_pc_hits_over_time_chart);
        scoreOverTime = findViewById(R.id.sc_stats_general_score_over_time_chart);
        outerHitsOverTime = findViewById(R.id.stats_pc_outer_over_time_chart);
        innerHitsOverTime = findViewById(R.id.stats_pc_inner_over_time_chart);

        Button updateCharts = findViewById(R.id.stats_pc_update_charts);
        updateCharts.setOnClickListener(v -> {
            Log.i(TAG, "updating charts...");
            initiateScouting();
        });

        initiateScouting();
    }

    private void updateCharts(Map<String, Object> data) {
        updateAttemptDistribution(data);
        updateHitDistribution(data);
        updateAveragePerBottomHit(data);
        updateAveragePerTopHit(data);
        updateHitsOverTime(data);
        updateScoreOverTime(data);
        updateOuterOverTime(data);
        updateInnerOverTime(data);
    }

    private float getMatchHits(Map<String, Object> matchData) {
        float hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (float)((Map)autoData.get("bottom")).get("hit");
        hits += (float)((Map)autoData.get("outer")).get("hit");
        hits += (float)((Map)autoData.get("inner")).get("hit");

        hits += (float)((Map)teleopData.get("bottom")).get("hit");
        hits += (float)((Map)teleopData.get("outer")).get("hit");
        hits += (float)((Map)teleopData.get("inner")).get("hit");

        hits += (float)((Map)endgameData.get("bottom")).get("hit");
        hits += (float)((Map)endgameData.get("outer")).get("hit");
        hits += (float)((Map)endgameData.get("inner")).get("hit");

        return hits;
    }

    private float getMatchMisses(Map<String, Object> matchData) {
        float misses = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        misses += (float)((Map)autoData.get("bottom")).get("miss");
        misses += (float)((Map)autoData.get("outer")).get("miss");
        misses += (float)((Map)autoData.get("inner")).get("miss");

        misses += (float)((Map)teleopData.get("bottom")).get("miss");
        misses += (float)((Map)teleopData.get("outer")).get("miss");
        misses += (float)((Map)teleopData.get("inner")).get("miss");

        misses += (float)((Map)endgameData.get("bottom")).get("miss");
        misses += (float)((Map)endgameData.get("outer")).get("miss");
        misses += (float)((Map)endgameData.get("inner")).get("miss");

        return misses;
    }

    private float getMatchAttempts(Map<String, Object> matchData) {
        float attempts = 0;

        attempts += (float)((Map)((Map)matchData.get("autonomous")).get("bottom")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("autonomous")).get("outer")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("autonomous")).get("inner")).get("attempts");

        attempts += (float)((Map)((Map)matchData.get("teleop")).get("bottom")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("teleop")).get("outer")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("teleop")).get("inner")).get("attempts");

        attempts += (float)((Map)((Map)matchData.get("endgame")).get("bottom")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("endgame")).get("outer")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("endgame")).get("inner")).get("attempts");

        return attempts;
    }

    private float getMatchBottomHits(Map<String, Object> matchData) {
        float hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (float)((Map)autoData.get("bottom")).get("hit");

        hits += (float)((Map)teleopData.get("bottom")).get("hit");

        hits += (float)((Map)endgameData.get("bottom")).get("hit");

        return hits;
    }

    private float getMatchBottomAttempts(Map<String, Object> matchData) {
        float attempts = 0;

        attempts += (float)((Map)((Map)matchData.get("autonomous")).get("bottom")).get("attempts");

        attempts += (float)((Map)((Map)matchData.get("teleop")).get("bottom")).get("attempts");

        attempts += (float)((Map)((Map)matchData.get("endgame")).get("bottom")).get("attempts");

        return attempts;
    }

    private float getMatchOuterHits(Map<String, Object> matchData) {
        float hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (float)((Map)autoData.get("outer")).get("hit");

        hits += (float)((Map)teleopData.get("outer")).get("hit");

        hits += (float)((Map)endgameData.get("outer")).get("hit");

        return hits;
    }

    private float getMatchInnerHits(Map<String, Object> matchData) {
        float hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (float)((Map)autoData.get("inner")).get("hit");

        hits += (float)((Map)teleopData.get("inner")).get("hit");

        hits += (float)((Map)endgameData.get("inner")).get("hit");

        return hits;
    }

    private int getPowercellScore(Map<String, Object> matchData) {
        int score = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        score += (float)((Map)autoData.get("bottom")).get("hit") * 2;
        score += (float)((Map)autoData.get("outer")).get("hit") * 4;
        score += (float)((Map)autoData.get("inner")).get("hit") * 6;

        score += (float)((Map)teleopData.get("bottom")).get("hit") * 1;
        score += (float)((Map)teleopData.get("outer")).get("hit") * 2;
        score += (float)((Map)teleopData.get("inner")).get("hit") * 3;

        score += (float)((Map)endgameData.get("bottom")).get("hit") * 1;
        score += (float)((Map)endgameData.get("outer")).get("hit") * 2;
        score += (float)((Map)endgameData.get("inner")).get("hit") * 3;

        return score;
    }

    private float getMatchTopAttempts(Map<String, Object> matchData) {
        float attempts = 0;

        attempts += (float)((Map)((Map)matchData.get("autonomous")).get("outer")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("autonomous")).get("inner")).get("attempts");

        attempts += (float)((Map)((Map)matchData.get("teleop")).get("outer")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("teleop")).get("inner")).get("attempts");

        attempts += (float)((Map)((Map)matchData.get("endgame")).get("outer")).get("attempts");
        attempts += (float)((Map)((Map)matchData.get("endgame")).get("inner")).get("attempts");

        return attempts;
    }

    private void updateAttemptDistribution(Map<String, Object> raw) {
        float hits = 0, misses = 0;

        for(Object obj : raw.values()) {
            hits += getMatchHits((Map<String, Object>) obj);
            misses += getMatchMisses((Map<String, Object>) obj);
        }

        List<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(hits, "Total Hits"));
        dataEntries.add(new PieEntry(misses, "Total Misses"));
        PieDataSet set = new PieDataSet(dataEntries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(8);
        attemptDistribution.setData(data);
        attemptDistribution.getDescription().setEnabled(false);
        attemptDistribution.invalidate();
    }

    private void updateHitDistribution(Map<String, Object> raw) {
        float bottom = 0, outer = 0, inner = 0;

        for (Object obj : raw.values()) {
            bottom += (float)((Map)((Map)((Map)obj).get("autonomous")).get("bottom")).get("hit");
            bottom += (float)((Map)((Map)((Map)obj).get("teleop")).get("bottom")).get("hit");
            bottom += (float)((Map)((Map)((Map)obj).get("endgame")).get("bottom")).get("hit");

            outer += (float)((Map)((Map)((Map)obj).get("autonomous")).get("outer")).get("hit");
            outer += (float)((Map)((Map)((Map)obj).get("teleop")).get("outer")).get("hit");
            outer += (float)((Map)((Map)((Map)obj).get("endgame")).get("outer")).get("hit");

            inner += (float)((Map)((Map)((Map)obj).get("autonomous")).get("inner")).get("hit");
            inner += (float)((Map)((Map)((Map)obj).get("teleop")).get("inner")).get("hit");
            inner += (float)((Map)((Map)((Map)obj).get("endgame")).get("inner")).get("hit");
        }

        List<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(bottom, "Bottom Hits"));
        dataEntries.add(new PieEntry(outer, "Outer Hits"));
        dataEntries.add(new PieEntry(inner, "Inner Hits"));
        PieDataSet set = new PieDataSet(dataEntries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(8);
        hitDistribution.setData(data);
        hitDistribution.getDescription().setEnabled(false);
        hitDistribution.invalidate();
    }

    private void updateAveragePerBottomHit(Map<String, Object> raw) {
        List<Float> values = new ArrayList<>();

        for (Object obj : raw.values()) {
            values.add((getMatchBottomAttempts((Map<String, Object>)obj) / getMatchBottomHits((Map<String, Object>) obj)));
        }

        float average = 0;
        for (Float val : values) {
            average += val;
        }

        average /= values.size();

        averagePerBottomHit.setText(String.format("%.2f", average));
    }

    private void updateAveragePerTopHit(Map<String, Object> raw) {
        List<Float> values = new ArrayList<>();

        for (Object obj : raw.values()) {
            values.add((getMatchTopAttempts((Map<String, Object>)obj) / (getMatchOuterHits((Map<String, Object>)obj) + getMatchInnerHits((Map<String, Object>)obj))));
        }

        float average = 0;
        for (Float val : values) {
            average += val;
        }

        average /= values.size();

        averagePerTopHit.setText(String.format("%.2f", average));
    }

    private void updateHitsOverTime(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        for (String str : raw.keySet()) {
            data.put(str, getMatchHits((Map<String, Object>) raw.get(str)));
        }

        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        sortedKeys.forEach(key -> {
            if (data.get(key) != null) {
                dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
            }
            else Log.v(TAG, "null key is " + key);
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "Total Hits Over Time"));
        hitsOverTime.setData(bars);
        hitsOverTime.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        hitsOverTime.getDescription().setEnabled(false);
        hitsOverTime.invalidate();
    }

    private void updateScoreOverTime(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        for (String str : raw.keySet()) {
            data.put(str, getPowercellScore((Map<String, Object>) raw.get(str)));
        }

        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        sortedKeys.forEach(key -> {
            if (data.get(key) != null) {
                dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
            }
            else Log.v(TAG, "null key is " + key);
        });

        Log.v(TAG, "dataEntries:\n" + dataEntries.toString());

        BarData bars = new BarData(new BarDataSet(dataEntries, "Power Cell Score Over Time"));
        scoreOverTime.setData(bars);
        scoreOverTime.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        scoreOverTime.getDescription().setEnabled(false);
        scoreOverTime.invalidate();
    }

    private void updateOuterOverTime(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        for (String str : raw.keySet()) {
            data.put(str, getMatchOuterHits((Map<String, Object>) raw.get(str)));
        }

        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        sortedKeys.forEach(key -> {
            if (data.get(key) != null) {
                dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
            }
            else Log.v(TAG, "null key is " + key);
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "Outer Hits Over Time"));
        outerHitsOverTime.setData(bars);
        outerHitsOverTime.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        outerHitsOverTime.getDescription().setEnabled(false);
        outerHitsOverTime.invalidate();
    }

    private void updateInnerOverTime(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        for (String str : raw.keySet()) {
            data.put(str, getMatchInnerHits((Map<String, Object>) raw.get(str)));
        }

        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        sortedKeys.forEach(key -> {
            if (data.get(key) != null) {
                dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
            }
            else Log.v(TAG, "null key is " + key);
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "Outer Hits Over Time"));
        innerHitsOverTime.setData(bars);
        innerHitsOverTime.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        innerHitsOverTime.getDescription().setEnabled(false);
        innerHitsOverTime.invalidate();
    }

    private void initiateScouting() {
        scoutDataDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "data retrieval task successful.");
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.exists()) {
                        Log.i(TAG, "scouting data exists, retrieving data.");

                        updateCharts(docSnap.getData());
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
    }
}
