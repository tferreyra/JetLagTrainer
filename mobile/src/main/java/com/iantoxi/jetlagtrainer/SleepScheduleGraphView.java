package com.iantoxi.jetlagtrainer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Josef Nunez on 7/29/15.
 *
 * View that draws Sleep Schedule.
 */
public class SleepScheduleGraphView extends View {
    // Shift to place sine values on graph.
    private int verticalShift;
    // Amplitude to fill space on graph.
    private int amplitude;
    // Graph width.
    private int graphWidth;
    // Paint object to use throughout drawing process.
    Paint paint;
    // Rect object to use throughout drawing process.
    Rect rect;
    // Stroke Width for Paint Object. Determines width of sine curve line.
    private final float STROKE_WIDTH = 6f;
    // y-coordinate of Current Time axis labels.
    private int yCurrentTimeAxis;
    // y-coordinate of Target Time axis labels.
    private int yTargetTimeAxis;
    // Height of axes labels.
    private int axesTextSize;
    // Canvas to draw sleep schedule.
    private Canvas mCanvas;

    public SleepScheduleGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint.FontMetrics fm = new Paint.FontMetrics();
        paint = new Paint();
        paint.getFontMetrics(fm);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        // Get max width and height.
        int xMax = getWidth();
        int yMax = getHeight();

        // Set x-coordinates of left and right border.
        int left = 5;
        int right = xMax - 5;

        // Set heights of different graph sections:
        // 1) Title bar heights.
        // 2) Font size of titles and axes labels.
        // 3) Graph size (sine function section of View).
        int timeLabelsHeight = 120;
        int timeLabelsFontSize = timeLabelsHeight/2;
        int timeAxesHeight = 90;
        axesTextSize = timeAxesHeight/2;
        int graphHeight = 400;

        // Draw Current Time title bar.
        int topcurrentTimeTitle = 0;
        int bottomCurrentTimeTitle = timeLabelsHeight;
        setPaintAttributes(paint, Color.BLACK, Paint.Style.FILL);
        drawRect(canvas, left, topcurrentTimeTitle, right, bottomCurrentTimeTitle, paint);
        setPaintAttributes(paint, Color.WHITE, timeLabelsFontSize, Paint.Align.LEFT);
        canvas.drawText("Current Time", left, bottomCurrentTimeTitle-20, paint);

