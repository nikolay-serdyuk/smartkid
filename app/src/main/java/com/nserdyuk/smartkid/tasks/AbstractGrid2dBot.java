package com.nserdyuk.smartkid.tasks;

import android.content.Context;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Complexity;
import com.nserdyuk.smartkid.common.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class AbstractGrid2dBot extends AbstractBot {
    private static final String TAG = "AbstractGrid2dBot";
    private static final Point INVALID_POINT = new Point(-1, -1);
    private static final int SHORT_DELAY = 500;
    private static final int LONG_DELAY = 1500;

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;
    private final Complexity complexity;
    private final int examplesNum;
    private final List<Point> generatedList = new ArrayList<>();
    private final int rows;
    private final int columns;
    private final Random random;


    public AbstractGrid2dBot(Context context, int examplesNum, Complexity complexity, int rows, int columns) {
        super(TAG);
        greetingMsg = context.getResources().getString(R.string.greeting);
        rightAnswerMsg = context.getResources().getString(R.string.rightAnswer);
        wrongAnswerMsg = context.getResources().getString(R.string.wrongAnswer);
        random = new Random();
        this.examplesNum = examplesNum;
        this.complexity = complexity;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    protected void startBot() {
        process(INVALID_POINT);
    }

    @Override
    protected void process(Object o) {
        if (!(o instanceof Point)) {
            return;
        }

        if (INVALID_POINT.equals(o)) {
            send(greetingMsg);
            /*
            delay(LONG_DELAY);
            send("");
            delay(SHORT_DELAY);
            */
            generatePoints();
        }
    }

    private void generatePoints() {
        generatedList.clear();

        if (complexity == Complexity.HARD) {
            for (int i = 0; i < columns; i++) {
                Point p = new Point(i, 0);
                generatedList.add(p);
            }

            for (int i = 1; i < rows; i++) {
                Point p = new Point(0, i);
                generatedList.add(p);
            }
        }

        for (int i = 0; i < examplesNum; i++) {
            Point p;
            do {
                int x = random.nextInt(columns);
                int y = random.nextInt(rows);
                p = new Point(x, y);
            } while (generatedList.contains(p));
            generatedList.add(p);
        }

        if (complexity == Complexity.HARD) {
            Collections.shuffle(generatedList);
        }
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
