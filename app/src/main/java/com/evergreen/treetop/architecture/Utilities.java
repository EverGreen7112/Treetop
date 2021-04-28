package com.evergreen.treetop.architecture;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.evergreen.treetop.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utilities {
    public static <T> String stringify(List<T> list) {
        return  stringify(list, obj -> obj);
    }
    public static <K, V> String stringify(Map<K, V> map) {
        return "{" +
                map.entrySet().stream()
                        .map(entry -> entry.getKey().toString() + ": " + entry.getValue().toString())
                        .collect(Collectors.joining(","))
                + "}";
    }

    public static <T> String stringify(List<T> list, Function<T, Object> propertyMapper) {
        return "[" + list.stream().map(propertyMapper).map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    public static <T> String stringify(T[] arr) {
        return stringify(Arrays.asList(arr));
    }

    public static final PlaceholderObject PLACEHOLDER_OBJECT = new PlaceholderObject();
    private static class PlaceholderObject {
        public final boolean exists = true;
        private PlaceholderObject() {}
    }

    public static void setBackgroundColor(Context context, View view, int colorId) {
        Drawable background = view.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, colorId));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(context, colorId));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(context, colorId));
        }

    }
}