        // Draw Current Time axis.
        int topCurrentTimeAxis = bottomCurrentTimeTitle;
        int bottomCurrentTimeAxis = topCurrentTimeAxis + timeAxesHeight;
        yCurrentTimeAxis = bottomCurrentTimeAxis;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, left, topCurrentTimeAxis, right, bottomCurrentTimeAxis, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, left, topCurrentTimeAxis, right, bottomCurrentTimeAxis, paint);

        // Draw Graph. Dark shaded night half created by additional rectangle.
        int topGraph = bottomCurrentTimeAxis;
        int bottomGraph = topGraph + graphHeight;
        int topNight = topGraph + (graphHeight/2);
        int bottomNight = bottomGraph;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, left, topGraph, right, bottomGraph, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, left, topGraph, right, bottomGraph, paint);
        setPaintAttributes(paint, Color.GRAY, Paint.Style.FILL);
        drawRect(canvas, left, topNight, right, bottomNight, paint);

        // Save graph mid-line coordinate and height of graph.
        verticalShift = topNight;
        amplitude = Math.abs((graphHeight/2) - 5);
        graphWidth = right - left;

        // Draw Target Time axis.
        int topTargetTimeAxis = bottomGraph;
        int bottomTargetTimeAxis = topTargetTimeAxis + timeAxesHeight;
        yTargetTimeAxis = bottomTargetTimeAxis;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, left, topTargetTimeAxis, right, bottomTargetTimeAxis, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, left, topTargetTimeAxis, right, bottomTargetTimeAxis, paint);

        // Draw Target Time label.
        int topTargetTimeTitle = bottomTargetTimeAxis;
        int bottomTargetTimeTitle = topTargetTimeTitle + timeLabelsHeight;
        setPaintAttributes(paint, Color.BLACK, Paint.Style.FILL);
        drawRect(canvas, left, topTargetTimeTitle, right, bottomTargetTimeTitle, paint);
        setPaintAttributes(paint, Color.WHITE, timeLabelsFontSize, Paint.Align.LEFT);
        canvas.drawText("Target Time", left, bottomTargetTimeTitle-20, paint);

        // Draw Border around the entire view.
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, left, topcurrentTimeTitle, right, bottomTargetTimeTitle, paint);

        // Draw the schedule on the phone.
        setSleepSchedule(25200, 70000, 10800);
    }

    private float convertSecToHourFloat(float seconds) {
        return seconds / 3600;
    }

    private int converSecToHourInt(float seconds) {
        return (int) (seconds / 3600);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style) {
        p.setColor(color);
        p.setStyle(style);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style, float strokeWidth) {
        setPaintAttributes(p, color, style);
        p.setStrokeWidth(strokeWidth);
    }

    private void setPaintAttributes(Paint p, int color, float textSize, Paint.Align textAlign) {
        p.setColor(color);
        p.setTextSize(textSize);
        p.setTextAlign(textAlign);
    }

    private void drawRect(Canvas c, int left, int top, int right, int bottom, Paint p) {
        rect.set(left, top, right, bottom);
        c.drawRect(left, top, right, bottom, p);
    }

    /**
     * Draw daylight cycle and sleep schedule. Assumes bedTime and wakeTime
     * given in seconds from midnight.
     */
    public void setSleepSchedule(float bedTime, float wakeTime, float timeDiff) {
        Paint black = new Paint();
        setPaintAttributes(black, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        Paint white = new Paint();
        setPaintAttributes(white, Color.WHITE, Paint.Style.STROKE, STROKE_WIDTH);
        int initialTime = 12;
        int terminalTime = 36;
        float delta = graphWidth/((terminalTime - initialTime)*10);
        float x0 = 5;
        float y0 = daylightCycle(bedTime);
        // Graphs from Noon to Noon.
        float eps = (float) Math.pow(10.0, -3.0);
        for (float x1 = initialTime; x1 < terminalTime+1; x1+=0.10) {
            float y1 = daylightCycle((float) x1);
            if (y1 > verticalShift) {
                mCanvas.drawLine(x0, y0, x0+delta, y1, white);
            } else {
                mCanvas.drawLine(x0, y0, x0+delta, y1, black);
            }
            if (Math.abs(x1-Math.round(x1)) < eps) {
                drawAxisLabel(x0, (float) x1, timeDiff);
            }
            x0 += delta;
            y0 = y1;
        }
    }

    /**
     * Draw axis label, current and target time.
     * @param x        x-coordinate to begin text
     * @param time     military time entry to add to axis, added to axis mod 12
     */
    private void drawAxisLabel(float x, float time, float timeDiff) {
        setPaintAttributes(paint, Color.BLACK, 20, Paint.Align.LEFT);
        int currentHour = Math.round(time);
        int targetHour = Math.round(time + timeDiff);
        String currentLabel = null;
        String targetLabel = null;
        if (currentHour <= 12) {
            currentLabel = String.valueOf(currentHour); // + "am"
        } else {
            currentHour = (currentHour % 12) + 1;
            currentLabel = String.valueOf(currentHour); // + "pm"
        }
        if (targetHour <= 12) {
            targetLabel = String.valueOf(targetHour); // + "am"
        } else {
            targetHour = (targetHour % 12) + 1; // + "pm"
            targetLabel = String.valueOf(targetHour); // + "pm"
        }
        mCanvas.drawText(currentLabel, x, yCurrentTimeAxis-40, paint);
        mCanvas.drawText(targetLabel, x, yTargetTimeAxis-40, paint);
    }

    /**
     * Daylight cycle sine funciton. Assumes time in military time,
     * sunrise at 7am (7:00), and sunset at 7pm (19:00).
     *
     * (x,y) coordinates reflected across the x-axis based on Android screen coordinates.
     */
    private float daylightCycle(float hour) {
        return (float) -1*amplitude * (float) Math.sin((Math.PI / (float) 12)*(hour - 7)) + verticalShift;
    }
}
