package com.evergreen.treetop.custom_ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.evergreen.treetop.R;

public class CircleTextView extends androidx.appcompat.widget.AppCompatTextView {
    public CircleTextView(@NonNull Context context) {
        super(context);
        initConfigure();
    }

    public CircleTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfigure();
    }

    public CircleTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfigure();
    }

    public void setRadius(int r) {
        super.setHeight(r);
        super.setWidth(r);
    }

    @Override
    public void setHeight(int pixels) {
        setRadius(pixels);
    }

    @Override
    public void setWidth(int pixels) {
        setRadius(pixels);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int r = Math.max(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(r, r);
    }

    private void initConfigure() {
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle));
        setGravity(Gravity.CENTER);
    }

    public void setBackgroundColor(int color) {
        getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}

