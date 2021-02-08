package com.evergreen.treetop.ui.custom.text;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evergreen.treetop.ui.custom.utils.Shape;

public class OvaTextView extends BaseText {

    public OvaTextView(@NonNull Context context) {
        super(context, Shape.OVAL_RECT);
    }

    public OvaTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, Shape.OVAL_RECT);
    }

    public OvaTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, Shape.OVAL_RECT);
    }
}
