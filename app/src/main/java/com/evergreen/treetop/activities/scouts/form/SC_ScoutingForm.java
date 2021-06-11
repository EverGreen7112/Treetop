package com.evergreen.treetop.activities.scouts.form;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.ui.fragments.form.SC_FormAutoFragment;
import com.evergreen.treetop.ui.fragments.form.SC_FormEndgameFragment;
import com.evergreen.treetop.ui.fragments.form.SC_FormSubmissionFragment;
import com.evergreen.treetop.ui.fragments.form.SC_FormTeleopFragment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SC_ScoutingForm extends AppCompatActivity {

    private final String TAG = "ScoutingForm_sc";

    TabLayout m_tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting_form_sc);

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

        m_tabLayout = findViewById(R.id.sc_form_tab_layout);

        adapter = new ViewPagerAdapter(this);

        viewPager = findViewById(R.id.sc_form_pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(m_tabLayout, viewPager, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("auto");
                    break;
                case 1:
                    tab.setText("teleop");
                    break;
                case 2:
                    tab.setText("endgame");
                    break;
                case 3:
                    tab.setText("submit");
                    break;
            }
        })).attach();

        ScoutingMatch.getCurrent().start();
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new SC_FormAutoFragment();
                case 1:
                    return new SC_FormTeleopFragment();
                case 2:
                    return new SC_FormEndgameFragment();
                case 3:
                    return new SC_FormSubmissionFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
