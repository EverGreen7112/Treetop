package com.evergreen.treetop.activities.scouts.stats;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.scouts.form.SC_FormLauncher;
import com.evergreen.treetop.activities.tasks.users.TM_SignUpActivity;
import com.evergreen.treetop.architecture.scouts.handlers.MatchDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.seismic.ShakeDetector;

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
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StatsLauncher extends AppCompatActivity implements ShakeDetector.Listener {

    private final String TAG = "StatsLauncher_sc";

    public static DocumentReference scoutDataDoc;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ListView teamList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_launcher);

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

        teamList = findViewById(R.id.stats_launcher_list);

        teamList.setOnItemClickListener((parent, view, position, id) -> {
            String team = (String)parent.getItemAtPosition(position);
            scoutDataDoc = new MatchDB(Integer.parseInt(team)).getRef();
            startActivity(new Intent(this, GeneralStats.class));
        });

        getRegisteredTeams();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_global_options_menu, menu);
        menu.setGroupVisible(R.id.global_menu_tm, false);
        menu.setGroupVisible(R.id.global_menu_sc, false);
        menu.setGroupVisible(R.id.global_menu_stats_general, false);
        menu.setGroupVisible(R.id.global_menu_stats_pc, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_stats_scout) {
            startActivity(new Intent(this, SC_FormLauncher.class));
        } else if (itemId == R.id.menu_stats_tm) {
            startActivity(new Intent(this, TM_SignUpActivity.class));
        }

        return true;
    }

    @Override
    public void hearShake() {
        startActivity(new Intent(this, SC_FormLauncher.class));
    }

    private void getRegisteredTeams() {
        List<String> teams = new ArrayList<>();

        db.collection("Scouting").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "data retrieval task successful.");
                        for (QueryDocumentSnapshot team : task.getResult()) {
                            teams.add(team.getId());
                        }
                        Log.v(TAG, teams.toString());
                        teamList.setAdapter(new ArrayAdapter<>(this, R.layout.listrow_stats_teams_picker, teams));
                    }
                    else {
                        Log.d(TAG, "data retrieval task failed.");
                    }
                });
    }
}
