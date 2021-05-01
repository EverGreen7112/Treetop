package com.evergreen.treetop.activities.scouts.stats;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralStats extends AppCompatActivity {
    private static final String TAG = "GeneralStats_sc";

    private final DocumentReference scoutDataDoc = ScoutingMatch.getCurrent().getDocRef(FormObject.getScoutedTeam());

    private BarChart scoreOverTimeChart;
    private BarChart rankingOverTimeChart;
    private PieChart scoreSourcesChart;
    private BarChart scorePortionChart;
    private PieChart shieldEnergizedChart;
    private PieChart shieldOperationalChart;
    private PieChart resultChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_general_sc);

        scoreOverTimeChart = findViewById(R.id.sc_stats_general_score_over_time_chart);
        rankingOverTimeChart = findViewById(R.id.sc_stats_general_ranking_over_time_chart);
        scoreSourcesChart = findViewById(R.id.sc_stats_general_score_sources_chart);
        scorePortionChart = findViewById(R.id.sc_stats_general_score_portion_chart);
        shieldEnergizedChart = findViewById(R.id.sc_stats_general_energized_chart);
        shieldOperationalChart = findViewById(R.id.sc_stats_general_operational_chart);
        resultChart = findViewById(R.id.sc_stats_general_results_chart);

        Button updateCharts = findViewById(R.id.sc_stats_general_update_charts);
        updateCharts.setOnClickListener(v -> {
            Log.i(TAG, "updating charts...");
            updateCharts(getScoutingData());
        });

        updateCharts(getScoutingData());
    }

    private void updateCharts(Map<String, Object> scoutData) {
        updateScoreOverTime(scoutData);
        updateRankingOverTime(scoutData);
        updateScoreSources(scoutData);
        updateScorePortion(scoutData);
        updateShieldEnergized(scoutData);
        updateShieldOperational(scoutData);
        updateResultsChart(scoutData);
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
        scoreOverTimeChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        scoreOverTimeChart.getDescription().setEnabled(false);
        scoreOverTimeChart.invalidate();
    }

    private void updateRankingOverTime(Map<String, Object> raw) {
        Map<String, Object> data = getAllWhereKey(raw, "ranking");

        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        sortedKeys.forEach(key -> {
            dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "rankingOverTimeSet"));
        rankingOverTimeChart.setData(bars);
        rankingOverTimeChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        rankingOverTimeChart.getDescription().setEnabled(false);
        rankingOverTimeChart.invalidate();
    }

    private int getPowercellScore(Map<String, Object> matchData) {
        int score = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        score += (int)((Map)autoData.get("bottom")).get("hit") * 2;
        score += (int)((Map)autoData.get("outer")).get("hit") * 4;
        score += (int)((Map)autoData.get("inner")).get("hit") * 6;

        score += (int)((Map)teleopData.get("bottom")).get("hit") * 2;
        score += (int)((Map)teleopData.get("outer")).get("hit") * 4;
        score += (int)((Map)teleopData.get("inner")).get("hit") * 6;

        score += (int)((Map)endgameData.get("bottom")).get("hit") * 2;
        score += (int)((Map)endgameData.get("outer")).get("hit") * 4;
        score += (int)((Map)endgameData.get("inner")).get("hit") * 6;

        return score;
    }

    private int getWheelScore(Map<String, Object> matchData) {
        int score = 0;

        if ((boolean)matchData.get("rotation-control-hit")) score += 10;
        if ((boolean)matchData.get("position-control-hit")) score += 15;

        return score;
    }

    private int getClimbScore(Map<String, Object> matchData) {
        int score = 0;

        Map<String, Object> data = (Map)matchData.get("endgame");

        if ((boolean)data.get("hang")) score += 25;
        if ((boolean)data.get("hang-many") && (boolean)data.get("level")) score += 15;

        return score;
    }

    private void updateScoreSources(Map<String, Object> raw) {
        int powercellScore = 0, wheelScore = 0, climbScore = 0;

        for(Object obj : raw.values()) {
            powercellScore += getPowercellScore((Map<String, Object>)obj);
            wheelScore += getWheelScore((Map<String, Object>)obj);
            climbScore += getClimbScore((Map<String, Object>)obj);
        }

        List<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(powercellScore, "Powercell Hits"));
        dataEntries.add(new PieEntry(wheelScore, "Colour Wheel Spins"));
        dataEntries.add(new PieEntry(climbScore, "Climbing"));
        PieDataSet set = new PieDataSet(dataEntries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(8);
        scoreSourcesChart.setData(data);
        scoreSourcesChart.getDescription().setEnabled(false);
        scoreSourcesChart.invalidate();
    }

    private void updateScorePortion(Map<String, Object> raw) {
        Map<String, Object> individualData = getAllWhereKey(raw, "score");
        Map<String, Object> allianceData = getAllWhereKey(raw, "alliance-score");

        List<BarEntry> dataEntries = new ArrayList<>();
        List<String> sortedKeys = new ArrayList<>(individualData.keySet());
        Collections.sort(sortedKeys);

        sortedKeys.forEach(key -> {
            dataEntries.add(new BarEntry(sortedKeys.indexOf(key),
                    Integer.parseInt(individualData.get(key).toString())
                    / Integer.parseInt(allianceData.get(key).toString())));
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "allianceScorePortionSet"));
        scorePortionChart.setData(bars);
        scorePortionChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
        scorePortionChart.getDescription().setEnabled(false);
        scorePortionChart.invalidate();
    }

    private void updateShieldEnergized(Map<String, Object> raw) {
        Map<String, Object> energizedData = getAllWhereKey(raw, "shield-energized");
        int energizedAmount = 0;
        for (Object obj : energizedData.values()) {
            if ((boolean) obj) energizedAmount++;
        }

        List<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(energizedAmount, "Energized"));
        dataEntries.add(new PieEntry(raw.size(), "Not Energized"));
        PieDataSet set = new PieDataSet(dataEntries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(8);
        shieldEnergizedChart.setData(data);
        shieldEnergizedChart.getDescription().setEnabled(false);
        shieldEnergizedChart.invalidate();
    }

    private void updateShieldOperational(Map<String, Object> raw) {
        Map<String, Object> operationalData = getAllWhereKey(raw, "shield-operational");
        int operationalAmount = 0;
        for (Object obj : operationalData.values()) {
            if ((boolean) obj) operationalAmount++;
        }

        List<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(operationalAmount, "Operational"));
        dataEntries.add(new PieEntry(raw.size(), "Not Operational"));
        PieDataSet set = new PieDataSet(dataEntries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(8);
        shieldOperationalChart.setData(data);
        shieldOperationalChart.getDescription().setEnabled(false);
        shieldOperationalChart.invalidate();
    }

    private void updateResultsChart(Map<String, Object> raw) {
        Map<String, Object> resultData = getAllWhereKey(raw, "result");
        int winCount = 0, lossCount = 0, drawCount = 0;
        for (Object obj : resultData.values()) {
            switch ((String) obj) {
                case "win":
                    winCount++;
                    break;
                case "loss":
                    lossCount++;
                    break;
                case "draw":
                    drawCount++;
                    break;
            }
        }

        List<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(winCount, "Wins"));
        dataEntries.add(new PieEntry(lossCount, "Losses"));
        dataEntries.add(new PieEntry(drawCount, "Draws"));
        PieDataSet set = new PieDataSet(dataEntries, "");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(8);
        resultChart.setData(data);
        resultChart.getDescription().setEnabled(false);
        resultChart.invalidate();
    }

    private Map<String, Object> getScoutingData() {
        final Map<String, Object> data = new HashMap<>();
        scoutDataDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "data retrieval task successful.");
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.exists()) {
                        Log.i(TAG, "scouting data exists, retrieving data.");
                        data.putAll(docSnap.getData());
                        Log.v(TAG, data.toString());
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
