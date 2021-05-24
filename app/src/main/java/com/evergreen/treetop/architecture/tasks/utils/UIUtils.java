package com.evergreen.treetop.architecture.tasks.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.evergreen.treetop.R;
import com.evergreen.treetop.architecture.tasks.data.AppTask;
import com.evergreen.treetop.architecture.tasks.data.Goal;
import com.evergreen.treetop.architecture.tasks.handlers.GoalDB;
import com.evergreen.treetop.architecture.tasks.handlers.TaskDB;

public class UIUtils {



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

    public static void deleteTaskDialouge(Activity context, AppTask taskToDelete) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("Are you sure you want to delete the task?\n" +
                "This action is permanent and cannot be undone.");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
//            TaskDB.getInstance().delete(taskToDelete);
            context.finish();
        });
        alertBuilder.setNegativeButton("No", null);
        alertBuilder.create().show();
    }


    public static void deleteGoalDialouge(Activity context, Goal goal) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("Are you sure you want to delete the task?\n" +
                "This action is permanent and cannot be undone.");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
//            GoalDB.getInstance().delete(goal);
            context.finish();
        });
        alertBuilder.setNegativeButton("No", null);
        alertBuilder.create().show();
    }

}
