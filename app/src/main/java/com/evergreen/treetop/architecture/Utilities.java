package com.evergreen.treetop.architecture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utilities {

    public static <T> String stringify(Collection<T> list, Function<T, Object> propertyMapper, boolean brackets) {
        return (brackets? "[" : "") + list.stream().map(propertyMapper).map(Object::toString).collect(Collectors.joining(", ")) + (brackets? "]" : "");
    }

    public static <T> String stringify(Collection<T> list, Function<T, Object> propertyMapper) {
        return stringify(list, propertyMapper, true);
    }

    public static <T> String stringify(List<T> list) {
        return  stringify(list, obj -> obj);
    }

    public static <T> String stringify(T[] arr) {
        return stringify(Arrays.asList(arr));
    }

    public static <K, V> String stringify(Map<K, V> map) {
        return "{" +
                map.entrySet().stream()
                        .map(entry -> entry.getKey().toString() + ": " + entry.getValue().toString())
                        .collect(Collectors.joining(","))
                + "}";
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

    public static Goal dummyGoal(String id) {

        Goal goal = new Goal(0, id, "Test Goal " + id, "this is a test goal", new Unit("TestUnit"));
        goal.addTaskById("hello");
        goal.addTaskById("test");
        goal.addTaskById("Long one");
        goal.addTaskById("and");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");
        goal.addTaskById("see?");

        return goal;
    }

    public static AppTask dummyTask(String id) {

        Goal goal = new Goal(0, id + "-parent", "Test Goal", "this is a test goal", new Unit("TestUnit"));
        AppTask task =
                new AppTask(0,
                        id,
                        "Test Task",
                        "this is a test task",
                        new Unit("TestUnit"),
                        "test-id",
                        LocalDate.now().plus(Period.of(0, 0, 1)),
                        LocalDate.now().plus(Period.of(0, 0, 3)),
                        new User("Test assigner"),
                        id.length() > 5
                );
        task.addTaskById("hello");
        task.addTaskById("test");
        task.addTaskById("Long one");
        task.addTaskById("and");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");
        task.addTaskById("see?");

        task.addAssignee(new User("Test Assignee 1"));
        task.addAssignee(new User("Test Assignee 2"));

        return task;
    }


    public static String priorityChar(int priority) {

        switch (priority) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            default:
                return null;
        }
    }


    public static int priorityNum(String charc) {
        switch (charc) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            case "E":
                return 4;
            default:
                return -1;
        }
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        float alpha = 0;

        if (toVisibility == View.VISIBLE) {
            alpha = toAlpha;
            view.setAlpha(alpha);
        }

        view.setVisibility(View.VISIBLE);
        view.animate()
            .setDuration(duration)
            .alpha(alpha)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(toVisibility);
                }
            });
    }

    public static void showLoading(Activity context) {
        animateView(context.findViewById(R.id.loading_overlay), View.VISIBLE, 0.4f, 200);
    }

    public static void showLoading(Activity context, int mainViewId) {
        showLoading(context);
        animateView(context.findViewById(mainViewId), View.VISIBLE, 0.4f, 200);
    }

    public static void hideLoading(Activity context) {
        animateView(context.findViewById(R.id.loading_overlay), View.GONE, 0.4f, 200);
    }

    public static void hideLoading(Activity context, int mainViewId) {
        hideLoading(context);
        animateView(context.findViewById(mainViewId), View.VISIBLE, 1, 200);
    }

    public static class NoSuchDocumentException extends Exception {

        public NoSuchDocumentException(String message) {
            super(message);
        }

        public NoSuchDocumentException(Throwable cause) {
            super(cause);
        }

        public NoSuchDocumentException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
