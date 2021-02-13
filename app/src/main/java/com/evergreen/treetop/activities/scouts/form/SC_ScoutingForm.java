package com.evergreen.treetop.activities.scouts.form;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.scouts.utils.ScoutingMatch;
import com.evergreen.treetop.ui.fragments.form.SC_FormAutoFragment;
import com.evergreen.treetop.ui.fragments.form.SC_FormEndgameFragment;
import com.evergreen.treetop.ui.fragments.form.SC_FormTeleopFragment;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SC_ScoutingForm extends AppCompatActivity {
    TabLayout m_tabLayout;
    // ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting_form_sc);

        m_tabLayout = findViewById(R.id.sc_form_tab_layout);
        setContent(new SC_FormAutoFragment());

        m_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        setContent(new SC_FormAutoFragment());
                        break;
                    case 1:
                        setContent(new SC_FormTeleopFragment());
                        break;
                    case 2:
                        setContent(new SC_FormEndgameFragment());
                    case 3:
                        setContent(new SC_FormSubmissionFragment());
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ScoutingMatch.getCurrent().start();
    }

    private void setContent(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        for (Fragment frag : getSupportFragmentManager().getFragments()) {
            fragmentTransaction.remove(frag);
        }

        fragmentTransaction.add(R.id.sc_form_frag_tab_view_content, fragment);
        fragmentTransaction.commit();
    }

}
