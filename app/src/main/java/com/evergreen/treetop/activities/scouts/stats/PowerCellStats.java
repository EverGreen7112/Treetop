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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.form.SC_FormLauncher;
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
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerCellStats extends AppCompatActivity implements ShakeDetector.Listener {

    private final String TAG = "PowerCellStats_sc";

    private DocumentReference scoutDataDoc;

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
        this.setTitle("Treetop");
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

        SwipeRefreshLayout swipeLayout = findViewById(R.id.stats_pc_swipe_layout);
        swipeLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "updating charts...");
            initiateScouting();
        });

        attemptDistribution = findViewById(R.id.stats_pc_attempt_distribution_chart);
        hitDistribution = findViewById(R.id.stats_pc_hit_distribution_chart);
        averagePerBottomHit = findViewById(R.id.stats_pc_average_per_bottom_value);
        averagePerTopHit = findViewById(R.id.stats_pc_average_per_top_value);
        hitsOverTime = findViewById(R.id.stats_pc_hits_over_time_chart);
        scoreOverTime = findViewById(R.id.stats_pc_score_over_time_chart);
        outerHitsOverTime = findViewById(R.id.stats_pc_outer_over_time_chart);
        innerHitsOverTime = findViewById(R.id.stats_pc_inner_over_time_chart);

        initiateScouting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_options_menu, menu);
        menu.setGroupVisible(R.id.global_menu_tm, false);
        menu.setGroupVisible(R.id.global_menu_sc, false);
        menu.setGroupVisible(R.id.global_menu_stats_general, false);
        menu.setGroupVisible(R.id.global_menu_stats, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_stats_general) {
            startActivity(new Intent(this, GeneralStats.class));
        }
        return true;
    }

    @Override
    public void hearShake() {
        startActivity(new Intent(this, SC_FormLauncher.class));
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
        Log.i(TAG, "finished updating chart data");
    }

    private long getMatchHits(Map<String, Object> matchData) {
        long hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (long)((Map)autoData.get("bottom")).get("hit");
        hits += (long)((Map)autoData.get("outer")).get("hit");
        hits += (long)((Map)autoData.get("inner")).get("hit");

        hits += (long)((Map)teleopData.get("bottom")).get("hit");
        hits += (long)((Map)teleopData.get("outer")).get("hit");
        hits += (long)((Map)teleopData.get("inner")).get("hit");

        hits += (long)((Map)endgameData.get("bottom")).get("hit");
        hits += (long)((Map)endgameData.get("outer")).get("hit");
        hits += (long)((Map)endgameData.get("inner")).get("hit");

        return hits;
    }

    private long getMatchMisses(Map<String, Object> matchData) {
        long misses = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        misses += (long)((Map)autoData.get("bottom")).get("miss");
        misses += (long)((Map)autoData.get("outer")).get("miss");
        misses += (long)((Map)autoData.get("inner")).get("miss");

        misses += (long)((Map)teleopData.get("bottom")).get("miss");
        misses += (long)((Map)teleopData.get("outer")).get("miss");
        misses += (long)((Map)teleopData.get("inner")).get("miss");

        misses += (long)((Map)endgameData.get("bottom")).get("miss");
        misses += (long)((Map)endgameData.get("outer")).get("miss");
        misses += (long)((Map)endgameData.get("inner")).get("miss");

        return misses;
    }

    private long getMatchAttempts(Map<String, Object> matchData) {
        long attempts = 0;

        attempts += (long)((Map)((Map)matchData.get("autonomous")).get("bottom")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("autonomous")).get("outer")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("autonomous")).get("inner")).get("attempts");

        attempts += (long)((Map)((Map)matchData.get("teleop")).get("bottom")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("teleop")).get("outer")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("teleop")).get("inner")).get("attempts");

        attempts += (long)((Map)((Map)matchData.get("endgame")).get("bottom")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("endgame")).get("outer")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("endgame")).get("inner")).get("attempts");

        return attempts;
    }

    private long getMatchBottomHits(Map<String, Object> matchData) {
        long hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (long)((Map)autoData.get("bottom")).get("hit");

        hits += (long)((Map)teleopData.get("bottom")).get("hit");

        hits += (long)((Map)endgameData.get("bottom")).get("hit");

        return hits;
    }

    private long getMatchBottomAttempts(Map<String, Object> matchData) {
        long attempts = 0;

        attempts += (long)((Map)((Map)matchData.get("autonomous")).get("bottom")).get("attempts");

        attempts += (long)((Map)((Map)matchData.get("teleop")).get("bottom")).get("attempts");

        attempts += (long)((Map)((Map)matchData.get("endgame")).get("bottom")).get("attempts");

        return attempts;
    }

    private long getMatchOuterHits(Map<String, Object> matchData) {
        long hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (long)((Map)autoData.get("outer")).get("hit");

        hits += (long)((Map)teleopData.get("outer")).get("hit");

        hits += (long)((Map)endgameData.get("outer")).get("hit");

        return hits;
    }

    private long getMatchInnerHits(Map<String, Object> matchData) {
        long hits = 0;

        Map<String, Object> autoData = (Map)matchData.get("autonomous");
        Map<String, Object> teleopData = (Map)matchData.get("teleop");
        Map<String, Object> endgameData = (Map)matchData.get("endgame");

        hits += (long)((Map)autoData.get("inner")).get("hit");

        hits += (long)((Map)teleopData.get("inner")).get("hit");

        hits += (long)((Map)endgameData.get("inner")).get("hit");

        return hits;
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

    private long getMatchTopAttempts(Map<String, Object> matchData) {
        long attempts = 0;

        attempts += (long)((Map)((Map)matchData.get("autonomous")).get("outer")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("autonomous")).get("inner")).get("attempts");

        attempts += (long)((Map)((Map)matchData.get("teleop")).get("outer")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("teleop")).get("inner")).get("attempts");

        attempts += (long)((Map)((Map)matchData.get("endgame")).get("outer")).get("attempts");
        attempts += (long)((Map)((Map)matchData.get("endgame")).get("inner")).get("attempts");

        return attempts;
    }

    private void updateAttemptDistribution(Map<String, Object> raw) {
        long hits = 0, misses = 0;

        for(Object obj : raw.values()) {
            if (!obj.equals(true)) {
                hits += getMatchHits((Map<String, Object>) obj);
                misses += getMatchMisses((Map<String, Object>) obj);
            }
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
        long bottom = 0, outer = 0, inner = 0;

        for (Object obj : raw.values()) {
            if (!obj.equals(true)) {
                bottom += (long)((Map)((Map)((Map)obj).get("autonomous")).get("bottom")).get("hit");
                bottom += (long)((Map)((Map)((Map)obj).get("teleop")).get("bottom")).get("hit");
                bottom += (long)((Map)((Map)((Map)obj).get("endgame")).get("bottom")).get("hit");

                outer += (long)((Map)((Map)((Map)obj).get("autonomous")).get("outer")).get("hit");
                outer += (long)((Map)((Map)((Map)obj).get("teleop")).get("outer")).get("hit");
                outer += (long)((Map)((Map)((Map)obj).get("endgame")).get("outer")).get("hit");

                inner += (long)((Map)((Map)((Map)obj).get("autonomous")).get("inner")).get("hit");
                inner += (long)((Map)((Map)((Map)obj).get("teleop")).get("inner")).get("hit");
                inner += (long)((Map)((Map)((Map)obj).get("endgame")).get("inner")).get("hit");
            }
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
        List<Long> values = new ArrayList<>();

        for (Object obj : raw.values()) {
            if (!obj.equals(true)) {
                try {
                    values.add((getMatchBottomAttempts((Map<String, Object>) obj) / getMatchBottomHits((Map<String, Object>) obj)));
                }
                catch (ArithmeticException e) {
                    Log.i(TAG, "no bottom hits, thus this iteration is not counted.");
                }
            }
        }

        long average = 0;
        for (long val : values) {
            average += val;
        }

        try {
            average /= values.size();
        }
        catch (ArithmeticException e) {
            Log.i(TAG, "no bottom hits for the team at all");
            average = -1;
        }

        averagePerBottomHit.setText(String.valueOf(average));
    }

    private void updateAveragePerTopHit(Map<String, Object> raw) {
        List<Long> values = new ArrayList<>();

        for (Object obj : raw.values()) {
            if (!obj.equals(true)) {
                try {
                    values.add((getMatchTopAttempts((Map<String, Object>) obj)
                            / (getMatchOuterHits((Map<String, Object>) obj) + getMatchInnerHits((Map<String, Object>) obj))
                    ));
                }
                catch (ArithmeticException e) {
                    Log.i(TAG, "no top hits, thus this iteration is not counted");
                }
            }
        }

        long average = 0;
        for (long val : values) {
            average += val;
        }

        try {
            average /= values.size();
        }
        catch (ArithmeticException e) {
            Log.i(TAG, "no top hits for the team at all");
            average = -1;
        }

        averagePerTopHit.setText(String.valueOf(average));
    }

    private void updateHitsOverTime(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        for (String str : raw.keySet()) {
            if (!str.equals("exists")) {
                data.put(str, getMatchHits((Map<String, Object>) raw.get(str)));
            }
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
            if (!str.equals("exists")) {
                data.put(str, getPowercellScore((Map<String, Object>) raw.get(str)));
            }
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
            if (!str.equals("exists")) {
                data.put(str, getMatchOuterHits((Map<String, Object>) raw.get(str)));
            }
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
            if (!str.equals("exists")) {
                data.put(str, getMatchInnerHits((Map<String, Object>) raw.get(str)));
            }
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
        scoutDataDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "data retrieval task successful.");
                DocumentSnapshot docSnap = task.getResult();
                if (docSnap.exists()) {
                    Log.i(TAG, "scouting data exists, retrieving data.");

                    updateCharts(docSnap.getData());
                    ((SwipeRefreshLayout)findViewById(R.id.stats_pc_swipe_layout)).setRefreshing(false);
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
