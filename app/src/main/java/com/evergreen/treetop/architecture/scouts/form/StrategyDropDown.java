package com.evergreen.treetop.architecture.scouts.form;

import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.utils.StrategyOptions;
import com.evergreen.treetop.ui.views.spinner.BaseSpinner;

import java.util.stream.Collectors;

public class StrategyDropDown extends DropDown {

    public StrategyDropDown(StrategyOptions options,  BaseSpinner spinner, TextView labelView, TextView descriptionBox) {
        super(
                options.getType() + " Strategy",
                options.getType().toLowerCase() + "-strategy",
                spinner,
                labelView,
                descriptionBox,
                options.getOptions().stream().collect(
                        Collectors.toMap(
                                StrategyOptions.StrategyOption::getKey,
                                StrategyOptions.StrategyOption::getDescription
                        )
                )
        );
    }

    @Override
    protected String getType() {
        return "Strategy Dropdown";
    }
}
