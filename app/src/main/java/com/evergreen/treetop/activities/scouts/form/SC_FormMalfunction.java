package com.evergreen.treetop.activities.scouts.form;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.evergreen.treetop.R;

public class SC_FormMalfunction extends AppCompatActivity {

    private final String TAG = "FormMalfunction_sc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_malfunction_sc);

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
    }
}
