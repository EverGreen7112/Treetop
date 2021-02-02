package com.evergreen.treetop;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SC_ScoutingForm extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabLayout = findViewById(R.id.sc_scouting_form_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.sc_scouting_form_auto_tab_text); break;
                case 1: tab.setText(R.string.sc_scouting_form_teleop_tab_text); break;
                case 2: tab.setText(R.string.sc_scouting_form_end_tab_text); break;
            }
        }).attach();
    }
}
