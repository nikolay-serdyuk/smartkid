package com.nserdyuk.smartkid.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.nserdyuk.smartkid.common.Constants;

public class ClockfaceView extends AppCompatImageView {
    private final static float MINUTE_HAND_FACTOR = 0.7f;
    private final static float HOUR_HAND_FACTOR = 0.5f;
    private final static int DEFAULT_MINUTE_HAND_WIDTH = 7;
    private final static int DEFAULT_HOUR_HAND_WIDTH = 10;
    private final static int DEFAULT_HOUR_COLOR = Color.RED;
    private final static int DEFAULT_MINUTE_COLOR = Color.BLACK;
    private final static int DEFAULT_HOUR = 1;
    private final static int DEFAULT_MINUTE = 15;

    private int hour = DEFAULT_HOUR;
    private int minute = DEFAULT_MINUTE;
    private int hourHandColor;
    private int minuteHandColor;
    private int hourHandWidth;
    private int minuteHandWidth;

    private Paint paint;

    public ClockfaceView(Context context) {
        super(context);
        init();
    }

    public ClockfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public int getHourHandWidth() {
        return hourHandWidth;
    }

    public void setHourHandWidth(int hourHandWidth) {
        this.hourHandWidth = hourHandWidth;
    }

    public int getMinuteHandWidth() {
        return minuteHandWidth;
    }

    public void setMinuteHandWidth(int minuteHandWidth) {
        this.minuteHandWidth = minuteHandWidth;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        invalidate();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        invalidate();
    }

    public int getHourHandColor() {
        return hourHandColor;
    }

    public void setHourHandColor(int hourHandColor) {
        this.hourHandColor = hourHandColor;
    }

    public int getMinuteHandColor() {
        return minuteHandColor;
    }

    public void setMinuteHandColor(int minuteHandColor) {
        this.minuteHandColor = minuteHandColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHands(canvas);
    }

    private void init() {
        hourHandColor = DEFAULT_HOUR_COLOR;
        minuteHandColor = DEFAULT_MINUTE_COLOR;
        minuteHandWidth = DEFAULT_MINUTE_HAND_WIDTH;
        hourHandWidth = DEFAULT_HOUR_HAND_WIDTH;
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void drawHands(Canvas canvas) {
        drawHand(canvas, minute, MINUTE_HAND_FACTOR, minuteHandColor, minuteHandWidth);
        drawHand(canvas, minute / 12 + hour * 5, HOUR_HAND_FACTOR, hourHandColor, hourHandWidth);
    }

    private void drawHand(Canvas canvas, int minute, float radiusFactor, int color, int width) {
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        float radiusX = centerX * radiusFactor;
        float radiusY = centerY * radiusFactor;
        int angle = (minute * 6 + 270) % 360;
        float endX = centerX + Constants.DEGREE_TO_COS[angle] * radiusX;
        float endY = centerY + Constants.DEGREE_TO_SIN[angle] * radiusY;
        paint.setStrokeWidth(width);
        paint.setColor(color);
        canvas.drawLine(centerX, centerY, endX, endY, paint);
    }

}
