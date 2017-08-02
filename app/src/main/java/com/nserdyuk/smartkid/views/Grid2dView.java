package com.nserdyuk.smartkid.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.nserdyuk.smartkid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid2dView extends View {

    private final static int COLOR_TEXT = Color.BLACK;
    private final static int WIDTH_DOT = 15;
    private final static int WIDTH_GRID_MINOR = 6;
    private final static int SIZE_TEXT = 30;
    private final static int COLUMNS = 7;
    private final static int ROWS = 10;
    private final static int TEXT_LEFT_MARGIN = 35;
    private final static int TEXT_TOP_MARGIN = 15;

    private Paint paint;
    private Canvas canvas;
    private int leftMargin;
    private int topMargin;
    private int maxX;
    private int maxY;
    private int columnWidth;
    private int rowHeight;
    private int textSize = SIZE_TEXT;
    private int xAxisColor = Color.BLACK;
    private int yAxisColor = Color.BLACK;
    private int dotColor = Color.BLACK;
    private int barColor;
    private int dotWidth = WIDTH_DOT;
    private int minorAxisWidth = WIDTH_GRID_MINOR;
    private int majorAxisWidth = 2 * WIDTH_GRID_MINOR;
    private int errorColor = Color.RED;

    public Grid2dView(Context context) {
        super(context);
        init(context);
    }

    public Grid2dView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setAxisWidth(int width) {
        this.minorAxisWidth = width;
        this.majorAxisWidth = 2 * width;
    }

    public void setXAxisColor(int color) {
        this.xAxisColor = color;
    }

    public void setYAxisColor(int color) {
        this.yAxisColor = color;
    }

    public void setDotColor(int color) {
        this.dotColor = color;
    }

    public void setDotWidth(int width) {
        this.dotWidth = width;
    }

    public void setBarColor(int color) {
        this.barColor = color;
    }

    public void setTextSize(int size) {
        this.textSize = size;
    }

    public void setErrorColor(int color) {
        this.errorColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        columnWidth = canvas.getWidth() / (COLUMNS);
        rowHeight = canvas.getHeight() / (ROWS);
        maxX = (COLUMNS - 1) * columnWidth;
        maxY = (ROWS - 1) * rowHeight;
        leftMargin = (canvas.getWidth() - maxX) / 2;
        topMargin = (canvas.getHeight() - maxY) / 2;

        drawBar(barColor);

        for (int i = 0; i < COLUMNS; i++) {
            drawLine(i, true, yAxisColor, minorAxisWidth);
        }

        for (int i = 0; i < ROWS; i++) {
            drawLine(i, false, xAxisColor, minorAxisWidth);
        }

        int rows = ROWS - 1;
        drawLine(0, true, yAxisColor, majorAxisWidth);
        drawLine(rows, false, xAxisColor, majorAxisWidth);

        print(Integer.toString(0), rows, false, COLOR_TEXT, textSize);

        for (int i = 0; i < rows; i++) {
            print(Integer.toString(rows - i), i, false, xAxisColor, textSize);
        }

        for (int i = 1; i < COLUMNS; i++) {
            print(Integer.toString(i), i, true, yAxisColor, textSize);
        }

    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        errorColor = ContextCompat.getColor(context, R.color.error);
        barColor = ContextCompat.getColor(context, R.color.grid2dBar);
        xAxisColor = ContextCompat.getColor(context, R.color.xAxis);
        yAxisColor = ContextCompat.getColor(context, R.color.yAxis);
        errorColor = ContextCompat.getColor(context, R.color.error);
    }

    private void print(String str, int num, boolean Vertical, int color, int width) {
        paint.setColor(color);
        paint.setTextSize(width);
        if (Vertical) {
            canvas.drawText(str, leftMargin + num * columnWidth - TEXT_LEFT_MARGIN / 2,
                    topMargin + maxY + (int) (TEXT_TOP_MARGIN * 2.5), paint);
        } else {
            canvas.drawText(str, leftMargin - TEXT_LEFT_MARGIN,
                    topMargin + num * rowHeight + TEXT_TOP_MARGIN, paint);
        }
    }

    private void drawLine(int num, boolean Vertical, int color, int width) {
        paint.setColor(color);
        paint.setStrokeWidth(width);
        if (Vertical) {
            canvas.drawLine(leftMargin + num * columnWidth, topMargin,
                    leftMargin + num * columnWidth, topMargin + maxY, paint);
        } else {
            canvas.drawLine(leftMargin, topMargin + num * rowHeight, leftMargin + maxX,
                    topMargin + num * rowHeight, paint);
        }
    }

    private void drawDot(int column, int row, int color, int width) {
        paint.setColor(color);
        paint.setStrokeWidth(dotWidth);
        canvas.drawCircle(leftMargin + column * columnWidth, topMargin + row * rowHeight,
                width, paint);
    }

    private void drawBar(int color) {
        Rect r = new Rect(leftMargin, topMargin, leftMargin + (COLUMNS - 1) * columnWidth, topMargin + (ROWS - 1) * rowHeight);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(r, paint);
    }

    private int getRow(int y) {
        y -= topMargin;
        y += rowHeight / 2;
        y /= rowHeight;
        return ROWS - 1 - y;
    }

    private int getColumn(int x) {
        x -= leftMargin;
        x += columnWidth / 2;
        x /= columnWidth;
        return x;
    }

}
