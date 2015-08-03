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

/**
 * Created by Josef Nunez on 7/29/15.
 *
 * View that draws Sleep Schedule.
 */
public class SleepScheduleGraphView extends View {
    // Stroke Width for Paint Object. Determines width of sine curve line.
    private final float STROKE_WIDTH = 6f;
    // Initial hour on graph.
    private final int INITIAL_TIME = 12;
    // Terminal hour on graph (not mod 24).
    private final int TERMINAL_TIME = 36;
    // x-coordinate for Left border of View.
    private int LEFT;
    // x-coordinate for Right border of View.
    private int RIGHT;
    // Width of View.
    private int WIDTH;
    // Number of seconds per hour.
    private final int NUM_SECS_PER_HOUR = 3600;
    // Number of seconds per day.
    private final int NUM_SECS_PER_DAY = 86400;
    // Shift to place sine values on graph.
    private int verticalShift;
    // Amplitude to fill space on graph.
    private int amplitude;
    // Graph width.
    private int graphWidth;
    // y-coordinate of Current Time axis labels.
    private int yCurrentTimeAxis;
    // y-coordinate of Target Time axis labels.
    private int yTargetTimeAxis;
    // Text size of axes labels.
    private int axesTextSize;
    // Height of axes labels.
    private int timeAxesHeight;
    // Paint object to use throughout drawing process.
    private Paint paint;
    // Rect object to use throughout drawing process.
    private Rect rect;
    // Canvas to draw sleep schedule.
    private Canvas mCanvas;
    // Current Bedtime and Wake Time.
    private float bedTime;
    private float wakeTime;
    // Target Bedtime and Wake Time.
    private float targetBedTime;
    private float targetWakeTime;
    // Time Zone Difference.
    private float timeDiff;

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

        LEFT = getPaddingLeft();
        RIGHT = getWidth() - getPaddingRight();
        WIDTH = RIGHT - LEFT;

        int TOP = getPaddingTop();
        int BOTTOM = getHeight() - getPaddingBottom();
        int HEIGHT = (BOTTOM - TOP);

        // Set heights of different graph sections:
        // 1) Title bar heights.
        // 2) Font size of titles and axes labels.
        // 3) Graph size (sine function section of View).
        timeAxesHeight = (HEIGHT/20)*3;
        axesTextSize = timeAxesHeight/2;
        int graphHeight = (HEIGHT/20)*14;

