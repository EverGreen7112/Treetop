package com.evergreen.treetop.activities.scouts.stats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.form.SC_FormLauncher;
import com.evergreen.treetop.architecture.scouts.form.FormObject;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralStats extends AppCompatActivity implements ShakeDetector.Listener {

    private final String TAG = "GeneralStats_sc";

    private DocumentReference scoutDataDoc;

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
        scoutDataDoc = StatsLauncher.scoutDataDoc;

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "broadcast received");
                String action = intent.getAction();
                if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                    if (state % 10 == WifiManager.WIFI_STATE_ENABLED || state % 10 == WifiManager.WIFI_STATE_ENABLING) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setMessage("WARNING!\n" +
                                "You have your hotspot on, which is not allowed during competitions.");
                        alertBuilder.setNeutralButton("OK", (dialog, which) -> {});
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        this.registerReceiver(receiver, filter);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        if (scoutDataDoc == null) {
            Log.i(TAG, "no team chosen, showing stats for 7112");
            scoutDataDoc = new MatchDB(7112).getRef();
        }

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
            initiateScouting();
        });

        initiateScouting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_options_menu, menu);
        menu.setGroupVisible(R.id.global_menu_tm, false);
        menu.setGroupVisible(R.id.global_menu_sc, false);
        menu.setGroupVisible(R.id.global_menu_stats, false);
        menu.setGroupVisible(R.id.global_menu_stats_pc, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_stats_pc) {
            startActivity(new Intent(this, PowerCellStats.class));
        }
        return true;
    }

    @Override
    public void hearShake() {
        startActivity(new Intent(this, SC_FormLauncher.class));
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
            if (!k.equals("exists")) {
                v = (Map<String, Object>) v;
                results.put(k, ((Map) v).get(key));
            }
        });

        return results;
    }

    private void updateScoreOverTime(Map<String, Object> raw) {
        Map<String, Object> data = getAllWhereKey(raw, "score");

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

        BarData bars = new BarData(new BarDataSet(dataEntries, "Match Score Over Time"));
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
            if (data.get(key) != null) {
                dataEntries.add(new BarEntry(sortedKeys.indexOf(key), Integer.parseInt(data.get(key).toString())));
            }
            else Log.v(TAG, "null key is " + key);
        });

        BarData bars = new BarData(new BarDataSet(dataEntries, "Ranking Points Over Time"));
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

        score += (long)((Map)autoData.get("bottom")).get("hit") * 2;
        score += (long)((Map)autoData.get("outer")).get("hit") * 4;
        score += (long)((Map)autoData.get("inner")).get("hit") * 6;

        score += (long)((Map)teleopData.get("bottom")).get("hit") * 1;
        score += (long)((Map)teleopData.get("outer")).get("hit") * 2;
        score += (long)((Map)teleopData.get("inner")).get("hit") * 3;

        score += (long)((Map)endgameData.get("bottom")).get("hit") * 1;
        score += (long)((Map)endgameData.get("outer")).get("hit") * 2;
        score += (long)((Map)endgameData.get("inner")).get("hit") * 3;

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
            if (!obj.equals(true)) {
                powercellScore += getPowercellScore((Map<String, Object>)obj);
                wheelScore += getWheelScore((Map<String, Object>)obj);
                climbScore += getClimbScore((Map<String, Object>)obj);
            }
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
//        Map<String, Object> individualData = getAllWhereKey(raw, "score");
//        Map<String, Object> allianceData = getAllWhereKey(raw, "alliance-score");
//
//        List<BarEntry> dataEntries = new ArrayList<>();
//        List<String> sortedKeys = new ArrayList<>(individualData.keySet());
//        Collections.sort(sortedKeys);
//
//        sortedKeys.forEach(key -> {
//            dataEntries.add(new BarEntry(sortedKeys.indexOf(key),
//                    Integer.parseInt(individualData.get(key).toString())
//                    / Integer.parseInt(allianceData.get(key).toString())));
//        });
//
//        BarData bars = new BarData(new BarDataSet(dataEntries, "allianceScorePortionSet"));
//        scorePortionChart.setData(bars);
//        scorePortionChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(sortedKeys));
//        scorePortionChart.getDescription().setEnabled(false);
//        scorePortionChart.invalidate();
    }

    private void updateShieldEnergized(Map<String, Object> raw) {
        Map<String, Object> energizedData = getAllWhereKey(raw, "shield-energized");
        int energizedAmount = 0;
        for (Object obj : energizedData.values()) {
            if (obj != null) {
                if ((boolean) obj) energizedAmount++;
            }
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
            if (obj != null) {
                if ((boolean) obj) operationalAmount++;
            }
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
            if (obj == null) break;
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

    private void initiateScouting() {
        scoutDataDoc.get().addOnCompleteListener(task -> {
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
        });
    }
}
