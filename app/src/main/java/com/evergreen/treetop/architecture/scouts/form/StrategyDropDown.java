package com.evergreen.treetop.architecture.scouts.form;

import android.widget.TextView;

import com.evergreen.treetop.architecture.scouts.utils.StrategyOptions;
import com.evergreen.treetop.ui.custom.spinner.BaseSpinner;

import java.util.HashMap;
import java.util.stream.Collectors;

public class StrategyDropDown extends DropDown {

    public StrategyDropDown(StrategyOptions options,  BaseSpinner spinner, TextView labelView, TextView descriptionBox) {
        super(
                options.getType().toString() + "STRATEGIES",
                "strategies/" + options.getType().toString().toLowerCase(),
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
}
