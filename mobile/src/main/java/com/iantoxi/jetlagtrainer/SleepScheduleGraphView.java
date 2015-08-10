package com.iantoxi.jetlagtrainer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

/**
 * Created by Josef Nunez on 7/29/15.
 *
 * View that draws Sleep Schedule.
 */
public class SleepScheduleGraphView extends View {
    // Initial and Terminal minutes on graph (graph begins at 12pm and ends at 12pm on next day).
    private final int INITIAL_TIME = 720;
    private final int TERMINAL_TIME = 2160;
    // Paint object to use throughout drawing process.
    private Paint paint;
    // Current Bedtime and Wake Time.
    private int bedTime;
    private int wakeTime;
    // Target Bedtime and Wake Time.
    private int targetBedTime;
    private int targetWakeTime;
    // Time Zone Difference in minutes.
    private int timeDiff;

    public SleepScheduleGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint.FontMetrics fm = new Paint.FontMetrics();
        paint = new Paint();
        paint.getFontMetrics(fm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw shaded region for sleep times. Note: graph dependent on target time zone.
        drawSleepRegion(canvas, bedTime+timeDiff, wakeTime+timeDiff, Color.CYAN, 20);  // Put local bedtime into target time.
        drawSleepRegion(canvas, targetBedTime, targetWakeTime, Color.YELLOW, 30);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style) {
        p.setColor(color);
        p.setStyle(style);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style, float strokeWidth) {
        setPaintAttributes(p, color, style);
        p.setStrokeWidth(strokeWidth);
    }

    private void drawRect(Canvas c, int left, int top, int right, int bottom, Paint p) {
        c.drawRect(left, top, right, bottom, p);
    }

    /**
     * Pass in sleep schedule information to be plotted.
     * @param bedTime            current bedTime in minutes from midnight (domain: 0-2880)
     * @param wakeTime           current wakeTime in minutes from midnight (domain: 0-2880)
     * @param targetBedTime      target bedTime in minutes from midnight (domain: 0-2880)
     * @param targetWakeTime     target wakeTime in minutes from midnight (domain: 0-2880)
     * @param timeDiff           (target - current) time difference in hours
     */
    public void setSleepSchedule(int bedTime, int wakeTime, int targetBedTime,
                                 int targetWakeTime, int timeDiff) {
        this.bedTime = bedTime;
        this.wakeTime = wakeTime;
        this.targetBedTime = targetBedTime;
        this.targetWakeTime = targetWakeTime;
        this.timeDiff = timeDiff*60; // convert from hours to minutes
        invalidate();
    }

    /**
     * Draw sleep region on graph. Currently only supports startTime
     * in the after noon, and endTime before noon.
     * @param startTime   time input in hours from midnight (domain: 0-2880)
     * @param endTime     time input in hours from midnight (domain: 0-2880)
     * @param color       color of striped region
     * @param alpha       integer from 0-255 indicating transparency level
     */
    private void drawSleepRegion(Canvas mCanvas, float startTime, float endTime, int color, int alpha) {
        final int LEFT = getPaddingLeft();
        final int RIGHT = getWidth() - getPaddingRight();
        final int TOP_GRAPH = getPaddingTop();
        final int BOTTOM_GRAPH = getHeight() - getPaddingBottom();
        final float NUM_POINTS = 100f;
        float delta = (float) (RIGHT - LEFT) / NUM_POINTS;
        float delta2 = (float) (TERMINAL_TIME - INITIAL_TIME) / NUM_POINTS;
        int left = (int) (LEFT + ((startTime - INITIAL_TIME)/delta2) * delta);
        int right = (int) (LEFT + ((endTime - INITIAL_TIME)/delta2)*delta);
        setPaintAttributes(paint, color, Paint.Style.FILL);
        paint.setAlpha(alpha);
        drawRect(mCanvas, left, TOP_GRAPH, right, BOTTOM_GRAPH, paint);
        // Draw lines at left and right borders of region.
        paint.setStrokeWidth(2f);
        mCanvas.drawLine(left, BOTTOM_GRAPH, left, TOP_GRAPH, paint);
        mCanvas.drawLine(right, BOTTOM_GRAPH, right, TOP_GRAPH, paint);
    }
}
