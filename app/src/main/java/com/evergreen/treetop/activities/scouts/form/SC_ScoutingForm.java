package com.evergreen.treetop.activities.scouts.form;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.data.GameStage;
import com.evergreen.treetop.architecture.scouts.form.FormCounter;
import com.evergreen.treetop.architecture.scouts.form.FormSwitch;
import com.evergreen.treetop.architecture.scouts.form.SequenceSwitch;
import com.evergreen.treetop.ui.fragments.form.SC_FormCountersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SC_ScoutingForm extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_auto_sc);

        SC_FormCountersFragment counterFrag =
                (SC_FormCountersFragment)getSupportFragmentManager().findFragmentById(R.id.sc_form_frag_auto_counter);

        counterFrag.init(GameStage.AUTO);

        new FormSwitch("Passed the Line", "auto/line-pass", findViewById(R.id.sc_form_line_passed_switch), findViewById(R.id.sc_form_line_passed_label));
        new SequenceSwitch("Functions", "auto/function", findViewById(R.id.sc_form_function_switch), findViewById(R.id.sc_form_function_label));
//        new FormCounter("Initial Cell Load", "auto/init-load", findViewById(R.id.sc_form_loaded_counter_valuebox), findViewById(R.id.loaded));

//        tabLayout = findViewById(R.id.sc_scouting_form_tab_layout);
//        viewPager = findViewById(R.id.sc_scouting_form_view_pager);
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            switch (position) {
//                case 0: tab.setText(R.string.sc_scouting_form_auto_tab_text); break;
//                case 1: tab.setText(R.string.sc_scouting_form_teleop_tab_text); break;
//                case 2: tab.setText(R.string.sc_scouting_form_end_tab_text); break;
//            }
//        }).attach();
    }
}
