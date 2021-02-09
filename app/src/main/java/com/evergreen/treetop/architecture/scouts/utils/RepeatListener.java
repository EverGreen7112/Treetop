package com.evergreen.treetop.architecture.scouts.utils;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the <code>initialInterval</code>, and subsequent
 * ones after the <code>normalInterval</code>.
 *
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 */
public class RepeatListener implements OnTouchListener, Loggable {

    private final Handler handler = new Handler();

    private final int m_initTime;
    private final int m_interval;
    private final OnClickListener m_init;
    private final OnClickListener m_execute;
    private final OnClickListener m_end;
    private final String m_label;
    private View m_touchedView;

    private final Runnable m_actionWrapper = new Runnable() {
        @Override
        public void run() {
            if(m_touchedView.isEnabled()) {
                handler.postDelayed(this, m_interval);
                m_execute.onClick(m_touchedView);
                Log.d(getTag(), "RepeatListener \"" + getLabel() + "\" EXECUTE");
            } else {
                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(m_actionWrapper);
                m_touchedView.setPressed(false);
                m_touchedView = null;
            }
        }
    };

    /**
     * @param initTime The interval after first click event
     * @param interval The interval after second and subsequent click
     * @param init initialization action. May be null to do nothing.
     * @param execute action to execute while the view is held. May be null to do nothing.
     * @param end action to run when the view stops being held. May be null to do nothing.
     */
    public RepeatListener(String label, int initTime, int interval,
                          OnClickListener init,
                          OnClickListener execute,
                          OnClickListener end) {
        m_label = label;

        if (init == null) {
            m_init = (v) -> {};
        } else {
            m_init = init;
        }

        if (execute == null) {
            m_execute = (v) -> {};
        } else {
            m_execute = execute;
        }

        if (end == null) {
            m_end = (v) -> {};
        } else {
            m_end = end;
        }

        if (initTime < 0 || interval < 0) {
            throw new IllegalArgumentException("negative interval");
        }

        m_initTime = initTime;
        m_interval = interval;

        Log.i("ACTION_OBJECT", "Initialized RepeatListener \"" + label + "\", with " +
                "initial wait of " + initTime + " and an interval of " + interval);

    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: // When first held down
                Log.d(getTag(), "RepeatListener \"" + getLabel() + "\" ran DOWN" );
                handler.removeCallbacks(m_actionWrapper); // Clear the handler
                handler.postDelayed(m_actionWrapper, m_initTime); // Schedule the repeated task
                m_touchedView = view; //
                m_touchedView.setPressed(true);
                m_init.onClick(view);
                Log.d(getTag(), "RepeatListener \"" + getLabel() + "\" INIT");
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(getTag(), "RepeatListener \"" + getLabel() + "\" ran UP" );
                view.performClick();
            case MotionEvent.ACTION_CANCEL:
                Log.d(getTag(), "RepeatListener \"" + getLabel() + "\" ran CANCEL" );
                handler.removeCallbacks(m_actionWrapper);
                m_end.onClick(view);
                Log.d(getTag(), "RepeatListener \"" + getLabel() + "\" END");
                m_touchedView.setPressed(false); //
                m_touchedView = null;
                return true;
        }

        return false;

        // TODO add label + logging
    }

    @Override
    public String getLabel() {
        return m_label;
    }

    private String getTag() {
        return "REPEAT_HANDLER";
    }
}