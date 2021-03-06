package com.nserdyuk.smartkid.tasks;

import android.content.Context;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Complexity;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Point;
import com.nserdyuk.smartkid.tasks.base.Bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

abstract class Grid2dBot extends Bot {
    private static final String TAG = Grid2dBot.class.getName();
    private static final Point START_POINT = new Point(-1, -1);
    private static final int SHORT_DELAY = 500;
    private static final int LONG_DELAY = 1500;

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;
    private final Complexity complexity;
    private final int examplesNum;
    private final int rows;
    private final int columns;
    private final Random random;
    private final List<Point> userList = new ArrayList<>();
    private int currentPoint;
    private int solved;
    private List<Point> generatedList = new ArrayList<>();


    Grid2dBot(Context context, int examplesNum, Complexity complexity, int rows, int columns) {
        super(TAG);
        greetingMsg = context.getResources().getString(R.string.greeting);
        rightAnswerMsg = context.getResources().getString(R.string.right_answer);
        wrongAnswerMsg = context.getResources().getString(R.string.wrong_answer);
        random = new Random();
        this.examplesNum = examplesNum;
        this.complexity = complexity;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    protected void onStart() {
        onMessage(START_POINT);
    }

    @Override
    protected void onMessage(Object o) {
        if (!(o instanceof Point)) {
            return;
        }

        if (START_POINT.equals(o)) {
            sendWithDelay(greetingMsg, LONG_DELAY);
            generatePoints();
            currentPoint = 0;
        } else {
            userList.add((Point) o);

            if (currentPoint == examplesNum) {
                currentPoint = 0;
                if (userList.equals(generatedList)) {
                    sendWithDelay(rightAnswerMsg, LONG_DELAY);
                    generatePoints();
                    solved += examplesNum;
                } else {
                    sendWithDelay(wrongAnswerMsg, LONG_DELAY);
                }
                send(Constants.GRID2D_CLEAR_POINTS);
                userList.clear();
            }
        }
        Point p = generatedList.get(currentPoint++);
        String msg = String.format(Locale.US,
                solved == 0 ? Constants.GRID2D_POINT_FORMAT
                        : Constants.GRID2D_POINT_FORMAT_EXAMPLES,
                p.getX(), p.getY(), solved);
        sendWithDelay(msg, SHORT_DELAY);
    }

    private void generatePoints() {
        generatedList.clear();

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
            for (int i = 0; i < columns; i++) {
                Point p = new Point(i, 0);
                generatedList.add(p);
            }

            for (int i = 1; i < rows; i++) {
                Point p = new Point(0, i);
                generatedList.add(p);
            }

            Collections.shuffle(generatedList);
            generatedList = generatedList.subList(0, examplesNum);
        }
    }

}