        // Draw Current Time axis.
        int topCurrentTimeAxis = TOP;
        int bottomCurrentTimeAxis = topCurrentTimeAxis + timeAxesHeight;
        yCurrentTimeAxis = bottomCurrentTimeAxis;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, LEFT, topCurrentTimeAxis, RIGHT, bottomCurrentTimeAxis, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topCurrentTimeAxis, RIGHT, bottomCurrentTimeAxis, paint);

        // Draw Graph. Dark shaded night half created by additional rectangle.
        int topGraph = bottomCurrentTimeAxis;
        int bottomGraph = topGraph + graphHeight;
        int topNight = topGraph + (graphHeight/2);
        int bottomNight = bottomGraph;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, LEFT, topGraph, RIGHT, bottomGraph, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topGraph, RIGHT, bottomGraph, paint);
        setPaintAttributes(paint, Color.GRAY, Paint.Style.FILL);
        drawRect(canvas, LEFT, topNight, RIGHT, bottomNight, paint);

        // Save graph mid-line coordinate and height of graph.
        verticalShift = topNight;
        amplitude = Math.abs((graphHeight/2) - 5);
        graphWidth = RIGHT - LEFT;

        // Draw Target Time axis.
        int topTargetTimeAxis = bottomGraph;
        int bottomTargetTimeAxis = topTargetTimeAxis + timeAxesHeight;
        yTargetTimeAxis = bottomTargetTimeAxis;
        setPaintAttributes(paint, Color.WHITE, Paint.Style.FILL);
        drawRect(canvas, LEFT, topTargetTimeAxis, RIGHT, bottomTargetTimeAxis, paint);
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topTargetTimeAxis, RIGHT, bottomTargetTimeAxis, paint);

        // Draw Border around the entire view.
        setPaintAttributes(paint, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        drawRect(canvas, LEFT, topCurrentTimeAxis, RIGHT, bottomTargetTimeAxis, paint);

        // Draw the schedule on the phone.
        drawSleepSchedule(bedTime, wakeTime, targetBedTime, targetWakeTime, timeDiff);
    }

    private float convertSecToHourFloat(float seconds) {
        return seconds / NUM_SECS_PER_HOUR;
    }

    private int converSecToHourInt(float seconds) {
        return (int) (seconds / NUM_SECS_PER_HOUR);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style) {
        p.setColor(color);
        p.setStyle(style);
    }

    private void setPaintAttributes(Paint p, int color, Paint.Style style, float strokeWidth) {
        setPaintAttributes(p, color, style);
        p.setStrokeWidth(strokeWidth);
    }

    private void setPaintAttributes(Paint p, int color, float textSize, Paint.Align textAlign, Typeface typeface) {
        p.setColor(color);
        p.setTextSize(textSize);
        p.setTextAlign(textAlign);
        p.setTypeface(typeface);
    }

    private void drawRect(Canvas c, int left, int top, int right, int bottom, Paint p) {
        rect.set(left, top, right, bottom);
        c.drawRect(left, top, right, bottom, p);
    }

    /**
     * Pass in sleep schedule information to be plotted.
     * @param bedTime            current bedTime in seconds from midnight
     * @param wakeTime           current wakeTime in seconds from midnight
     * @param targetBedTime      target bedTime in seconds from midnight
     * @param targetWakeTime     target wakeTime in seconds from midnight
     * @param timeDiff           (target - current) time difference in hours
     */
    public void setSleepSchedule(float bedTime, float wakeTime, float targetBedTime,
                                 float targetWakeTime, float timeDiff) {
        this.bedTime = bedTime;
        this.wakeTime = wakeTime;
        this.targetBedTime = targetBedTime;
        this.targetWakeTime = targetWakeTime;
        this.timeDiff = timeDiff;
        invalidate();
    }

    /**
     * Draw daylight cycle and sleep schedule. Assumes bedTime and wakeTime
     * given in seconds from midnight.
     * @param bedTime            current bedTime in seconds from midnight
     * @param wakeTime           current wakeTime in seconds from midnight
     * @param targetBedTime      target bedTime in seconds from midnight
     * @param targetWakeTime     target wakeTime in seconds from midnight
     * @param timeDiff           (target - current) time difference in hours
     */
    private void drawSleepSchedule(float bedTime, float wakeTime, float targetBedTime,
                                   float targetWakeTime, float timeDiff) {
        Paint black = new Paint();
        setPaintAttributes(black, Color.BLACK, Paint.Style.STROKE, STROKE_WIDTH);
        Paint white = new Paint();
        setPaintAttributes(white, Color.WHITE, Paint.Style.STROKE, STROKE_WIDTH);
        float delta = (float) graphWidth/ (float) ((TERMINAL_TIME - INITIAL_TIME)*100);
        float delta2 = (float) (TERMINAL_TIME - INITIAL_TIME) / (float) ((TERMINAL_TIME - INITIAL_TIME)*100);
        float x0 = LEFT;
        float y0 = daylightCycle(bedTime);
        // Graphs from Noon to Noon.
        float eps = (float) Math.pow(10.0, -2.0);
        Log.d("delta2:", String.valueOf(delta2));
        for (float x1 = INITIAL_TIME; x1 <= TERMINAL_TIME; x1+=delta2) {
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

        // Draw shaded region for sleep times.
        // Note: Graph y-values based on target time zone, so
        // need to shift given current bedTime and wakeTime by timeDiff.
        // timeDiff given as (target - current) time zone.
         drawSleepRegion(bedTime-timeDiff, wakeTime-timeDiff, Color.CYAN, 40);
         drawSleepRegion(targetBedTime, targetWakeTime, Color.YELLOW, 20);
    }

    /**
     * Draw axis label, current and target hour.
     * @param x          x-coordinate to begin text
     * @param time       current time in hours (may be greater than 24)
     * @param timeDiff   (target - current) time difference in hours
     */
    private void drawAxisLabel(float x, float time, float timeDiff) {
        setPaintAttributes(paint, Color.BLACK, axesTextSize, Paint.Align.LEFT, Typeface.MONOSPACE);
        paint.setStrokeWidth(0f);
        int targetHour = Math.round(time) % 24;
        int currentHour = Math.round(time - timeDiff) % 24;
        String currentLabel = null;
        String targetLabel = null;
        if (targetHour % 4 == 0) {
            if (currentHour == 0) {
                currentLabel = "12am";
            } else if (currentHour < 12) {
                currentLabel = String.valueOf(currentHour) + "am";
            } else if (currentHour == 12) {
                currentLabel = String.valueOf(currentHour) + "pm";
            } else {
                currentHour = currentHour % 12;
                currentLabel = String.valueOf(currentHour) + "pm";
            }
            if (targetHour == 0) {
                targetLabel = "12am";
            } else if (targetHour < 12) {
                targetLabel = String.valueOf(targetHour) + "am";
            } else if (targetHour == 12) {
                targetLabel = String.valueOf(targetHour) + "pm";
            } else {
                targetHour = targetHour % 12;
                targetLabel = String.valueOf(targetHour) + "pm";
            }
            mCanvas.drawText(currentLabel, x+10, yCurrentTimeAxis - (float) timeAxesHeight/ (float) 3, paint);
            mCanvas.drawText(targetLabel, x+10, yTargetTimeAxis - (float) timeAxesHeight/ (float) 3, paint);
        }
    }

    /**
     * Draw sleep region on graph. Currently only supports startTime
     * in the after noon, and endTime before noon.
     * @param startTime   time input in seconds from midnight
     * @param endTime     time input in seconds from midnight
     * @param color       color of striped region
     * @param alpha       integer from 0-255 indicating transparency level
     */
    private void drawSleepRegion(float startTime, float endTime, int color, int alpha) {
        float delta = graphWidth/((TERMINAL_TIME - INITIAL_TIME)*10);
        startTime = convertSecToHourFloat(startTime);   // assumes startTime after noon
        endTime = convertSecToHourFloat(endTime) + 24;  // assumes endTime before noon
        int left = (int) (INITIAL_TIME + ((startTime - INITIAL_TIME)/.10f)*delta);
        int right = (int) (INITIAL_TIME + ((endTime - INITIAL_TIME)/.10f)*delta);
        int top = verticalShift - amplitude;
        int bottom = verticalShift + amplitude;
        setPaintAttributes(paint, color, Paint.Style.FILL);
        paint.setAlpha(alpha);
        drawRect(mCanvas, left, top, right, bottom, paint);
//        drawStripedRegion(left, verticalShift - amplitude, right, verticalShift + amplitude,
//                2, 10, color, 2f);
    }

    /**
     * Draw striped region on graph. Note that Android device y-coordinates are
     * inverted, i.e. y increases as you move down the graph.
     * @param left         left border coordinate (x value)
     * @param top          top border coordinate (y value)
     * @param right        right border coordinate (x value)
     * @param bottom       bottom border coordinate (y value)
     * @param slope        slope of stripes, must be positive
     * @param numStripes   number of stripes
     * @param color        color of striped region; use Color class fields.
     * @param stripeWidth  width of each stripe
     */
    private void drawStripedRegion(float left, float top, float right, float bottom,
                                   float slope, int numStripes, int color, float stripeWidth) {
        // Draw region border.
        setPaintAttributes(paint, color, Paint.Style.STROKE, stripeWidth*1.5f);
        mCanvas.drawLine(left, top, left, bottom, paint);     // Left border
        mCanvas.drawLine(left, top, right, top, paint);       // Top border
        mCanvas.drawLine(right, top, right, bottom, paint);   // Right border
        mCanvas.drawLine(left, bottom, right, bottom, paint); // Bottom border

        // Draw stripes.
        setPaintAttributes(paint, color, Paint.Style.STROKE, stripeWidth);
        float yDelta = (bottom - top) / numStripes;
        float y0 = 0f;
        float y1 = 0f;
        for (y0 = top; y1 <= bottom; y0 += yDelta) {
            y1 = -1*(right-left)*slope + y0;
            if (y1 < top && y0 > bottom) {
                // Cut line off at both top and bottom of graph.
                float x0 = left + (y0-bottom)/slope;
                float x1 = left + (y0-top)/slope;
                if (x0 <= right && x1 <= right) {
                    mCanvas.drawLine(x0, bottom, x1, top, paint);
                }
            } else if (y1 < top) {
                // Cut line off at top of graph.
                float x1 = left + (y0-top)/slope;
                if (x1 <= right) {
                    mCanvas.drawLine(left, y0, x1, top, paint);
                }
            } else if (y0 > bottom) {
                // Cut line off at bottom of graph.
                float x0 = left + (y0-bottom)/slope;
                if (x0 <= right) {
                    mCanvas.drawLine(x0, bottom, right, y1, paint);
                }
            } else {
                mCanvas.drawLine(left, y0, right, y1, paint);
            }
        }
    }

    /**
     * Daylight cycle sine funciton. Assumes input in military time,
     * sunrise at 7am (7:00), and sunset at 7pm (19:00).
     *
     * (x,y) coordinates reflected across the x-axis based on Android screen coordinates.
     *
     * @param hour    hour in current time
     */
    private float daylightCycle(float hour) {
        return (float) -1*amplitude * (float) Math.sin((Math.PI / (float) 12)*(hour - 7)) + verticalShift;
    }
}
